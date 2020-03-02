package frc.lib5k.drivebase.chassis.differential;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.drive.IDifferentialDrivebase;
import frc.lib5k.components.gyroscopes.IGyroscope;
import frc.lib5k.drivebase.IDrivebase;
import frc.lib5k.drivebase.modules.TankTrack;

/**
 * A common backend for all differential drivebases.
 */
public abstract class DifferentialDrivebase extends SubsystemBase implements IDrivebase, IDifferentialDrivebase {

    // Tank tracks
    private TankTrack leftTrack;
    private TankTrack rightTrack;

    // Gyroscope
    private IGyroscope gyro;

    // Localization
    private DifferentialDriveOdometry odometry;

    // Chassis position goal
    private Pose2d goalPose = new Pose2d();

    // Chassis velocity goal
    private DifferentialDriveWheelSpeeds goalVelocity = new DifferentialDriveWheelSpeeds(0.0, 0.0);

    // Control modes
    private enum ControlMode {
        POSITION, TWIST, VELOCITY
    }

    private ControlMode mode = ControlMode.VELOCITY;

    public DifferentialDrivebase(TankTrack left, TankTrack right, IGyroscope gyro) {

        // Set locals
        this.leftTrack = left;
        this.rightTrack = right;
        this.gyro = gyro;

        // Zero both tracks
        leftTrack.zero();
        rightTrack.zero();

        // Set up odometer
        this.odometry = new DifferentialDriveOdometry(gyro.getRotation());

    }

    @Override
    public void periodic() {

        // Handle control mode
        switch (mode) {
            case POSITION:
                handlePosition();
                break;
            case TWIST:
                handleTwist();
                break;
            case VELOCITY:
                handleVelocity();
                break;
            default:
                break;

        }
    }

    private void handlePosition() {

    }

    private void handleTwist() {

    }

    /**
     * Handle velocity control
     */
    private void handleVelocity() {

        // Send a velocity command to each track
        sendVelocityCommand(goalVelocity);
    }

    /**
     * Send a velocity command to both tracks
     * 
     * @param velocity Velocity goal
     */
    private void sendVelocityCommand(DifferentialDriveWheelSpeeds velocity) {

        // Ensure velocities are normalized
        velocity.normalize(Math.min(leftTrack.getMaxMPS(), rightTrack.getMaxMPS()));

        // Set left & right
        leftTrack.setVelocity(velocity.leftMetersPerSecond);
        rightTrack.setVelocity(velocity.rightMetersPerSecond);
    }

    /**
     * Set the track velocities
     * 
     * @param velocity Velocity goals
     */
    public void setTrackVelocity(DifferentialDriveWheelSpeeds velocity) {

        // Set goal velocity
        goalVelocity = velocity;

        // Set control mode
        mode = ControlMode.VELOCITY;
    }

    @Override
    public void driveTo(Translation2d point) {

        // Set the goal pose
        goalPose = new Pose2d(point, new Rotation2d(0));

        // Set the control mode
        mode = ControlMode.POSITION;

    }

    @Override
    public void driveTowards(Translation2d point) {

        // Transform the point by the robot's pose
        double newX = getPoint().getX() + point.getX();
        double newY = getPoint().getY() + point.getY();

        // Drive to that point
        driveTo(new Translation2d(newX, newY));

    }

    @Override
    public void twist(Rotation2d angle) {

        // Set the goal pose to the current with a new angle
        goalPose = new Pose2d(getPoint(), angle);

        // Set the control mode
        mode = ControlMode.TWIST;
    }

    @Override
    public void face(Translation2d point) {

        // Find angle to face
        double theta = Math.atan2(point.getY() - getPoint().getY(), point.getX() - getPoint().getX());

        // Twist to that angle
        twist(new Rotation2d(theta));

    }

    @Override
    public double getLeftMeters() {
        return leftTrack.getMeters();
    }

    @Override
    public double getRightMeters() {
        return rightTrack.getMeters();
    }

    @Override
    public Pose2d getPosition() {
        return odometry.getPoseMeters();
    }

    @Override
    public Translation2d getPoint() {
        return getPosition().getTranslation();
    }

    @Override
    public Rotation2d getHeading() {
        return getPosition().getRotation();
    }

    @Override
    public void setPosition(Pose2d pose) {
        odometry.resetPosition(pose, gyro.getRotation());
    }

}