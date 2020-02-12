package frc.robot.autonomous.paths.balls;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.utils.RobotLogger;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.LogCommand;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;
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
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 2.0).withTimeout(2));

        // Get to the front of the balls
        output.addCommands(new LogCommand("[Autonomous]", "Driving to the front of the trench"));
        output.addCommands(new DriveToCommand(new Pose2d(startx + 2.0, starty, Rotation2d.fromDegrees(0)),
                new SpeedConstraint(1, 1), false, false).withTimeout(10));

        // Slowly drive through the balls while intaking
        output.addCommands(new LogCommand("[Autonomous]", "Deploying intake and driving through balls"));
        output.addCommands(new ParallelRaceGroup(new DriveToCommand(new Pose2d(startx + 3.1, starty + 0.0, Rotation2d.fromDegrees(0)),
        new SpeedConstraint(0.3, 0.8), false), new IntakeCells(2)).withTimeout(5));

        // Aim at goal
        // VisionAlignOrAngle
        output.addCommands(new LogCommand("[Autonomous]", "Turning to facce target"));
        output.addCommands(new VisionAlign(Rotation2d.fromDegrees(165), 2.0).withTimeout(4));

        output.addCommands(new ShootCells(5).withTimeout(8));

        // Return the command
        return output;
    }

}