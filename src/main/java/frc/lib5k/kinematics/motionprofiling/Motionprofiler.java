package frc.lib5k.kinematics.motionprofiling;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.Notifier;
import frc.lib5k.components.sensors.EncoderBase;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.kinematics.PIDProfile;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.followers.EncoderFollower;
import jaci.pathfinder.modifiers.TankModifier;

public class Motionprofiler {

    /**
     * A collection of motor data
     */
    public class MotionOutput {
        public DriveSignal signal;
        public boolean finished;

        public MotionOutput(DriveSignal signal, boolean finished) {
            this.signal = signal;
            this.finished = finished;
        }

        public void reset() {
            signal.setL(0.0);
            signal.setR(0.0);
            finished = true;
        }
    }

    // Profile objects
    private MotionProfile m_profile;
    private PIDProfile m_pidProfile;
    private Notifier m_notifier;

    // EncoderFollowers
    private EncoderFollower m_leftFollower;
    private EncoderFollower m_rightFollower;

    // Robot parameters
    private double m_tpr, m_wheelDiameter;
    private double m_wheelbaseWidth;

    // Encoders
    private EncoderBase m_leftEncoder;
    private EncoderBase m_rightEncoder;

    private MotionOutput m_output;
    private DoubleSupplier m_gyroSupplier;

    /**
     * Create a MotionProfiler
     * 
     * @param profile        {@link MotionProfile} for robot to follow
     * @param pid_profile    {@link PIDProfile} defining PID Gains for motion
     *                       profiling
     * @param leftEncoder    Robot's left drivetrain encoder (as an
     *                       {@link EncoderBase})
     * @param rightEncoder   Robot's right drivetrain encoder (as an
     *                       {@link EncoderBase})
     * @param ticks_per_rev  Number of encoder ticks per wheel revolution
     * @param wheel_diameter Wheel diameter in CM
     * @param wheelbaseWidth Width of wheelbase in Meters
     * @param gyroSupplier   Method for retrieving gyro angle (This is any method
     *                       that can return a double, for a NavX, this would be
     *                       something like: AHRS::getAngle)
     */
    public Motionprofiler(MotionProfile profile, PIDProfile pid_profile, EncoderBase leftEncoder,
            EncoderBase rightEncoder, double ticks_per_rev, double wheel_diameter, double wheelbaseWidth,
            DoubleSupplier gyroSupplier) {

        // Set locals
        this.m_profile = profile;
        this.m_pidProfile = pid_profile;
        this.m_output = new MotionOutput(new DriveSignal(0.0, 0.0), true);
        this.m_tpr = ticks_per_rev;

        this.m_wheelbaseWidth = wheelbaseWidth;
        this.m_gyroSupplier = gyroSupplier;

        // Convert to meters
        this.m_wheelDiameter = wheel_diameter / 100;

        // Set encoders
        m_leftEncoder = leftEncoder;
        m_rightEncoder = rightEncoder;

        m_notifier = new Notifier(this::update);
    }

    /**
     * Start the MotionProfiler thread. This will generate the {@link MotionProfile}
     * if it nas not yet been generated. WARNING: If not pre-generated, there will
     * be a pause while the robot computes the path.
     * 
     * Encoders are also configured here
     */
    public void start() {
        // Get the motion trajectory
        Trajectory trajectory = m_profile.getTrajectoryOrGenerate();

        // Modify the trajectory to match a tank drive
        TankModifier modifier = new TankModifier(trajectory).modify(m_wheelbaseWidth);

        // Set the EncoderFollowers
        m_leftFollower = new EncoderFollower(modifier.getLeftTrajectory());
        m_rightFollower = new EncoderFollower(modifier.getRightTrajectory());

        // Handle encoder flipping
        int leftTicks = (!m_profile.getReversed()) ? m_leftEncoder.getRawTicks() : m_rightEncoder.getRawTicks();
        int rightTicks = (!m_profile.getReversed()) ? m_rightEncoder.getRawTicks() : m_rightEncoder.getRawTicks();

        // Configure the encoders
        m_leftFollower.configureEncoder(leftTicks, (int) Math.round(m_tpr), m_wheelDiameter);
        m_rightFollower.configureEncoder(rightTicks, (int) Math.round(m_tpr), m_wheelDiameter);

        // Configure PID for each side
        m_leftFollower.configurePIDVA(m_pidProfile.kp, m_pidProfile.ki, m_pidProfile.kd,
                1 / m_profile.getConstraints().getMaxVelocity(), 0.0);
        m_rightFollower.configurePIDVA(m_pidProfile.kp, m_pidProfile.ki, m_pidProfile.kd,
                1 / m_profile.getConstraints().getMaxVelocity(), 0.0);

        // Start the notifier
        m_notifier.startPeriodic(m_profile.getTrajectoryOrGenerate().get(0).dt);
    }

    /**
     * Stop the MotionProfiler thread without resetting the followers.
     * 
     * This probably should not be used? Has not been fully tested
     */
    public void pause() {
        // Stop the notifier
        m_notifier.stop();

        // Reset the output to stop the robot
        m_output.reset();
    }

    /**
     * Stop the MotionProfiler thread, and reset the MotionProfile
     */
    public void stop() {
        // Pause the thread
        pause();

        // Reset the Followers
        m_leftFollower.reset();
        m_rightFollower.reset();
    }

    /**
     * Calculate path, and update the Output. This is called by the Notifier
     */
    private void update() {
        // Calculate Left and Right motor speeds
        // Handle reversed path
        double l, r;
        if (!m_profile.getReversed()) {
            l = m_leftFollower.calculate(m_leftEncoder.getRawTicks());
            r = m_rightFollower.calculate(m_rightEncoder.getRawTicks());
        } else {
            l = m_leftFollower.calculate(-m_rightEncoder.getRawTicks());
            r = m_rightFollower.calculate(-m_leftEncoder.getRawTicks());
        }

        // Read Robot gyro, and desired gyro
        double heading = m_gyroSupplier.getAsDouble();
        double desiredHeading = Pathfinder.r2d(m_leftFollower.getHeading());

        // Find wrapped difference between angles
        double angleDiff = Pathfinder.boundHalfDegrees(desiredHeading - heading);
        angleDiff = angleDiff % 360.0;
        if (Math.abs(angleDiff) > 180.0) {
            angleDiff = (angleDiff > 0) ? angleDiff - 360 : angleDiff + 360;
        }

        // Determine turn modifier
        double turn = 0.8 * (-1.0 / 80.0) * angleDiff;

        // Define outputs
        double leftOut, rightOut;

        // Handle reversed path
        if (!m_profile.getReversed()) {
            leftOut = l + turn;
            rightOut = r - turn;
        } else {
            leftOut = -l + turn;
            rightOut = -r - turn;
        }

        // Set the output
        m_output.signal.setL(leftOut);
        m_output.signal.setR(rightOut);
        m_output.finished = m_leftFollower.isFinished() || m_rightFollower.isFinished();

    }

    /**
     * Get the generated motor output info
     * 
     * @return MotionOutput for drivetrain
     */
    public MotionOutput getOutput() {
        return m_output;
    }
}