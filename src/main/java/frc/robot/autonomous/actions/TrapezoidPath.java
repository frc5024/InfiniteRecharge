package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.helpers.EasyTrajectory;
import frc.robot.autonomous.helpers.PathGenerator;
import frc.robot.autonomous.helpers.SpeedConstraint;

public class TrapezoidPath extends SequentialCommandGroup {

    public TrapezoidPath(Pose2d start, Translation2d mid1, Translation2d mid2, Pose2d end,
            SpeedConstraint constraints) {

        // Determine angle from start to mid1
        Rotation2d startToMid1Theta = new Rotation2d(Math.atan2(mid1.getY(), mid1.getX()));

        // Add a Turn-to to get to start position
        addCommands(new TurnToCommand(startToMid1Theta, 2.0));

        // Get from start to mid1
        addCommands(PathGenerator.generate(new EasyTrajectory(start, new Pose2d(mid1, startToMid1Theta))));

        // Determine angle from mid1 to mid2
        Rotation2d mid1ToMid2Theta = new Rotation2d(
                Math.atan2((mid2.getY() - mid1.getY()), (mid2.getX() - mid1.getX())));

        // Add a Turn-to to face mid2
        addCommands(new TurnToCommand(mid1ToMid2Theta, 2.0));

        // Get from mid1 to mid2
        addCommands(PathGenerator
                .generate(new EasyTrajectory(new Pose2d(mid1, mid1ToMid2Theta), new Pose2d(mid2, mid1ToMid2Theta))));

        // Determine angle from mid2 to end
        Rotation2d mid2ToEndTheta = new Rotation2d(
                Math.atan2((end.getTranslation().getY() - mid2.getY()), (end.getTranslation().getX() - mid2.getX())));

        // Add a Turn-to to face end
        addCommands(new TurnToCommand(mid2ToEndTheta, 2.0));

        // Get from mid2 to end
        addCommands(PathGenerator.generate(new EasyTrajectory(new Pose2d(mid2, mid2ToEndTheta),
                new Pose2d(end.getTranslation(), mid2ToEndTheta))));
    }

}