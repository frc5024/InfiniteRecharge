package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.DriveDistance;
import frc.robot.autonomous.actions.DrivePath;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.LogCommand;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.subsystems.cellmech.Hopper;


/**
 * Starts Center scores then gets one ball
 */
public class CenterDriveOff extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {

        // Define some handy helpers
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();

        // Make an output group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Ensure robot faces correct angle
        output.addCommands(new LogCommand("Autonomous", "Ensuring robot is facing proper heading"));
        output.addCommands(new TurnToCommand(getStartingPose().getRotation(), 2.0));

        // Shoot 3 balls
        output.addCommands(new LogCommand("Autonomous", "Shooting 3 cells"));
        output.addCommands(new InstantCommand(()->{Hopper.getInstance().forceCellCount(3);}));
        output.addCommands(new ShootCells(3).withTimeout(3.5));

        // Turn to trench
        output.addCommands(new LogCommand("Autonomous", "Driving Backwards"));
        output.addCommands(new TurnToCommand(0, 2.0));
        output.addCommands(new DriveToCommand(new Pose2d(0, 2, Rotation2d.fromDegrees(0))));
   

        output.addCommands(new LogCommand("Autonomous", "Auto Path has finished"));        

        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_INFRONT_OF_GOAL, Rotation2d.fromDegrees(-180));
    }

}