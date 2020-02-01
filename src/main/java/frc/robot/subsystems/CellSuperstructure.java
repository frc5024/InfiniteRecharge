package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.robot.subsystems.cellmech.Hopper;
import frc.robot.subsystems.cellmech.Intake;
import frc.robot.subsystems.cellmech.Shooter;

/**
 * The CellSuperstructure is the overarching subsystem and state machine in
 * charge of managing the intake, sorting, and shooting of balls.
 * 
 * This class should be called by other parts of the codebase, and will pass
 * commands along to it's sub-subsystems. Do not call the sub-subsystems
 * independently.
 */
public class CellSuperstructure extends SubsystemBase {

    /**
     * Class instance
     */
    private static CellSuperstructure s_instance = null;

    /**
     * Robot logger util
     */
    private RobotLogger logger = RobotLogger.getInstance();

    /* Intake instance */
    private Intake m_intake = Intake.getInstance();
    /* Hopper instance */
    private Hopper m_hopper = Hopper.getInstance();
    /* Shooter instance */
    private Shooter m_shooter = Shooter.getInstance();

    /* Internal system states */
    private enum SystemState {
        IDLE, // System idle
        INTAKING, // Intaking cells
        SHOOTING, // Shooting cells
        UNJAMING // Unjamming cells
    }

    /** Tracker for intake system state */
    private SystemState m_systemState = SystemState.IDLE;
    /** Tracker for last intake system state */
    private SystemState m_lastState = null;

    /* User requested action */
    public enum WantedAction {
        STOWED, // System stowed
        INTAKING, // Intaking balls
        SHOOTING, // Shooting balls
        UNJAMING, // Unjamming balls
    }

    /** Tracker for what the user wants the superstructure to do */
    private WantedAction m_wantedAction = WantedAction.STOWED;

    /** Amount of cells hopper should have after intaking */
    private int m_wantedCellsIntake = 5;

    /** Amount of cells hopper should have after shooting */
    private int m_wantedCellsAfterShot = 0;

    private CellSuperstructure() {

        // Register all sub-subsystems
        logger.log("CellSuperstructure", "Registering sub-subsystems", Level.kRobot);
        m_intake.register();
        m_hopper.register();
        m_shooter.register();

    }

    /**
     * Get the {@link CellSuperstructure} instance
     * 
     * @return Instance
     */
    public static CellSuperstructure getInstance() {
        if (s_instance == null) {
            s_instance = new CellSuperstructure();
        }

        return s_instance;
    }

    @Override
    public void periodic() {

        // Handle wanted action. Preventing switching between certain states here
        switch (m_wantedAction) {
            case STOWED:
                if (m_systemState != SystemState.IDLE) {
                    m_systemState = SystemState.IDLE;
                }
                break;
            case INTAKING:
                if (m_systemState != SystemState.INTAKING) {
                    m_systemState = SystemState.INTAKING;
                }
                break;
            case SHOOTING:
                if (m_systemState != SystemState.SHOOTING) {
                    m_systemState = SystemState.SHOOTING;
                }
                break;
            case UNJAMING:
                if (m_systemState != SystemState.UNJAMING) {
                    m_systemState = SystemState.UNJAMING;
                }
                break;
            default:
                m_wantedAction = WantedAction.STOWED;
        }

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
            case INTAKING:
                handleIntaking(isNewState);
                break;
            case SHOOTING:
                handleShooting(isNewState);
                break;
            case UNJAMING:
                handleUnjamming(isNewState);
                break;
            default:
                m_systemState = SystemState.IDLE;
        }
    }

    /**
     * Set subsystems to hold cells
     * 
     * @param newState Is this state new?
     */
    private void handleIdle(boolean newState) {
        if (newState) {

            // Stops subsystems
            m_intake.raise();
            m_hopper.stop();
            m_shooter.stop();

        }
    }

    /**
     * Set subsystems to intake cells
     * 
     * @param newState Is this state new?
     */
    private void handleIntaking(boolean newState) {
        if (newState) {

            m_intake.intakeCells();

            m_hopper.startIntake(m_wantedCellsIntake);

            m_shooter.stop();

        } else {
            // stop everything once hopper has desired amount of cells
            if (m_hopper.isDone()) {
                m_wantedAction = WantedAction.STOWED;
            }
        }
    }

    /**
     * Set subsystems to shoot cells
     * 
     * @param newState Is this state new?
     */
    private void handleShooting(boolean newState) {
        if (newState) {

            m_intake.raise();

            m_hopper.supplyCellsToShooter(m_wantedCellsAfterShot);

            m_shooter.setOutputPercent(0.7);

        } else {
            // stop everything once hopper has desired amount of cells
            if (m_hopper.isDone()) {
                m_wantedAction = WantedAction.STOWED;
            }
        }
    }

    /**
     * Set subsystems unjam balls
     * 
     * @param newState Is this state new?
     */
    private void handleUnjamming(boolean newState) {
        if (newState) {

            m_intake.unjam();

            m_hopper.unjam();

            m_shooter.setOutputPercent(-0.2);

        }
    }

    /**
     * @return wether or not the superStructure has completed it's actions (if it is idle or not)
     */
    public boolean isDone() {
        return m_systemState == SystemState.IDLE;
    }

    /**
     * Set the subsystems to shoot an amount of cells
     * 
     * @param amount amount of cells the subsystems should try to shoot
     */
    public void shootCells(int amount) {
        int amountToEndUpWith = m_hopper.getCellCount() - amount;

        // limit amount to between 0 and 4
        amountToEndUpWith = amountToEndUpWith < 0 ? 0 : amountToEndUpWith;
        amountToEndUpWith = amountToEndUpWith > 4 ? 4 : amountToEndUpWith;

        // set amount of cells the hopper should have before stopping
        m_wantedCellsAfterShot = amountToEndUpWith;

        m_wantedAction = WantedAction.SHOOTING;
    }

    /**
     * Set the subsystems to intake an amount of cells
     * 
     * @param amount amount of cells the subsystems should try to take in
     */
    public void intakeCells(int amount) {
        int amountToEndUpWith = m_hopper.getCellCount() + amount;

        // limit amount to between 1 and 5
        amountToEndUpWith = amountToEndUpWith < 1 ? 1 : amountToEndUpWith;
        amountToEndUpWith = amountToEndUpWith > 5 ? 5 : amountToEndUpWith;

        // set amount of cells the hopper should have before stopping
        m_wantedCellsIntake = amountToEndUpWith;

        m_wantedAction = WantedAction.INTAKING;
    }

    /**
     * Set the subsystems to stop
     */
    public void stop() {
        m_wantedAction = WantedAction.STOWED;
    }

    /**
     * Set the subsystems to unjam
     */
    public void unjam() {
        m_wantedAction = WantedAction.UNJAMING;
    }
}