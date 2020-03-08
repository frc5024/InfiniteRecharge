package frc.robot.autonomous;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.utils.RobotLogger;
import frc.robot.Dashboard;
import frc.robot.autonomous.actions.LogCommand;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.autonomous.paths.score.BuddyScore;
import frc.robot.autonomous.paths.score.CenterDriveOff;
import frc.robot.autonomous.paths.score.CenterScore;
import frc.robot.autonomous.paths.score.ScoreTwice;
import frc.robot.autonomous.paths.score.ShootTrenchDriveOff;
import frc.robot.subsystems.DriveTrain;
import frc.robot.autonomous.paths.test.TestPID;
import frc.robot.autonomous.paths.test.TestPure;

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
        m_pathChooser.setDefaultOption("1. Score Twice", new ScoreTwice());
        m_pathChooser.addOption("2. Shoot Center", new CenterScore());
        m_pathChooser.addOption("3. Shoot Trench Drive Off", new ShootTrenchDriveOff());
        m_pathChooser.addOption("4. Shoot Center Drive Off", new CenterDriveOff());
        m_pathChooser.addOption("5. Buddy Score", new BuddyScore());
        m_pathChooser.addOption("Test PID", new TestPID());
        m_pathChooser.addOption("Test Pure", new TestPure());
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
        // autonTab.add(m_pathChooser);
        // autonTab.add(m_shouldScore);
        // autonTab.add(m_getExtraCells);

        Dashboard.getInstance().publishAutonChooser(m_pathChooser);

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
        try {
            outputCommand.addCommands(
                    m_pathChooser.getSelected().generate(m_shouldScore.getSelected(), m_getExtraCells.getSelected()));
        } catch (NullPointerException e) {
            System.out.println("WARNING: Failed to generate Autonomous path due to NPE");
        }

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