package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;

/**
 * Scores from the Right side of the starting line
 * AKA our World Class Auto
 */
public class ScorePickupRight extends AutonomousPath {

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_RIGHT, Rotation2d.fromDegrees(156));
    }

    @Override
    protected SequentialCommandGroup getCommand() {

        // Create output command group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Some constants to make positioning easier
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();

        // Ensure robot is facing the correct angle at the start
        output.addCommands(new VisionAlign(Rotation2d.fromDegrees(156), 2.0));

        // Shoot Balls
        output.addCommands(new WaitCommand(3));

        // Turns to balls
        // output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 2.0));

        // Get to the front of the balls
        output.addCommands(new DriveToCommand(new Pose2d(startx + 1.6, starty, Rotation2d.fromDegrees(0)),
                new SpeedConstraint(1, 1), false, false));

        // Slowly drive through the balls while intaking
        output.addCommands(new DriveToCommand(new Pose2d(startx + 3.8, starty, Rotation2d.fromDegrees(0)),
                 new SpeedConstraint(0.5, 0.5), false));

        // Turns around
        //output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(180), 2.0));

        // Drives to shooting positon
        output.addCommands(new DriveToCommand(new Pose2d(startx + 2.4, starty, Rotation2d.fromDegrees(180)),
                 new SpeedConstraint(1, 1), false));
        
        // Aims at target
        output.addCommands(new VisionAlign(Rotation2d.fromDegrees(167), 2.0)); 



        // Return the command
        return output;
    }

}