package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.autonomous.actions.DriveDistance;
import frc.robot.autonomous.actions.DriveToCommand;

/**
 * Slow path starting on the right of the line
 */
public class ScoreRight extends AutonomousPath {

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_GOAL, Rotation2d.fromDegrees(180));
    }


    @Override
    protected SequentialCommandGroup getCommand() {

        // Create output command group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Some constants to make positioning easier
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();
        

        // Ensure robot is facing the correct angle at the start
        output.addCommands(new TurnToCommand((180), 2));


        // aligns to target
        output.addCommands(new VisionAlign(Rotation2d.fromDegrees(180), 2.0));

        // This is where the ball shooting would happen
        
        // Rotates to face the trench
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(-45), 2));

        // Drives towards trench
        // output.addCommands(new LinePath(new Pose2d(startx, starty, Rotation2d.fromDegrees(-45)),
        //         new Pose2d(startx + 1.4, starty - 1.4, Rotation2d.fromDegrees(-45)), false));

        output.addCommands(new DriveToCommand(new Pose2d(startx + 1.4, starty - 1.4, Rotation2d.fromDegrees(-45)),
                 new SpeedConstraint(.5, 1), false));

        // Rotates to face balls
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 2));

        // Drives over balls
        // output.addCommands(new LinePath(new Pose2d(startx + 1.4, starty - 1.4, Rotation2d.fromDegrees(0)),
        //          new Pose2d(startx + 3, starty - 1.4, Rotation2d.fromDegrees(0)), 
        //          new SpeedConstraint(.5, .5) ,false));
        output.addCommands(new DriveToCommand(new Pose2d(startx + 3, starty - 1.6, Rotation2d.fromDegrees(0)),
                new SpeedConstraint(0.3, 0.6), false));



        
        
        return output;
    }

}