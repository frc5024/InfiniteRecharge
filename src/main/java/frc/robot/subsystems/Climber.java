package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.lib5k.components.AutoCamera;
import frc.lib5k.components.pneumatics.LazySolenoid;
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
    private LazySolenoid m_releasePin;

    // Motor for retracting climber
    private SimTalon m_liftMotor;

    // Climb camera
    private AutoCamera m_camera;

    // Hall effect sensors on the climbers
    private DigitalInput m_lowHall;
    private DigitalInput m_highHall;

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
        HIGH_BAR, // High climb bar position
        LOW_BAR, // Low climb bar position
        CURRENT, // Hold at current position
    }

    // System state tracker
    private SystemState m_state = SystemState.LOCKED;
    private SystemState m_lastState = SystemState.LOCKED;

    // System wanted position tracker
    private Position m_wantedPosition = Position.CURRENT;

    private Climber() {

        // Climber release
        m_releasePin = new LazySolenoid(RobotConstants.Pneumatics.PCM_CAN_ID,
                RobotConstants.Climber.PIN_RELEASE_SOLENOID);

        // Climb motor
        m_liftMotor = new SimTalon(RobotConstants.Climber.MOTOR_CONTROLLER_ID);

        /* Low and High Hall sensors */
        m_lowHall = new DigitalInput(RobotConstants.Climber.LOW_HALL_ID);
        m_highHall = new DigitalInput(RobotConstants.Climber.HIGH_HALL_ID);

        // Set up the camera
        m_camera = new AutoCamera("Climb camera", 0);
        m_camera.keepCameraAwake(true);
        m_camera.showCamera(false);

        // Disable the climb motor's brakes to allow easy servicing
        m_liftMotor.setNeutralMode(NeutralMode.Coast);

        // Force a CAN message to the solenoid
        m_releasePin.set(false);
        m_releasePin.flush();
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

    }

    /**
     * Handle system locked state
     * 
     * @param isNew Is new state?
     */
    private void handleLocked(boolean isNew) {
        if (isNew) {

            // Retract safety pin
            m_releasePin.set(false);

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
            m_releasePin.set(true);

            // Disable the motor
            m_liftMotor.set(0.0);

            // Show the camera feed to the drivers
            m_camera.showCamera(true);
        }

    }

    /**
     * Handle climber retraction
     * 
     * @param isNew
     */
    public void handleRetract(boolean isNew) {

        // If we are not at our desired position, get there
        if (m_wantedPosition != getPosition()) {

            // Pull down the climber
            m_liftMotor.set(1.0);

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
            return Position.HIGH_BAR;
        } else if (m_lowHall.get()) {
            return Position.LOW_BAR;
        } else {
            return Position.CURRENT;
        }
    }

}