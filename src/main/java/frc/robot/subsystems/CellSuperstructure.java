package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.lib5k.utils.Mathutils;
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
        UNJAMING, // Unjamming cells
        UNJAMUP, // unjamming cells upwards
        MOVETOBOTTOM // Moves cells to bottom
    }

    /** Tracker for intake system state */
    private SystemState m_systemState = SystemState.IDLE;
    /** Tracker for last intake system state */
    private SystemState m_lastState = null;

    /** Amount of cells hopper should have after intaking */
    private int m_wantedCellsIntake = 5;

    /** Amount of cells hopper should have after shooting */
    private int m_wantedCellsAfterShot = 0;

    /** True when intake just stopped itself */
    private boolean m_intakeDone = false;

    /** logger instance */
    private RobotLogger m_loggerInstance = RobotLogger.getInstance();

    private CellSuperstructure() {

        // Register all sub-subsystems
        m_loggerInstance.log("CellSuperstructure", "Registering sub-subsystems", Level.kRobot);
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

        if (m_intakeDone = true) {
            m_intakeDone = false;
        }

        // Determine if this state is new
        boolean isNewState = false;
        if (m_systemState != m_lastState) {
            isNewState = true;
        }

        boolean lastStateIsShooting = (m_lastState == SystemState.SHOOTING);
        m_lastState = m_systemState;

        // Handle states
        switch (m_systemState) {
            case IDLE:
                handleIdle(isNewState, lastStateIsShooting);
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
            case UNJAMUP:
                handleUnjamUp(isNewState);
                break;
            case MOVETOBOTTOM:
                handleMoveToBottom(isNewState);
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
    private void handleIdle(boolean newState, boolean wasShooting) {
        if (newState) {
            m_loggerInstance.log("CellSuperstructure", "Entering idle state");
            // Stops subsystems
            m_intake.stow();
            m_shooter.stop();

            if (wasShooting && m_hopper.getCellCount() > 0) {
                m_hopper.interruptShooting();
            } else {
                m_hopper.stop();
            }

        }
    }

    /**
     * Set subsystems to intake cells
     * 
     * @param newState Is this state new?
     */
    private void handleIntaking(boolean newState) {
        if (newState) {
            m_loggerInstance.log("CellSuperstructure", "Entering intake state");

            m_intake.intake();

            m_hopper.startIntake();

            m_shooter.stop();

        }

        int cellCount = m_hopper.getCellCount();

        // stop once there are enough cells
        if (cellCount >= 5 || cellCount == m_wantedCellsIntake || m_hopper.getTopLineBreak()) {
            m_systemState = SystemState.IDLE;
            m_intakeDone = true;
        }
    }

    /**
     * Set subsystems to shoot cells
     * 
     * @param newState Is this state new?
     */
    private void handleShooting(boolean newState) {
        if (newState) {
            m_loggerInstance.log("CellSuperstructure", "Entering shooting state");

            m_intake.stow();

            m_hopper.supplyCellsToShooter();

            m_shooter.setVelocity(m_shooter.getVelocityFromLimelight());

        } else {
            // stop everything once hopper has desired amount of cells or no cells
            int cellAmount = m_hopper.getCellCount();

            if (cellAmount == m_wantedCellsAfterShot || cellAmount == 0) {
                m_systemState = SystemState.IDLE;
            }

            // only supply cells if shooter isn't spun up or the top line break is not
            // tripped
            if (m_shooter.isSpunUp() || m_hopper.topLineBreakState() == false) {
                m_hopper.supplyCellsToShooter();
            } else {
                m_hopper.stop();
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
            m_loggerInstance.log("CellSuperstructure", "Entering unjam state");

            m_intake.unjam();

            m_hopper.unjam();

            m_shooter.setOutputPercent(-0.2);

        }
    }

    /**
     * Set subsystems unjam balls
     * 
     * @param newState Is this state new?
     */
    private void handleUnjamUp(boolean newState) {
        if (newState) {
            m_loggerInstance.log("CellSuperstructure", "Entering unjam up state");

            m_hopper.unjamUp();

            m_shooter.setOutputPercent(0.25);

        }
    }

    /**
     * Set subsystems to intake cells
     * 
     * @param newState Is this state new?
     */
    private void handleMoveToBottom(boolean newState) {
        if (newState) {
            m_loggerInstance.log("CellSuperstructure", "Entering move to bottom state");

            m_hopper.moveCellsToBottom();
        } else {

            // when done moving stop being in this state, so I can go into realign twice in a row
            if(m_hopper.isDone()) {
                m_systemState = SystemState.IDLE;
            }

        }

    }

    /**
     * @return wether or not the superStructure has completed it's actions (if it is
     *         idle or not)
     */
    public boolean isDone() {
        return m_systemState == SystemState.IDLE;
    }

    /**
     * @return wether or not the superStructure has completed intake
     */
    public boolean isDoneIntake() {
        return m_intakeDone;
    }

    public void moveToBottom() {
        m_systemState = SystemState.MOVETOBOTTOM;
    }

    /**
     * Set the subsystems to shoot an amount of cells
     * 
     * @param amount amount of cells the subsystems should try to shoot
     */
    public void shootCells(int amount) {
        int amountToEndUpWith = m_hopper.getCellCount() - amount;

        // limit amount to between 0 and 4
        amountToEndUpWith = MathUtil.clamp(amountToEndUpWith, 0, 4);

        // set amount of cells the hopper should have before stopping
        m_wantedCellsAfterShot = amountToEndUpWith;

        m_systemState = SystemState.SHOOTING;
    }

    /**
     * Set the subsystems to intake an amount of cells
     * 
     * @param amount amount of cells we want to be holding by the end of the action
     */
    public void intakeCells(int amount) {

        // limit amount to between 1 and 5
        amount = (int) Mathutils.clamp(amount, 1, 5);

        // set amount of cells the hopper should have before stopping
        m_wantedCellsIntake = amount;

        int currentCount = m_hopper.getCellCount();

        if (!(currentCount >= m_wantedCellsIntake) && !(m_hopper.getTopLineBreak())) {
            m_systemState = SystemState.INTAKING;
        }

    }

    /**
     * Set the subsystems to stop
     */
    public void stop() {
        m_systemState = SystemState.IDLE;
    }

    /**
     * Set the subsystems to unjam
     */
    public void unjam() {
        m_systemState = SystemState.UNJAMING;
    }

    /**
     * Set the subsystems to unjam up
     */
    public void unjamUp() {
        m_systemState = SystemState.UNJAMUP;
    }
}