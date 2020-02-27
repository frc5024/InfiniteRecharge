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
import frc.robot.autonomous.actions.DrivePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.subsystems.cellmech.Shooter;

public class ScoreTwice extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {

        // Define some handy helpers
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();

        // Make an output group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Ensure robot faces correct angle
        output.addCommands(new TurnToCommand(getStartingPose().getRotation(), 2.0));

        // Aim at goal
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(150), 8.0));

        // Config shooter fallback
        output.addCommands(new InstantCommand(() -> {
            Shooter.getInstance().setFallbackVelocity(RobotConstants.Shooter.MOTOR_MAX_RPM * 0.74);
        }));

        // Shoot 3 balls
        output.addCommands(new ShootCells(3).withTimeout(3.5));

        // Turn to trench
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 8.0));

        // Intake balls
        CommandBase intakeCommand = new IntakeCells(2);

        // Get to the start of the trench
        DrivePath preTrench = new DrivePath(
                new Path(new Translation2d(startx, starty), new Translation2d(startx + 0.4, starty)), 0.2,
                new Translation2d(0.2, 0.2), 0.025, 0.5);

        // Get through the trench
        DrivePath trenchMovement = new DrivePath(
                new Path(new Translation2d(startx + 0.2, starty), new Translation2d(startx + 1.1, starty)), 0.2,
                new Translation2d(0.2, 0.2), 0.025, 0.25);

        // Race the intake
        output.addCommands(new ParallelRaceGroup(intakeCommand, new SequentialCommandGroup(preTrench, trenchMovement)));

        // // PPS Turn
        // output.addCommands(new DrivePath(
        // new Path(new Translation2d(startx + 2.0, starty), new Translation2d(startx +
        // 2.1, starty + 0.4)), 0.2,
        // new Translation2d(0.2, 0.2), 0.2, 0.15));

        // Face the target
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(165), 8.0));

        // Config shooter fallback
        output.addCommands(new InstantCommand(() -> {
            Shooter.getInstance().setFallbackVelocity(RobotConstants.Shooter.MOTOR_MAX_RPM * 0.83);
        }));

        // Shoot 3 balls
        output.addCommands(new ShootCells(2).withTimeout(3.0));

        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_RIGHT, Rotation2d.fromDegrees(-180));
    }

}