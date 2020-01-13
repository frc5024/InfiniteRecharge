package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * The superstructure is an overarching subsystem that contains all robot
 * components other than the drivetrain. It coordinates robot actions.
 * 
 * Instead of interacting with these subsystems individually, controllers should
 * interact with the Superstructure, which will then pass commands on to each
 * subsystem.
 */
public class Superstructure extends SubsystemBase {
    
    private static Superstructure mInstance = null;

    /* Subsystems */
    private Intake m_intake = Intake.getInstance();
	private Climber m_climber = Climber.getInstance();
	private PanelManipulator m_panelManipulator = PanelManipulator.getInstance();
	private Shooter m_shooter = Shooter.getInstance();
    private Hopper m_hopper = Hopper.getInstance();
    
    /* Superstructure internal state */
    public enum SuperstructureState {
        IDLE, // System idle and stowed

        // TODO: work with @awpratten to fill these out
    }

    /* Wanted action from user */
    public enum WantedState {
        IDLE, // Nothing
        INTAKE, // Intaking balls
        SHOOT, // Shooting balls
        PANEL, // Manipulating panel
        CLIMB, // Climbing the truss
    }

    /* State trackers */
    private SuperstructureState m_systemState = SuperstructureState.IDLE;
    private WantedState m_wantedState = WantedState.IDLE;

    private Superstructure() {
        
        /* Register all sub-subsystems */
        m_intake.register();
		m_climber.register();
        m_shooter.register();
        m_hopper.register();
		m_panelManipulator.register();
    }

    public static Superstructure getInstance() {
        if (mInstance == null) {
            mInstance = new Superstructure();
        }
        return mInstance;
    }

    @Override
    public void periodic() {
        
    }
}