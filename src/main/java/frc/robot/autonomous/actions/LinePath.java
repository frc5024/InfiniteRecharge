package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.helpers.EasyTrajectory;
import frc.robot.autonomous.helpers.PathGenerator;
import frc.robot.autonomous.helpers.SpeedConstraint;

public class LinePath extends SequentialCommandGroup {

    public LinePath(Pose2d start, Pose2d end, SpeedConstraint constraints, boolean reversed) {

        double angleFlipOffset = (reversed) ? Math.PI : 0;

        // Determine angle from start to end
        Rotation2d theta = new Rotation2d(Math.atan2(end.getTranslation().getY() - start.getTranslation().getY(),
                end.getTranslation().getX() - start.getTranslation().getX()) - angleFlipOffset);

        addCommands(new LogCommand("THETA: " + theta));

        // Add a Turn-to to get to start position
        addCommands(new TurnToCommand(theta, 2.0));

        // Get from start to end
        addCommands(PathGenerator.generate(new EasyTrajectory(new Pose2d(start.getTranslation(), theta),
                end), constraints));

    }

}