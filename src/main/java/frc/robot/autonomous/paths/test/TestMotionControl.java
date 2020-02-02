package frc.robot.autonomous.paths.test;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.kinematics.systemdef.VelocityConstraint;
import frc.robot.autonomous.actions.MotionCommand;
import frc.robot.autonomous.paths.AutonomousPath;

public class TestMotionControl extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {
        SequentialCommandGroup output = new SequentialCommandGroup();

        output.addCommands(new MotionCommand(new Pose2d(3.0, 0.0, Rotation2d.fromDegrees(0.0)), 0.3, 1.0,
                new VelocityConstraint(1.0, 1.0), new Pose2d(0.1, 0.1, Rotation2d.fromDegrees(5.0))));
        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        // TODO Auto-generated method stub
        return new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0));
    }

}