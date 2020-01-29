package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.LinePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.autonomous.actions.DriveDistance;
import frc.robot.autonomous.actions.DriveToCommand;

// Built with Point Interpeter
public class ScoreGetBalls extends AutonomousPath {
    
    // TODO Document
    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(3.1, -3.28, Rotation2d.fromDegrees(152));
    }



    @Override
    protected SequentialCommandGroup getCommand() {
        // Create output command group
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Ensure robot is facing the correct angle at the start
        output.addCommands(new TurnToCommand((152), 2));

        // Auto goes here
        
	output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(1), 2));

	output.addCommands(new DriveToCommand(new Pose2d(5.28, -3.26, Rotation2d.fromDegrees(1)),new SpeedConstraint(1, 1), false));

	output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 2));

	output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(1), 2));

	output.addCommands(new DriveToCommand(new Pose2d(7.99, -3.22, Rotation2d.fromDegrees(1)),new SpeedConstraint(1, 1), false));

	output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(0), 2));

    return output;
    }

    
}