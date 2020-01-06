package frc.robot.autonomous;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.utils.RobotLogger;

/**
 * Class for handling autonomous command generation
 */
public class Chooser {
    RobotLogger logger = RobotLogger.getInstance();

    private enum ScoringDelay {
        EARLY, LATE;
    }

    /* Mode selectors */
    private SendableChooser<Pose2d> m_positionChooser = new SendableChooser<>();
    private SendableChooser<Boolean> m_shouldScore = new SendableChooser<>();
    private SendableChooser<ScoringDelay> m_scoreDelay = new SendableChooser<>();
    private SendableChooser<Boolean> m_getExtraCells = new SendableChooser<>();

    /**
     * Here, we set up each chooser. This should be changed to reflect the ideal
     * "defaults"
     */
    public Chooser() {

        // Positions
        m_positionChooser.setDefaultOption("Line right", AutonomousStartpoints.SECTOR_LINE_RIGHT);

        // Scoring
        m_shouldScore.setDefaultOption("Score balls", true);
        m_shouldScore.addOption("Do not score balls", false);

        // Delay
        m_scoreDelay.setDefaultOption("Score early", ScoringDelay.EARLY);
        m_scoreDelay.addOption("Score late", ScoringDelay.LATE);

        // Extra cells
        m_getExtraCells.setDefaultOption("Get extra balls", true);
        m_getExtraCells.addOption("Do not get extra balls", false);
    }

    /**
     * Publish all chooser options to Shuffleboard
     */
    public void publishOptions() {
        logger.log("Chooser", "Publishing auton options");

        ShuffleboardTab autonTab = Shuffleboard.getTab("Autonomous");

        // Push each chooser
        autonTab.add(m_positionChooser);
        autonTab.add(m_shouldScore);
        autonTab.add(m_scoreDelay);
        autonTab.add(m_getExtraCells);

    }

    /**
     * Generate an Autonomous command to be run based on chooser inputs
     * 
     * @return Generated command
     */
    public CommandBase generateAutonomousCommand() {
        logger.log("Chooser", "Generating autonomous command object");

        // TODO: Generate a command / commandgroup to be run in autonomous here.

        return null;
    }

    /**
     * Get a Pose2d representing the robot's exact starting location for the
     * selected autonomous mode
     * 
     * @return Robot position
     */
    public Pose2d getRobotAutoStartPosition() {
        logger.log("Chooser", "Reading autonomous position");

        return m_positionChooser.getSelected();
    }
}