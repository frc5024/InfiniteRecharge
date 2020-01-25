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

    /* Sub-Subsystems */
    private Intake m_intake = Intake.getInstance();
    private Hopper m_hopper = Hopper.getInstance();
    private Shooter m_shooter = Shooter.getInstance();

    /* Internal system states */
    private enum SystemState {
        IDLE, // System idle
    }

    // Tracker for current system state
    private SystemState m_systemState = SystemState.IDLE;

    /* User requested action */
    public enum WantedAction {
        STOWED, // System stowed
        INTAKING, // Intaking balls
        SHOOTING, // Shooting balls
        UNJAMING, // Unjamming balls
    }

    // Tracker for user action
    private WantedAction m_wantedAction = WantedAction.STOWED;

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

        // TODO: fancy statemachine stuff happens here
    }

    // TODO delete
    public void intakeStuff(int count) {
        m_intake.intakeCells();
        m_hopper.startIntake(count);
    }

    // TODO delete
    public void stopStuff() {
        m_intake.stop();
        m_hopper.stop();
    }
}