package frc.robot.autonomous.paths.balls;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;

/**
 * Slow path starting on the right of the line
 */
public class ballsScoreTrench extends AutonomousPath {

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_RIGHT, Rotation2d.fromDegrees(0));
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
        output.addCommands(new DriveToCommand(new Pose2d(startx + 2.0, starty, Rotation2d.fromDegrees(0)),
                new SpeedConstraint(1, 1), false, false));

        // Slowly drive through the balls while intaking
        output.addCommands(new DriveToCommand(new Pose2d(startx + 3.1, starty + 0.0, Rotation2d.fromDegrees(0)),
                new SpeedConstraint(0.3, 0.8), false));

        // Aim at goal
        // VisionAlignOrAngle
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(165), 20));
        output.addCommands(new VisionAlign(Rotation2d.fromDegrees(165), 2.0));

        // Return the command
        return output;
    }

}