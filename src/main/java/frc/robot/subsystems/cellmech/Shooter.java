package frc.robot.subsystems.cellmech;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.ControlType;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.roborio.RR_HAL;
import frc.lib5k.simulation.wrappers.SimSparkMax;
import frc.lib5k.utils.Mathutils;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.telemetry.FlywheelTuner;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;
import frc.robot.vision.Limelight2;
import frc.robot.vision.Limelight2.LEDMode;

/**
 * Robot Shooter subsystem
 */
public class Shooter extends SubsystemBase {
    public static Shooter s_instance = null;

    // Wind-up time
    private long windUpStartTime, windUpEndTime, windUpTotalTime;

    // Optimal Position
    boolean inPosition;

    // Logger
    RobotLogger logger = RobotLogger.getInstance();

    /**
     * Shooter motor controller
     */
    private SimSparkMax m_motorController;

    /**
     * System states
     */
    private enum SystemState {
        IDLE, // System Idle
        SPIN_UP, // Flywheel spinning up
        SPIN_DOWN, // Flywheel spinning down
        HOLD, // Flywheel holding a speed
        MANUAL, // Manual voltage control
        UNJAM, // Flywheel unjamming

    }

    /**
     * Tracker for shooter system state.
     */
    private SystemState m_systemState = SystemState.IDLE;
    private SystemState m_lastState = null;

    // Output value. Depending on mode, this will become different things
    private double output = 0.0;

    // Velocity calculating encoder
    private CANPIDController m_motorPID;
    private CANEncoder m_motorEncoder;

    // Limelight
    private Limelight2 m_limelight;

    // Telemetry object for tuning the flywheel
    private FlywheelTuner m_tuner;

    // Autonomous flywheel velocity tracker
    private double autonOutputGoal = RobotConstants.Shooter.DEFAULT_VELOCITY;

    private Shooter() {

        // Create Limelight
        m_limelight = Limelight2.getInstance();

        // Create and configure motor
        m_motorController = new SimSparkMax(RobotConstants.Shooter.MOTOR_ID, MotorType.kBrushless);
        m_motorController.restoreFactoryDefaults();
        m_motorEncoder = m_motorController.getEncoder();
        m_motorPID = m_motorController.getPIDController();

        m_motorPID.setP(RobotConstants.Shooter.kPVel);
        m_motorPID.setI(RobotConstants.Shooter.kIVel);
        m_motorPID.setD(RobotConstants.Shooter.kDVel);
        m_motorPID.setIZone(RobotConstants.Shooter.kIz);
        m_motorPID.setFF(RobotConstants.Shooter.kFF);
        m_motorPID.setOutputRange(-1.0, 1.0);

        // Stop the motor
        m_motorPID.setReference(0.0, ControlType.kVelocity);

        addChild("SimSparkMax", m_motorController);

        // Configure the tuner
        m_tuner = new FlywheelTuner("Shooter", m_motorEncoder::getVelocity);
        m_tuner.setEnabled(RobotConstants.ENABLE_PID_TUNING_OUTPUTS);

    }

