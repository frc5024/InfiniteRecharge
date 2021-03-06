package frc.robot.subsystems;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.cscore.VideoSource;
import frc.lib5k.components.AutoCamera;
import frc.lib5k.components.SmartServo;
import frc.lib5k.components.motors.TalonHelper;
import frc.lib5k.components.sensors.HallEffect;
import frc.lib5k.simulation.wrappers.SimTalon;
import frc.lib5k.utils.RobotLogger;
import frc.robot.RobotConstants;

/**
 * Robot climber subsystem
 */
public class Climber extends SubsystemBase {
    private static RobotLogger logger = RobotLogger.getInstance();
    private static Climber s_instance = null;

    // "Pin" for releasing the climber
    private SmartServo m_releaseServo;

    // Motor for retracting climber
    private SimTalon m_liftMotor;

    // Climb camera
    private AutoCamera m_camera;

    // Hall effect sensors on the climbers
    private HallEffect m_lowHall;
    private HallEffect m_highHall;

    // Ripcord timer
    private Timer m_ripTimer = new Timer();

    /**
     * System states
     */
    private enum SystemState {
        SERVICE, // Service mode for use in the pits
        LOCKED, // Climber locked, and sensors disabled
        DEPLOYING, // Climber deploying
        RETRACTING, // Climber retracting to setpoint
    }

    /**
     * System positions
     */
    public enum Position {
        LEVEL, // High climb bar position
        RETRACTED, // Low climb bar position
        CURRENT, // Hold at current position
    }

    // System state tracker
    private SystemState m_state = SystemState.LOCKED;
    private SystemState m_lastState = SystemState.LOCKED;

    // System wanted position tracker
    private Position m_wantedPosition = Position.CURRENT;

    private Climber() {

        // Climber release
        m_releaseServo = new SmartServo(RobotConstants.Climber.SERVO_PWM);
        m_releaseServo.stop();

        addChild("Release", m_releaseServo);

        // Climb motor
        m_liftMotor = new SimTalon(RobotConstants.Climber.MOTOR_CONTROLLER_ID);

        // Low and High Hall sensors
        m_lowHall = new HallEffect(RobotConstants.Climber.LOW_HALL_ID);
        m_highHall = new HallEffect(RobotConstants.Climber.HIGH_HALL_ID);

        // Set up the camera
        m_camera = new AutoCamera("Climb camera", 0);
        m_camera.keepCameraAwake(true);
        m_camera.showCamera(false);

        // Disable the climb motor's brakes to allow easy servicing
        m_liftMotor.setNeutralMode(NeutralMode.Coast);

        TalonHelper.configCurrentLimit(m_liftMotor, 34, 32, 15, 0);
    }

    /**
     * Get the instance of Climber
     * 
     * @return Climber Instance
     */
    public static Climber getInstance() {
        if (s_instance == null) {
            s_instance = new Climber();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

        // Determine if this state is new
        boolean isNewState = false;
        if (m_state != m_lastState) {
            isNewState = true;
        }

        /* Handle states */
        switch (m_state) {
            case LOCKED:
                handleLocked(isNewState);
                break;
            case DEPLOYING:
                handleDeploy(isNewState);
                break;
            case RETRACTING:
                handleRetract(isNewState);
                break;
            case SERVICE:
                handleService(isNewState);
                break;

        }

        // Set the last state
        m_lastState = m_state;

    }

    /**
     * Handle system locked state
     * 
     * @param isNew Is new state?
     */
    private void handleLocked(boolean isNew) {
        if (isNew) {

            // Retract safety pin
            m_releaseServo.stop();

            // Stop the motor
            m_liftMotor.setNeutralMode(NeutralMode.Brake);
            m_liftMotor.set(0.0);

            // Disable the camera
            m_camera.showCamera(false);
        }
    }

    /**
     * Handle climber deployment
     * 
     * @param isNew Is new state?
     */
    private void handleDeploy(boolean isNew) {
        if (isNew) {

            // Release the climber spring
            m_releaseServo.rip();

            // Reset the rip timer
            m_ripTimer.reset();
            m_ripTimer.start();

            // Disable the motor
            m_liftMotor.set(0.0);

            // Show the camera feed to the drivers
            m_camera.showCamera(true);
        }

        // If the timer runs out, we stop the ripcord
        if (m_ripTimer.hasElapsed(1.0)) {
            m_releaseServo.stop();
        }

    }

    /**
     * Handle climber retraction
     * 
     * @param isNew
     */
    public void handleRetract(boolean isNew) {

        // If the timer runs out, we stop the ripcord
        if (m_ripTimer.hasElapsed(1.0)) {
            m_releaseServo.stop();
        }

        // Read the current position
        Position current = getPosition();

        // If we are not at our desired position, get there
        if (m_wantedPosition != current && m_wantedPosition != Position.CURRENT) {

            // Pull down the climber
            m_liftMotor.set(-1.0);

        } else {
            // Hold the motor
            m_liftMotor.set(0.0);
        }

    }

    /**
     * Handle service mode
     * 
     * @param isNew Is new state?
     */
    public void handleService(boolean isNew) {
        if (isNew) {

            // Stop, and disable the motor
            m_liftMotor.set(0.0);
            m_liftMotor.setNeutralMode(NeutralMode.Coast);

            // Enable the camera
            m_camera.showCamera(true);

            // Retract safety pin
            m_releaseServo.stop();
        }
    }

    /**
     * Lock the climber
     */
    public void lock() {
        logger.log("Climber", "Locked");
        m_state = SystemState.LOCKED;

    }

    /**
     * Unlock the climber
     */
    public void unlock() {
        logger.log("Climber", "Unlocked");
        m_state = SystemState.DEPLOYING;
    }

    /**
     * Go into service mode
     */
    public void service() {
        logger.log("Climber", "In service mode");
        m_state = SystemState.SERVICE;

    }

    /**
     * Set the desired position for the climber. "CURRENT" will hold it in place
     * 
     * @param position Desired position
     */
    public void setPosition(Position position) {
        this.m_wantedPosition = position;
        this.m_state = SystemState.RETRACTING;
    }

    /**
     * Get the climber's current position
     * 
     * @return Climber's position
     */
    public Position getPosition() {
        /**
         * This should work as follows:
         * 
         * If either hall sensor is tripped, return the corresponding position,
         * otherwise, return CURRENT
         */

        if (m_highHall.get()) {
            return Position.LEVEL;
        } else if (m_lowHall.get()) {
            return Position.RETRACTED;
        } else {
            return Position.CURRENT;
        }
    }

    /**
     * Get the climber camera feed
     * 
     * @return Camera feed
     */
    public VideoSource getCameraFeed() {
        return m_camera.getFeed();
    }

}