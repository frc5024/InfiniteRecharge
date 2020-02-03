package frc.robot.autonomous.paths.test;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.robot.autonomous.actions.DrivePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.paths.AutonomousPath;

public class TestPath extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {
        SequentialCommandGroup output = new SequentialCommandGroup();

        output.addCommands(new TurnToCommand(0.0));
        output.addCommands(new DrivePath(new Path(new Translation2d(0.0, 0.0), new Translation2d(2.0, 1.0), new Translation2d(4.0, -1.0), new Translation2d(1.0, -2.0)), 0.8,
                new Translation2d(0.1, 0.1)));

        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d();
    }

}