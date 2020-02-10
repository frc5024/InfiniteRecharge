package frc.robot.subsystems.cellmech;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.TalonHelper;
import frc.lib5k.components.sensors.LimitSwitch;
import frc.lib5k.simulation.wrappers.SimTalon;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.robot.RobotConstants;

/**
 * Robot Intake subsystem
 */
public class Intake extends SubsystemBase {
    private RobotLogger logger = RobotLogger.getInstance();
    public static Intake s_instance = null;

    /** Motor that moves intake up and down */
    private SimTalon m_intakeActuator;

    private SimTalon m_intakeRoller;

    /** Hall effects sensor at the bottom intake arm position */
    private LimitSwitch m_bottomHall;

    /** Hall effects sensor at the top intake arm position */
    private LimitSwitch m_topHall;

    /** System states */
    private enum SystemState {
        INTAKE, // Intake pulling in balls
        UNJAM, // Ejecting balls
        STOWED, // Arm stowed
    }

    /** Tracker for intake system state */
    private SystemState m_systemState = SystemState.STOWED;

    /** Tracker for last intake system state */
    private SystemState m_lastState = null;

    /** Arm positions */
    private enum ArmPosition {
        STOWED, // Arm stowed
        DEPLOYED, // Arm deployed
        UNKNOWN, // Arm position unknown by system
    }

    private Intake() {

        // Construct motor controllers
        m_intakeActuator = new SimTalon(RobotConstants.Intake.INTAKE_ACTUATOR_TALON);
        m_intakeRoller = new SimTalon(RobotConstants.Intake.INTAKE_ROLLER_TALON);

        // Invert motors that need to be inverted
        m_intakeActuator.setInverted(RobotConstants.Intake.INTAKE_ACTUATOR_TALON_INVERTED);
        m_intakeRoller.setInverted(RobotConstants.Intake.INTAKE_ROLLER_TALON_INVERTED);

        // Set voltage limiting
        TalonHelper.configCurrentLimit(m_intakeActuator, 34, 32, 30, 0);
        TalonHelper.configCurrentLimit(m_intakeRoller, 34, 32, 30, 0);

        // Set ramps
        m_intakeActuator.configOpenloopRamp(0.2);
        m_intakeRoller.configOpenloopRamp(0.1);

        // Construct sensors
        m_bottomHall = new LimitSwitch(RobotConstants.Intake.INTAKE_LIMIT_BOTTOM);
        m_topHall = new LimitSwitch(RobotConstants.Intake.INTAKE_LIMIT_TOP);

        // Add children
        addChild("Arms", m_intakeActuator);
        addChild("Roller", m_intakeRoller);
        addChild("Bottom Limit", m_bottomHall);
        addChild("Top limit", m_topHall);
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
        case INTAKE:
            handleIntake(isNewState);
            break;
        case UNJAM:
            handleUnjam(isNewState);
            break;
        case STOWED:
            handleStowed(isNewState);
            break;
        default:
            logger.log("Intake", "Encountered unknown state", Level.kWarning);
            m_systemState = SystemState.STOWED;
        }
    }

    /**
     * Set arm to move down and roller to roll cells in
     * 
     * @param newState Is this state new?
     */
    private void handleIntake(boolean newState) {
        if (newState) {
            // Ensure our roller is stopped before arm deployment
            setRollerSpeed(0.0);

        }

        // As long as we are not at our deployed position, lower the arms
        if (getArmPosition() != ArmPosition.DEPLOYED) {
            setArmSpeed(1.0);
        } else {
            // Stop the arms
            setArmSpeed(0.0);

            // Handle intake of cells
            setRollerSpeed(1.0);
        }

        // NOTE: This action does not stop automatically
    }

    /**
     * Set arm to move down and roller to roll cells out
     * 
     * @param newState Is this state new?
     */
    private void handleUnjam(boolean newState) {
        if (newState) {
            // Ensure our roller is stopped before arm deployment
            setRollerSpeed(0.0);

        }

        // As long as we are not at our deployed position, lower the arms
        if (getArmPosition() != ArmPosition.DEPLOYED) {
            setArmSpeed(1.0);
        } else {
            // Stop the arms
            setArmSpeed(0.0);

            // Handle intake of cells
            setRollerSpeed(-0.8);
        }

        // NOTE: This action does not stop automatically
    }

    /**
     * Set arm to move up and stop roller
     * 
     * @param newState Is this state new?
     */
    private void handleStowed(boolean newState) {
        if (newState) {
            // Ensure our roller is stopped
            setRollerSpeed(0.0);

        }

        // As long as we are not at our deployed position, lower the arms
        if (getArmPosition() != ArmPosition.STOWED) {
            setArmSpeed(-1.0);
        } else {
            // Just apply a little voltage to arms to kep them in place
            // TODO: Is this needed?
            // setArmSpeed(-0.15);
            setArmSpeed(0.0);
        }

        // NOTE: This action does not stop automatically

    }

    private ArmPosition getArmPosition() {

        if (m_bottomHall.get()) {
            return ArmPosition.DEPLOYED;
        }
        if (m_topHall.get()) {
            return ArmPosition.STOWED;
        }
        return ArmPosition.UNKNOWN;
    }

    /**
     * Sets the speed of the arm. All arm movement should be done with this method,
     * so the arm doesn't try to go past it's limits
     * 
     * @param speed desired speed of the arm -1.0 to 1.0
     */
    private void setArmSpeed(double speed) {

        // if moving down, only move if bottom hall not active
        if (speed > 0.0) {
            if (m_bottomHall.get()) {
                speed = 0.0;
            } else {
                speed *= 0.5;
            }
        }

        // if moving up, only move if top hall not active
        if (speed < 0.0) {
            if (m_topHall.get()) {
                speed = 0.0;
            } else {
                speed *= 0.9;
            }
        }

        m_intakeActuator.set(speed);

    }

    /**
     * Sets the speed of the roller wheel
     * 
     * @param speed desired speed of the roller -1.0 to 1.0
     */
    private void setRollerSpeed(double speed) {
        m_intakeRoller.set(speed);
    }

    /**
     * Set the harvester to unjam
     */
    public void unjam() {
        m_systemState = SystemState.UNJAM;
    }

    /**
     * Set the harvester to intake
     */
    public void intake() {
        m_systemState = SystemState.INTAKE;
    }

    /**
     * Stow the harvester
     */
    public void stow() {
        m_systemState = SystemState.STOWED;
    }

}