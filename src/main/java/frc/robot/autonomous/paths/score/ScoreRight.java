package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.autonomous.actions.DriveDistance;

/**
 * Slow path starting on the right of the line
 */
public class ScoreRight extends AutonomousPath {

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_GOAL, Rotation2d.fromDegrees(0));
    }


    @Override
    protected SequentialCommandGroup getCommand() {

        // Create output command group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Some constants to make positioning easier
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();
        

        // Ensure robot is facing the correct angle at the start
        output.addCommands(new TurnToCommand(0));

        // Drives backwards 2.5 meters
        output.addCommands(new DriveDistance(-2.4, .01, 1));

        // This is where the ball shooting would happen
        
        // Rotates to face the trench
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(-8), 2));

        // Drives Forward
        output.addCommands(new LinePath(new Pose2d(startx - 2.4, starty, Rotation2d.fromDegrees(-8)),
                new Pose2d(startx + 1, starty - 2, Rotation2d.fromDegrees(-8)), false));


        // Rotates to face balls
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 1));

        // Drives into the ball
        // output.addCommands(new LinePath(new Pose2d(startx + 1, starty - 2, Rotation2d.fromDegrees(0)),
        //                 new Pose2d(startx + 3, starty - 2, Rotation2d.fromDegrees(0)),
        //                 false));

        // Return the command
        return output;
    }

}