    /**
     * Get the instance of Shooter
     * 
     * @return Shooter Instance
     */
    public static Shooter getInstance() {
        if (s_instance == null) {
            s_instance = new Shooter();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

        // Determine if this state is new
        boolean isNewState = false;
        if (m_systemState != m_lastState) {
            isNewState = true;
        }

        // Set the last state
        m_lastState = m_systemState;

        /* Handle states */
        switch (m_systemState) {
            case IDLE:

                // Handle the idle state
                handleIdle(isNewState);
                break;

            case SPIN_UP:

                // Handle the spinup state
                handleSpinUp(isNewState);
                break;

            case SPIN_DOWN:

                // Handle the spindown state
                handleSpinDown(isNewState);
                break;

            case HOLD:

                // Handle holding motor at velocity
                handleHold(isNewState);
                break;

            case UNJAM:

                // Handle unjamming
                handleUnjam(isNewState);
                break;

            default:

                // Set the system to spin down in case it is spinning and loses track of state.
                m_systemState = SystemState.SPIN_DOWN;

        }

        // Update the tuner
        m_tuner.update();

    }

    /**
     * Handle system idle state
     * 
     * @param newState Is this state new?
     */
    private void handleIdle(boolean newState) {

        if (newState) {
            logger.log("Shooter", "System idle");

            // Force-set the motor to 0.0V
            m_motorController.set(0.0);

            // Force-set output
            output = 0.0;
        }

    }

    /**
     * Handle flywheel spinup using PID control to get it near speed
     * 
     * @param newState Is this state new?
     */
    private void handleSpinUp(boolean newState) {

        if (newState) {
            logger.log("Shooter", String.format("Spinning up to: %.2f", output));

            // Reset wind-up
            windUpStartTime = System.currentTimeMillis();

            m_motorController.setOpenLoopRampRate(0);

            // Configure the spinup controller
            sendMotorCommand(output);
            m_tuner.setSetpoint(output);

            // Use Limelight
            m_limelight.use(true);

            // Enable telemetry
            m_tuner.enableLogging(true);
        }

        // Log the speeds
        System.out.println(
                String.format("Setpoint: %.1f, Curent: %.1f", output, m_motorController.getEncoder().getVelocity()));

        // Switch to HOLD state if spinup complete
        if (atRPMSetpoint()) {

            // Move to next state
            this.m_systemState = SystemState.HOLD;
        }
    }

    /**
     * Handle flywheel spindown to 0 RPM
     * 
     * @param newState Is this state new?
     */
    private void handleSpinDown(boolean newState) {

        if (newState) {

            // Turn off the LEDs
            m_limelight.setLED(LEDMode.OFF);
            m_limelight.use(false);

            m_motorController.setClosedLoopRampRate(1.0);
            m_motorController.set(0);

            // Disable telemetry
            m_tuner.enableLogging(false);
        }

        m_systemState = SystemState.IDLE;

    }

    /**
     * Handle holding the flywheel velocity
     * 
     * @param newState Is this state new?
     */
    private void handleHold(boolean newState) {

        if (newState) {

            windUpEndTime = System.currentTimeMillis();
            windUpTotalTime = windUpEndTime - windUpStartTime;
            logger.log("Shooter", "Holding. Spin-Up took " + (windUpTotalTime / 1000.0) + " seconds");

            m_tuner.setSetpoint(output);

        }

        // If we are under-RPM, spin up more
        if (!atRPMSetpoint()) {
            m_systemState = SystemState.SPIN_UP;
        }

    }

    /**
     * Handle unjamming the power cells
     * 
     * @param newState Is this state new?
     */
    public void handleUnjam(boolean newState) {
        if (newState) {
            // TODO
        }
    }

    /**
     * Roughly convert RPM to voltage, and send to motor.
     * 
     * @param desiredRPM Desired RPM of motor
     */
    private void sendMotorCommand(double desiredRPM) {

        m_motorPID.setReference(output, ControlType.kVelocity);

    }

    /**
     * Set shooter output as a percentage of the max output
     * 
     * @param val Percent output
     */
    public void setOutputPercent(double val) {

        // Switch to "Spin Up" mode. If we are already at, or above the setpoint, it
        // will automatically move to the next state.
        m_systemState = SystemState.SPIN_UP;

        // Set the desired voltage
        output = val * RobotConstants.Shooter.MOTOR_MAX_RPM;

    }

    /**
     * Set the shooter velocity
     * 
     * @param val Velocity RPM
     */
    public void setVelocity(double val) {
        m_systemState = SystemState.SPIN_UP;
        output = val;
    }

    public void stop() {
        logger.log("Shooter", "Stop requested");

        // Set the mode to spin down
        m_systemState = SystemState.SPIN_DOWN;

    }

    /**
     * Check if the flywheel has spun up
     * 
     * @return Has spun up?
     */
    public boolean isSpunUp() {
        return m_systemState == SystemState.HOLD;
    }

    /**
     * Check if the flywheel is currently at it's RPM setpoint.
     * 
     * @return At setpoint?
     */
    private boolean atRPMSetpoint() {
        return Mathutils.epsilonEquals(m_motorController.getEncoder().getVelocity(),
                Mathutils.clamp(this.output, 0, RobotConstants.Shooter.MOTOR_MAX_RPM),
                RobotConstants.Shooter.RPM_EPSILON);
    }

    public double getOutput() {
        return m_motorController.get();
    }

    /**
     * @param inPosition Is the bot in position to score?
     */
    public void setInPosition(boolean inPosition) {
        this.inPosition = inPosition;
    }

    /**
     * @return Is the bot in position to score?
     */
    public boolean isInPosition() {

        // Check if drivetrain has been moved off-target since alignment
        if (DriveTrain.getInstance().alignmentLost())
            inPosition = false;
        return inPosition;
    }

    /**
     * Set the static shooter speed for use during autonomous. This removes possible
     * vision error in a "safe" environment.
     * 
     * @param rpm Output RPM
     */
    public void setAutonomousOutput(double rpm) {

        // Clamp output to the possible outputs for the motor
        rpm = Mathutils.clamp(rpm, 0.0, RobotConstants.Shooter.MOTOR_MAX_RPM);

        // Log
        logger.log("Shooter", String.format("Set autonomous output velocity goal to: %.2fRPM", rpm));

        // Set
        autonOutputGoal = rpm;
    }

    /**
     * 
     * @return Desired flywheel velocity in RPM based on distance to target.
     */
    public double getVelocityFromLimelight() {

        // During autonomous, we force the shooter to use the auton goal.
        if (DriverStation.getInstance().isAutonomous()) {
            return autonOutputGoal;
        }

        // If there is no target found, default to a constant shooting point
        if (!m_limelight.hasTarget()) {
            return RobotConstants.Shooter.DEFAULT_VELOCITY;
        }

        // TODO: Temp
        return RobotConstants.Shooter.DEFAULT_VELOCITY;

        // // Get distance to target
        // double angleToTarget = m_limelight.getTarget().ty;

        // // d = (h2-h1) / tan(a1+a2)
        // double distance = (RobotConstants.Shooter.TARGET_HEIGHT -
        // RobotConstants.Shooter.LIMELIGHT_HEIGHT)
        // / Math.tan(RobotConstants.Shooter.LIMELIGHT_MOUNT_ANGLE + angleToTarget);
        // RobotLogger.getInstance().log(
        // "[LIMELIGHT]: Distance to target calculated. Distance is " +
        // String.format("%.4f", distance) + "m.");

        // // Calculate necessary linear velocity of ball
        // double ballVel = (Math.sqrt(9.81) * Math.sqrt(distance) * Math.sqrt(
        // (Math.tan(RobotConstants.Shooter.LAUNCH_ANGLE) *
        // Math.tan(RobotConstants.Shooter.LAUNCH_ANGLE)) + 1))
        // / Math.sqrt(2 * Math.tan(RobotConstants.Shooter.LAUNCH_ANGLE)
        // - (2 * 9.81 * RobotConstants.Shooter.TARGET_HEIGHT) / distance);

        // // Tangential velocity of flywheel, in RPM
        // double wheelVel = (ballVel * 2) * RobotConstants.Shooter.RPM_PER_MPS;
        // RobotLogger.getInstance().log("[LIMELIGHT]: Desired velocity calculated.
        // Desired velocity is "
        // + String.format("%.4f", wheelVel) + "RPM.");
        // return wheelVel;
    }

}