package frc.robot.autonomous;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandGroupBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.lib5k.utils.RobotLogger;
import frc.robot.FieldConstants;
import frc.robot.RobotConstants;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.LogCommand;
import frc.robot.autonomous.actions.TrapezoidPath;
import frc.robot.autonomous.actions.TrianglePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.helpers.EasyTrajectory;
import frc.robot.autonomous.helpers.PathGenerator;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.subsystems.DriveTrain;

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
        m_positionChooser.setDefaultOption("45degs drive demo", AutonomousStartpoints.ZERO);
        m_positionChooser.addOption("Auton full right demo", AutonomousStartpoints.SECTOR_LINE_RIGHT);

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

        // Create a group of commands to build from
        CommandGroupBase outputCommand = new SequentialCommandGroup();

        // Specify subsystems needed by the command
        outputCommand.addRequirements(DriveTrain.getInstance());

        // Add a log command
        outputCommand.addCommands(new LogCommand("Starting autonomous actions"));

        // Determine auto to run
        if (getRobotAutoStartPosition().equals(AutonomousStartpoints.ZERO)) {

            outputCommand.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 2.0));

            // Test path following
            outputCommand.addCommands(PathGenerator.generate(new EasyTrajectory(new Pose2d(0, 0, new Rotation2d(0)),
                    new Pose2d(2, 0, Rotation2d.fromDegrees(0))), new SpeedConstraint(1.0, 1.0)));

            // outputCommand.addCommands(PathGenerator.generateInPlaceRotation(45, 2.0));
            outputCommand.addCommands(new TurnToCommand(Rotation2d.fromDegrees(45), 2.0));

            outputCommand.addCommands(PathGenerator.generate(new EasyTrajectory(new Pose2d(2, 0, new Rotation2d(45)),
                    new Pose2d(3, 1, Rotation2d.fromDegrees(45))), new SpeedConstraint(1.0, 1.0)));

            // Example trapezoid
            // outputCommand.addCommands(new TrapezoidPath(new Pose2d(0, 0,
            // Rotation2d.fromDegrees(0)),
            // new Translation2d(1, 1), new Translation2d(2, 1), new Pose2d(3, 0,
            // Rotation2d.fromDegrees(0)),
            // new SpeedConstraint(1.0, 1.0)));
        } else if (getRobotAutoStartPosition().equals(AutonomousStartpoints.SECTOR_LINE_RIGHT)) {

            // Example full auto

            // Constants
            double halfField = FieldConstants.FIELD_WIDTH / 2;
            double halfRobotWidth = RobotConstants.DriveTrain.Measurements.DRIVEBASE_WIDTH / 2;
            double halfRobotLength = RobotConstants.DriveTrain.Measurements.DRIVEBASE_LENGTH / 2;

            // Get balls
            outputCommand.addCommands(new TrianglePath(
                    // Start position
                    new Pose2d(3.1, -3.79, Rotation2d.fromDegrees(90)),

                    // Interior point
                    new Translation2d(0, 0.3),

                    // End point
                    new Pose2d(5.2, -4.0, Rotation2d.fromDegrees(0)),

                    // Constraints on movement
                    new SpeedConstraint(1.0, 1.0), false));

            // Near the goal
            outputCommand.addCommands(new LinePath(new Pose2d(5.2, -4.0, Rotation2d.fromDegrees(0)),
                    new Pose2d(1.8, -3.5, Rotation2d.fromDegrees(-180)), new SpeedConstraint(1, 1), true));

            // Move back to the goal
            // outputCommand.addCommands(new LinePath(new Pose2d(1.8, -3.5,
            // Rotation2d.fromDegrees(0)),
            // new Pose2d(0, -2.6, Rotation2d.fromDegrees(180)), new SpeedConstraint(1, 1),
            // true));

            outputCommand.addCommands(new LinePath(new Pose2d(1.2, -3.1, Rotation2d.fromDegrees(0)),
                    new Pose2d(0, -2.3, Rotation2d.fromDegrees(180)), new SpeedConstraint(1, 1), true));
        }

        // /* Start building command based on params */

        // // Track what we have done
        // boolean hasGottenBalls = false;

        // // Handle late scoring
        // late_score: if (m_scoreDelay.getSelected() == ScoringDelay.LATE) {

        // // If we are not going to score, and we are going to pick up balls, we can
        // skip
        // // the delay. This will trigger the second "pick up balls" action
        // if (!m_shouldScore.getSelected() && m_getExtraCells.getSelected()) {
        // outputCommand.addCommands(new LogCommand("Skipping autonomous delay, and
        // going to get some balls"));

        // break late_score;
        // }

        // // If we are picking up balls, and scoring, go do it
        // if (m_getExtraCells.getSelected()) {

        // outputCommand.addCommands(new LogCommand("Going to pick up some balls"));

        // /**
        // * TODO: ball pickup
        // *
        // * We will go to the trench, and pick up balls until we have gotten 2 (max
        // * capacity), or time runs out.
        // *
        // * Make sure that curves are calculated for each possible start point here
        // */

        // // Return to the initiation line, just in front of the right side start point
        // outputCommand.addCommands(new LogCommand("Returning to initiation line for
        // next action"));

        // // TODO: handle logic

        // // Force-disable the "get balls" setting, so we won't do it again later
        // hasGottenBalls = true;
        // }

        // // Ensure we have waited enough time before going to score
        // outputCommand.addCommands(new LogCommand(String.format("Waiting for match
        // timer to reach the scoring delay time (%.2f)",
        // RobotConstants.Autonomous.SCORE_LATE_DELAY)));
        // outputCommand.addCommands(new
        // WaitUntilCommand(RobotConstants.Autonomous.SCORE_LATE_DELAY));

        // }

        // // Handle scoring
        // if (m_shouldScore.getSelected()) {

        // // Get the robot to the correct position to score
        // outputCommand.addCommands(new LogCommand("Getting into position to score"));

        // // TODO: get to scoring position

        // // Score balls
        // // TODO: score

        // // If we are going to pick up balls, and have not already, return to our
        // // starting position
        // if (m_getExtraCells.getSelected() && !hasGottenBalls) {
        // outputCommand.addCommands(new LogCommand("Returning to initiation line to get
        // ready to grab balls"));

        // // TODO: get to start position with curve again
        // }

        // }

        // // Handle getting balls
        // if (m_getExtraCells.getSelected()) {
        // outputCommand.addCommands(new LogCommand("Going to pickup some balls"));

        // /**
        // * TODO: ball pickup
        // *
        // * We will go to the trench, and pick up balls until we have gotten 3 (new max
        // * capacity), or time runs out.
        // *
        // * Make sure that curves are calculated for each possible start point here
        // */

        // // Force-disable the "get balls" setting, so we won't do it again later
        // hasGottenBalls = true;
        // }

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

        return m_positionChooser.getSelected();

    }
}