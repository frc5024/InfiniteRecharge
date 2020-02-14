package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.autonomous.actions.DriveDistance;
import frc.robot.autonomous.actions.DriveToCommand;

/**
 * Score path starting on the right of the line
 */
public class ScoreRight extends AutonomousPath {

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_GOAL, Rotation2d.fromDegrees(-180));
    }


    @Override
    protected SequentialCommandGroup getCommand() {

        // Create output command group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Some constants to make positioning easier
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();
        

        // Ensure robot is facing the correct angle at the start
        output.addCommands(new TurnToCommand((-180), 2).withTimeout(3));


        // This is where the ball shooting would happen
        output.addCommands(new ShootCells(3).withTimeout(5));

        // Drives towards trench
        output.addCommands(new DriveToCommand(new Pose2d(startx + 1.4, starty - 1.6, Rotation2d.fromDegrees(-45)),
                 new SpeedConstraint(1, 1), false, false).withTimeout(8));

        // Drives over balls
        output.addCommands(new ParallelCommandGroup(
            new DriveToCommand(new Pose2d(startx + 4, starty - 1.8, Rotation2d.fromDegrees(0)),
            new SpeedConstraint(0.3, 0.6), false),
            new IntakeCells(3)).withTimeout(7));

        // Refaces forward
        output.addCommands(new TurnToCommand((0), 5).withTimeout(3));

        return output;
    }

}