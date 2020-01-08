package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.drive.DifferentialDriveCalculation;
import frc.lib5k.components.drive.InputUtils;
import frc.lib5k.components.drive.InputUtils.ScalingMode;
import frc.lib5k.components.gyroscopes.NavX;
import frc.lib5k.components.motors.TalonSRXCollection;
import frc.lib5k.components.sensors.EncoderBase;
import frc.lib5k.interfaces.Loggable;
import frc.lib5k.utils.RobotLogger;
import frc.robot.RobotConstants;
import frc.lib5k.kinematics.DriveSignal;

/**
 * The DriveTrain handles all robot movement.
 */
public class DriveTrain extends SubsystemBase implements Loggable {
    private static RobotLogger logger = RobotLogger.getInstance();
    private static DriveTrain s_instance = null;

    /*
     * Drive Control Modes
     */
    public enum DriveMode {
        OPEN_LOOP, // Open loop control (percent output control)
        VOLTAGE // Voltage control

    }

    

    // Keep track of the current DriveMode
    private DriveMode m_currentDriveMode = DriveMode.OPEN_LOOP;

    private DriveSignal m_currentSignal;

    /**
     * Left side gearbox.
     */
    private TalonSRXCollection m_leftGearbox;

    /**
     * Right side gearbox.
     */
    private TalonSRXCollection m_rightGearbox;

    /**
     * Left side encoder
     */
    private EncoderBase m_leftEncoder;

    /**
     * Right side encoder
     */
    private EncoderBase m_rightEncoder;

    /**
     * Odometry object for tracking robot position
     */
    private DifferentialDriveOdometry m_odometry;

    /**
     * Pose2d for keeping track of robot position on the field
     */
    private Pose2d m_robotPose = new Pose2d();

    private double m_lastLeftMeters, m_lastRightMeters, m_leftMPS, m_rightMPS = 0;


    /**
     * DriveTrain constructor.
     * 
     * All subsystem components should be created and configured here.
     */
    private DriveTrain() {

        // Construct both gearboxes
        m_leftGearbox = new TalonSRXCollection(
                new WPI_TalonSRX(RobotConstants.DriveTrain.MotorControllers.LEFT_FRONT_TALON),
                new WPI_TalonSRX(RobotConstants.DriveTrain.MotorControllers.LEFT_REAR_TALON));
        m_rightGearbox = new TalonSRXCollection(
                new WPI_TalonSRX(RobotConstants.DriveTrain.MotorControllers.RIGHT_FRONT_TALON),
                new WPI_TalonSRX(RobotConstants.DriveTrain.MotorControllers.RIGHT_REAR_TALON));

        // Configure the gearboxes
        m_leftGearbox.setCurrentLimit(RobotConstants.DriveTrain.CurrentLimits.PEAK_AMPS,
                RobotConstants.DriveTrain.CurrentLimits.TIMEOUT_MS, RobotConstants.DriveTrain.CurrentLimits.HOLD_AMPS,
                0);
        m_rightGearbox.setCurrentLimit(RobotConstants.DriveTrain.CurrentLimits.PEAK_AMPS,
                RobotConstants.DriveTrain.CurrentLimits.TIMEOUT_MS, RobotConstants.DriveTrain.CurrentLimits.HOLD_AMPS,
                0);

        // Disable motor safety
        m_leftGearbox.setMasterMotorSafety(false);
        m_rightGearbox.setMasterMotorSafety(false);

        // Set motor inversions
        m_leftGearbox.setInverted(RobotConstants.DriveTrain.MotorControllers.LEFT_SIDE_INVERTED);
        m_rightGearbox.setInverted(RobotConstants.DriveTrain.MotorControllers.RIGHT_SIDE_INVERTED);

        // Get encoders
        m_leftEncoder = m_leftGearbox.getEncoder(RobotConstants.DriveTrain.Encoders.LEFT_ENCODER_SLOT,
                RobotConstants.DriveTrain.Encoders.LEFT_SENSOR_PHASE);
        m_rightEncoder = m_rightGearbox.getEncoder(RobotConstants.DriveTrain.Encoders.RIGHT_ENCODER_SLOT,
                RobotConstants.DriveTrain.Encoders.RIGHT_SENSOR_PHASE);

        // Set up encoder simulation if robot is not real
        if (RobotBase.isSimulation()) {
            m_leftEncoder.initSimulationDevice(m_leftGearbox, RobotConstants.DriveTrain.Encoders.PULSES_PER_REVOLUTION,
                    RobotConstants.DriveTrain.Measurements.GEAR_RATIO,
                    RobotConstants.DriveTrain.Measurements.MOTOR_MAX_RPM);

            m_rightEncoder.initSimulationDevice(m_rightGearbox,
                    RobotConstants.DriveTrain.Encoders.PULSES_PER_REVOLUTION,
                    RobotConstants.DriveTrain.Measurements.GEAR_RATIO,
                    RobotConstants.DriveTrain.Measurements.MOTOR_MAX_RPM);
        }

        // Create odometry object
        m_odometry = new DifferentialDriveOdometry(NavX.getInstance().getRotation());

    }

