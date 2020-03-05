package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.robot.RobotConstants;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.DriveDistance;
import frc.robot.autonomous.actions.DrivePath;
import frc.robot.autonomous.actions.LogCommand;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.SetShooterOutput;
import frc.robot.autonomous.actions.cells.ShootCells;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.subsystems.cellmech.Hopper;


public class ShootTrenchDriveOff extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {

        // Define some handy helpers
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();

        // Make an output group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Ensure robot faces correct angle
        output.addCommands(new LogCommand("Autonomous", "Ensuring proper heading"));
        output.addCommands(new TurnToCommand(getStartingPose().getRotation(), 2.0));

        // Aim at goal
        output.addCommands(new LogCommand("Autonomous", "Turing to Goal"));
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(150), 8.0));
        output.addCommands(new InstantCommand(()->{Hopper.getInstance().forceCellCount(3);}));
        

        // // Shoot 3 balls
        output.addCommands(new LogCommand("Autonomous", "Shooting 3 Balls"));
        output.addCommands(new SetShooterOutput(RobotConstants.Shooter.DEFAULT_VELOCITY));
        output.addCommands(new ShootCells(3).withTimeout(3.5));

        // Drives back
        output.addCommands(new LogCommand("Autonomous", "Driving Backwards"));
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 8.0));
        // output.addCommands(new DriveDistance(-.1, 10, .5));

        output.addCommands(new LogCommand("Autonomous", "Path Finished"));

        
        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_RIGHT, Rotation2d.fromDegrees(-180));
    }

}