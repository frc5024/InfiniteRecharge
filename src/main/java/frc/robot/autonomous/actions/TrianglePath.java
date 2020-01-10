package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.helpers.EasyTrajectory;
import frc.robot.autonomous.helpers.PathGenerator;
import frc.robot.autonomous.helpers.SpeedConstraint;

public class TrianglePath extends SequentialCommandGroup {

    public TrianglePath(Pose2d start, Translation2d mid, Pose2d end, SpeedConstraint constraints, boolean reversed) {

        double angleFlipOffset = (reversed) ? Math.PI : 0;

        // Determine angle from start to mid
        Rotation2d startToMidTheta = new Rotation2d(Math.atan2(mid.getY(), mid.getX()) - angleFlipOffset);

        // Add a Turn-to to get to start position
        addCommands(new TurnToCommand(startToMidTheta, 2.0));

        // Get from start to mid
        addCommands(
                PathGenerator.generate(new EasyTrajectory(start, new Pose2d(start.getTranslation().getX() + mid.getX(),
                        start.getTranslation().getY() + mid.getY(), startToMidTheta))));

        // Determine angle from mid to end
        Rotation2d midToEndTheta = new Rotation2d(
                Math.atan2((end.getTranslation().getY() - (start.getTranslation().getY() + mid.getY())),
                        (end.getTranslation().getX() - (start.getTranslation().getX() + mid.getX())))
                        - angleFlipOffset);

        // Add a Turn-to to face end
        addCommands(new TurnToCommand(midToEndTheta, 2.0));

        // // Get from mid2 to end
        addCommands(
                PathGenerator.generate(new EasyTrajectory(
                        new Pose2d(start.getTranslation().getX() + mid.getX(),
                                start.getTranslation().getY() + mid.getY(), midToEndTheta),
                        end)));
    }

}