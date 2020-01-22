package frc.robot.autonomous.paths.test;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.robot.autonomous.actions.FollowPath;
import frc.robot.autonomous.paths.AutonomousPath;

public class TestPathing extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Create a test path
        Path path = new Path(new Pose2d(), new Pose2d(1.0, 0.0, Rotation2d.fromDegrees(0)),
                new Pose2d(2.0, 1.0, Rotation2d.fromDegrees(45)), new Pose2d(3.0, 0.0, Rotation2d.fromDegrees(0)));
        
        // Add the path command
        output.addCommands(new FollowPath(path, 1.0, 0.1));
        
        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d();
    }

}