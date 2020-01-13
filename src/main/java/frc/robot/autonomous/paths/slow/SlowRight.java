package frc.robot.autonomous.paths.slow;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;

/**
 * Slow path starting on the right of the line
 */
public class SlowRight extends AutonomousPath {

    @Override
    public Pose2d getStartingPose() {
        return AutonomousStartpoints.SECTOR_LINE_RIGHT;
    }

    @Override
    protected SequentialCommandGroup getCommand() {

        // Create output command group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Some constants to make positioning easier
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();

        // Ensure robot is facing the correct angle at the start
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 2.0));

        // Get to the front of the balls
        output.addCommands(new LinePath(getStartingPose(),
                new Pose2d(startx + 3.6, starty + 0.2, Rotation2d.fromDegrees(0)), new SpeedConstraint(1, 1), false));

        // Turn to face the goal
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(30), 2.0));
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(165), 2.0));

        // Get part way to the goal
        output.addCommands(new LinePath(new Pose2d(startx + 3.6, starty + 0.2, Rotation2d.fromDegrees(165)),
                new Pose2d(startx + 1.5, starty + 1.1, Rotation2d.fromDegrees(180)), new SpeedConstraint(1, 1), false));

        // Re-align
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(180), 2.0));

        // Get the rest of the way to the goal
        output.addCommands(new LinePath(new Pose2d(startx + 1.3, starty + 1.1, Rotation2d.fromDegrees(180)),
                new Pose2d(startx - 1.3, starty + 0.8, Rotation2d.fromDegrees(180)), new SpeedConstraint(1, 1), false));

        // Final alignment (should eventually be powered by the limelight)
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(180), 2.0));

        // Return the command
        return output;
    }

}