    /**
     * Get the DriveTrain instance.
     * 
     * @return DriveTrain instance
     */
    public static DriveTrain getInstance() {
        if (s_instance == null) {
            s_instance = new DriveTrain();
        }

        return s_instance;
    }

    /**
     * Subsystem-specific tasks that must be run once per 20ms must be placed in
     * this method.
     */
    @Override
    public void periodic() {

        /* Handle motor outputs for each mode */
        switch (m_currentDriveMode) {
        case OPEN_LOOP:
            // Sets the left and right gearbox
            m_leftGearbox.set(m_currentSignal.getL());
            m_rightGearbox.set(m_currentSignal.getR());
            break;
        case VOLTAGE:
            // ets the left and right gearbox
            m_leftGearbox.setVoltage(m_currentSignal.getL());
            m_rightGearbox.set(m_currentSignal.getR());
            break;
        default:
            // This code should never run, but if it does, we set the mode to OPEN_LOOP, and
            // the outputs to 0
            setOpenLoop(new DriveSignal(0, 0));
        }

        /* Handle encoder updates */
        m_leftEncoder.update();
        m_rightEncoder.update();

        // Determine wheel speeds
        m_leftMPS = (getLeftMeters()- m_lastLeftMeters) * 50;
        m_rightMPS = (getRightMeters()- m_lastRightMeters) * 50;

        // set last distances
        m_lastLeftMeters = getLeftMeters();
        m_lastRightMeters = getRightMeters();

        /* Handle odometry updates */

        // Get the current robot heading
        Rotation2d heading = Rotation2d.fromDegrees(NavX.getInstance().getHeading());

        // Calculate the robot pose
        m_robotPose = m_odometry.update(heading, getLeftMeters(), getRightMeters());

    }

    /**
     * Open-loop control the drivebase with a desired speed and rotation factor.
     * 
     * @param speed    Desired speed percentage [-1.0-1.0]
     * @param rotation Desired rotation factor [-1.0-1.0]
     */
    public void drive(double speed, double rotation) {
        // Square inputs
        speed = InputUtils.scale(speed, ScalingMode.SQUARED);
        rotation = InputUtils.scale(rotation, ScalingMode.SQUARED);

        // Compute a DriveSignal from inputs
        DriveSignal signal = DifferentialDriveCalculation.semiConstCurve(speed, rotation);

        // Set the signal
        setOpenLoop(signal);

    }

    /**
     * Set the Open loop control signal. The values of this signal should be in the
     * rage of [-1.0-1.0]
     * 
     * @param signal Open loop signal
     */
    public void setOpenLoop(DriveSignal signal) {

        // Force-set the mode if not already set
        if (m_currentDriveMode != DriveMode.OPEN_LOOP) {

            // Enable motor brakes
            setBrakes(true);

            // Log the state change
            logger.log("DriveTrain", String.format("Set control mode to OPEN_LOOP with signal: %s", signal.toString()));

            // Set the new state
            m_currentDriveMode = DriveMode.OPEN_LOOP;
        }

        // Set the current DriveTrain signal
        m_currentSignal = signal;
    }

