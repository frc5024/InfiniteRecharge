package frc.robot.autonomous.paths.test;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.robot.autonomous.actions.DrivePath;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.TrianglePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.paths.AutonomousPath;

public class TestPath extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {
        SequentialCommandGroup output = new SequentialCommandGroup();

        output.addCommands(new TurnToCommand(0.0));
        // output.addCommands(new DrivePath(new Path(new Translation2d(0.0, 0.0), new
        // Translation2d(2.5, -1.0)), 0.8,
        // new Translation2d(0.3, 0.3)));
        // output.addCommands(new DriveToCommand(new Pose2d(2.5, -1.0,
        // Rotation2d.fromDegrees(0.0))));
        output.addCommands(new TrianglePath(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)),
                new Translation2d(1.0, -1.0), new Pose2d(2.0, -1.0, Rotation2d.fromDegrees(0.0))));
        output.addCommands(new TurnToCommand(0.0));
        // output.addCommands(new DriveToCommand(new Pose2d(5.4, -3.5,
        // Rotation2d.fromDegrees(0.0))));

        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d();
    }

}