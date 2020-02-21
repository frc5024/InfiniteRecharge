package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.LogCommand;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.subsystems.cellmech.Hopper;
import frc.robot.autonomous.actions.DriveDistance;
import frc.robot.autonomous.actions.DriveToCommand;

// Built with Point Interpeter
public class ScoreGetBalls extends AutonomousPath {

    // Starts in front of the trench facing the target
    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_RIGHT, Rotation2d.fromDegrees(180));
    }

    @Override
    protected SequentialCommandGroup getCommand() {
        // Create output command group
        SequentialCommandGroup output = new SequentialCommandGroup();

        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();

        // Ensure robot is facing the correct angle at the start
        output.addCommands(new LogCommand("[Autonomous]", "Setting Angle"));
        output.addCommands(new TurnToCommand((156), 2));


        // Shoot Command
        output.addCommands(new ShootCells(3).withTimeout(7.5));

        output.addCommands(new InstantCommand(()->{
            Hopper.getInstance().forceCellCount(0);
        }));

        output.addCommands(new TurnToCommand(0, 8));
        


        // Drives to the balls in the trench
        output.addCommands(new LogCommand("[Autonomous]", "Driving to front of the trench"));
        output.addCommands(new DriveToCommand(new Pose2d(startx + .3125, starty + .15625, Rotation2d.fromDegrees(0)),
                new SpeedConstraint(1, 1), false));

        // Drives over the balls
        output.addCommands(new LogCommand("[Autonomous]", "Deploying intake and driving through balls")); 
        output.addCommands(
                new ParallelRaceGroup(new DriveToCommand(new Pose2d(startx + .88, starty + .1, Rotation2d.fromDegrees(0)),
                new SpeedConstraint(.3, 1), false), 
                new IntakeCells(3)));

        // output.addCommands(new LogCommand("[Autonomous]", "Realigning"));
        // output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 2).withTimeout(5));

        return output;
    }

    
}