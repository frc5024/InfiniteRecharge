package frc.robot.autonomous;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.utils.RobotLogger;
import frc.robot.autonomous.actions.LogCommand;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.autonomous.paths.score.ScoreCenter;
import frc.robot.autonomous.paths.score.ScoreRight;
import frc.robot.autonomous.paths.slow.SlowRight;
import frc.robot.autonomous.paths.slow.SlowRightHalffield;
import frc.robot.autonomous.paths.test.TestReverse;
import frc.robot.subsystems.DriveTrain;
import frc.robot.autonomous.paths.test.TestPID;

/**
 * Class for handling autonomous command generation
 */
public class Chooser {
    RobotLogger logger = RobotLogger.getInstance();

    /* Mode selectors */
    private SendableChooser<AutonomousPath> m_pathChooser = new SendableChooser<>();
    private SendableChooser<Boolean> m_shouldScore = new SendableChooser<>();
    private SendableChooser<Boolean> m_getExtraCells = new SendableChooser<>();

    /**
     * Here, we set up each chooser. This should be changed to reflect the ideal
     * "defaults"
     */
    public Chooser() {

        // Paths
        m_pathChooser.setDefaultOption("Center Score", new ScoreCenter());
        m_pathChooser.addOption("Backwards test", new TestReverse());
        m_pathChooser.addOption("Test PID", new TestPID());
        m_pathChooser.addOption("Right side slow (half-field shot)", new SlowRightHalffield());
        m_pathChooser.addOption("Right side slow", new SlowRight());
        m_pathChooser.addOption("Score Right", new ScoreRight());

        // Scoring
        m_shouldScore.setDefaultOption("Score balls", true);
        m_shouldScore.addOption("Do not score balls", false);

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
        autonTab.add(m_pathChooser);
        autonTab.add(m_shouldScore);
        autonTab.add(m_getExtraCells);

    }

    /**
     * Generate an Autonomous command to be run based on chooser inputs
     * 
     * @return Generated command
     */
    public CommandBase generateAutonomousCommand() {
        logger.log("Chooser", "Generating autonomous command object");

        // Create a group of commands to build from
        CommandGroupBase outputCommand = new SequentialCommandGroup();

        // Specify subsystems needed by the command
        outputCommand.addRequirements(DriveTrain.getInstance());

        // Add a log command
        outputCommand.addCommands(new LogCommand("Starting autonomous actions"));

        // Generate and add the path
        outputCommand.addCommands(
                m_pathChooser.getSelected().generate(m_shouldScore.getSelected(), m_getExtraCells.getSelected()));

        return outputCommand;
    }

    /**
     * Get a Pose2d representing the robot's exact starting location for the
     * selected autonomous mode
     * 
     * @return Robot position
     */
    public Pose2d getRobotAutoStartPosition() {
        logger.log("Chooser", "Reading autonomous position");

        // Get the selected path start pose
        return m_pathChooser.getSelected().getStartingPose();

    }
}