package frc.robot.subsystems.cellmech;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.TalonHelper;
import frc.robot.RobotConstants;

/**
 * Robot Intake subsystem
 */
public class Intake extends SubsystemBase {
    public static Intake s_instance = null;

    /**
     * Motor that moves intake up and down
     */
    private WPI_TalonSRX m_intakeActuator;

    /**
     * Motor that drives the roller
     */
    private WPI_TalonSRX m_intakeRoller;

    /**
     * Hall effects sensor at the bottom intake arm position
     */
    private DigitalInput m_bottomHall;

    /**
     * Hall effects sensor at the top intake arm position
     */
    private DigitalInput m_topHall;

    /**
     * System states
     */
    private enum SystemState {
        IDLE, // System Idle
        LOWER, // Arm moving to bottom hall
        INTAKE, // Arm moving to bottom hall and spinning roller inwards
        UNJAM, // Arm moving to bottom hall and spinning roller outwards
        RAISING // Arm moving to top hall
    }

    /**
     * Tracker for intake system state.
     */
    private SystemState m_systemState = SystemState.IDLE;
    private SystemState m_lastState = null;

    private Intake() {

        // Construct motor controllers
        m_intakeActuator = new WPI_TalonSRX(RobotConstants.Intake.MotorControllers.INTAKE_ACTUATOR_TALON);
        m_intakeRoller = new WPI_TalonSRX(RobotConstants.Intake.MotorControllers.INTAKE_ROLLER_TALON);

        // Set voltage limiting
        TalonHelper.configCurrentLimit(m_intakeActuator, 34, 32, 30, 0);
        TalonHelper.configCurrentLimit(m_intakeRoller, 34, 32, 30, 0);

        // Construct sensors
        m_bottomHall = new DigitalInput(RobotConstants.Intake.Sensors.INTAKE_HALL_BOTTOM);
        m_topHall = new DigitalInput(RobotConstants.Intake.Sensors.INTAKE_HALL_TOP);
    }

    /**
     * Get the instance of Intake
     * 
     * @return Intake Instance
     */
    public static Intake getInstance() {
        if (s_instance == null) {
            s_instance = new Intake();
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

        // Handle states
        switch (m_systemState) {
            case IDLE:
                handleIdle(isNewState);
                break;
            case LOWER:
                handleLower(isNewState);
                break;
            case INTAKE:
                handleIntake(isNewState);
                break;
            case UNJAM:
                handleUnjam(isNewState);
                break;
            case RAISING:
                handleRaising(isNewState);
                break;
            default:
                m_systemState = SystemState.IDLE;
        }
    }

    /**
     * Set arm and roller motors to stop
     * 
     * @param newState Is this state new?
     */
    private void handleIdle(boolean newState) {
        if (newState) {

            // Stop arm movement
            setArmSpeed(0.0);

            // Stop roller
            m_intakeRoller.set(0.0);

        }
    }

    /**
     * Set arm to move down and roller to stop
     * 
     * @param newState Is this state new?
     */
    private void handleLower(boolean newState) {
        if (newState) {

            // Set arm to move down
            setArmSpeed(0.5);

            // Stop roller
            m_intakeRoller.set(0.0);

        }
    }

    /**
     * Set arm to move down and roller to roll cells in
     * 
     * @param newState Is this state new?
     */
    private void handleIntake(boolean newState) {
        if (newState) {

            // Set arm to move down
            setArmSpeed(0.5);

            // Set roller to take in cells
            m_intakeRoller.set(0.8);

        }
    }

    /**
     * Set arm to move down and roller to roll cells out
     * 
     * @param newState Is this state new?
     */
    private void handleUnjam(boolean newState) {
        if (newState) {

            // Set arm to move down
            setArmSpeed(0.5);

            // Set roller to take in cells
            m_intakeRoller.set(-0.8);

        }
    }

    /**
     * Set arm to move up and stop roller
     * 
     * @param newState Is this state new?
     */
    private void handleRaising(boolean newState) {
        if (newState) {

            // Set arm to move up
            setArmSpeed(-0.5);

            // Stop roller
            m_intakeRoller.set(0.0);

        }
    }

    /**
     * Sets the speed of the arm. All arm movement should be done with this method, so the arm doesn't try to go past it's limits
     * 
     * @param speed desired speed of the arm -1.0 to 1.0
     */
    private void setArmSpeed(double speed) {

        // if moving down, only move if bottom hall not active
        if (speed > 0.0) {
            if (m_bottomHall.get()) {
                speed = 0.0;
            }
        }

        // if moving up, only move if top hall not active
        if (speed < 0.0) {
            if (m_topHall.get()) {
                speed = 0.0;
            }
        }

        m_intakeActuator.set(speed);

    }

}