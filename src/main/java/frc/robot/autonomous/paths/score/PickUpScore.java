package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.DrivePath;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;

/**
 * Start on the Center then scores then picks up
 */
public class PickUpScore extends AutonomousPath {

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_RIGHT, Rotation2d.fromDegrees(0));
    }

    @Override
    protected SequentialCommandGroup getCommand() {

        // Create output command group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // // Some constants to make positioning easier
        // double startx = getStartingPose().getTranslation().getX();
        // double starty = getStartingPose().getTranslation().getY();

        // // Ensure robot is facing the correct angle at the start
        // output.addCommands(new VisionAlign(Rotation2d.fromDegrees(-156), 2.00));

        // // This is where the ball shooting would happen
        // output.addCommands(new WaitCommand(3));

        // // Rotates to face the rendevous point
        // output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 3));

        // // Drives towards rendevous point
        // output.addCommands(new DriveToCommand(new Pose2d(startx + 1, starty, Rotation2d.fromDegrees(0)),
        //            new SpeedConstraint(1, 1), false, false)); 

        // // Drives to balls in rendevous point
        // output.addCommands(new DriveToCommand(new Pose2d(startx + 1.8, starty, Rotation2d.fromDegrees(0)),
        //                                         new SpeedConstraint(.5, .5), false));


        return output;
    }

}