    /**
     * Set the Open loop control signal. The values of this signal should be in the
     * rage of [-12.0-12.0]
     * 
     * @param signal Voltage signal
     */
    public void setVoltage(DriveSignal signal) {

        // Force-set the mode if not already set
        if (m_currentDriveMode != DriveMode.VOLTAGE) {

            // Enable motor brakes
            setBrakes(false);

            // Log the state change
            logger.log("DriveTrain", String.format("Set control mode to VOLTAGE with signal: %s", signal.toString()));

            // Set the new state
            m_currentDriveMode = DriveMode.VOLTAGE;
        }

        // Set the current DriveTrain signal
        m_currentSignal = signal;
    }

    /**
     * Sets Voltage Signal
     * 
     * @param left
     * @param right
     */
    public void setVoltage(double left, double right) {
        setVoltage(new DriveSignal(left, right));
    }

    /**
     * Set the number of seconds the drivebase should take to get to full power
     * 
     * @param rampRate Ramp rate
     */
    public void setRampRate(double rampRate) {
        logger.log("DriveTrain", String.format("Setting DriveTrain ramp rate to: %.2f", rampRate));

        m_leftGearbox.setRampRate(rampRate);
        m_rightGearbox.setRampRate(rampRate);
    }

    /**
     * Stop the drivetrain
     */
    public void stop() {
        logger.log("DriveTrain", "Stopping DriveTrain");

        setOpenLoop(new DriveSignal(0, 0));
    }

    /**
     * Set the motor brakes. When enabled, the robot will automatically try to stay
     * in place (resisting pushing)
     * 
     * @param brakesApplied Should the brakes be applied?
     */
    public void setBrakes(boolean brakesApplied) {
        logger.log("DriveTrain", String.format("%s brakes", (brakesApplied) ? "Enabling" : "Disabling"));

        m_leftGearbox.setNeutralMode((brakesApplied) ? NeutralMode.Brake : NeutralMode.Coast);
        m_rightGearbox.setNeutralMode((brakesApplied) ? NeutralMode.Brake : NeutralMode.Coast);
    }

    /**
     * Get the left side distance traveled in meters
     * 
     * @return Left distance
     */
    public double getLeftMeters() {

        return m_leftEncoder.getMeters(RobotConstants.DriveTrain.Encoders.PULSES_PER_REVOLUTION,
                RobotConstants.DriveTrain.Measurements.WHEEL_CIRCUMFERENCE);

    }

    /**
     * Get the right side distance traveled in meters
     * 
     * @return Right distance
     */
    public double getRightMeters() {

        return m_rightEncoder.getMeters(RobotConstants.DriveTrain.Encoders.PULSES_PER_REVOLUTION,
                RobotConstants.DriveTrain.Measurements.WHEEL_CIRCUMFERENCE);

    }

    /**
     * Get the robot's current field-relative position
     * 
     * @return Robot position
     */
    public Pose2d getPosition() {
        return m_robotPose;
    }

    /**
     * Force-set the robot's position
     * 
     * @param pose Robot pose
     */
    public void setPosition(Pose2d pose) {
        logger.log("DriveTrain", String.format("Set odometry position to: %s", pose.toString()));

        m_odometry.resetPosition(pose, NavX.getInstance().getRotation());
    }

    public DifferentialDriveWheelSpeeds getWheelSpeeds(){
        return new DifferentialDriveWheelSpeeds(m_leftMPS, m_rightMPS);
    }

    @Override
    public void logStatus() {
        logger.log("DriveTrain",
                String.format("Pose: %s, Signal: %s", getPosition().toString(), m_currentSignal.toString()));

    }

    @Override
    public void updateTelemetry() {

        SmartDashboard.putString("[DriveTrain] pose", getPosition().toString());

    }
}