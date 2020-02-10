package frc.robot.autonomous.paths.test;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.actions.DriveDistance;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.paths.AutonomousPath;

public class TestPID extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {

        SequentialCommandGroup output = new SequentialCommandGroup();

        

        // output.addCommands(new LinePath(getStartingPose(), new Pose2d(0, 0,
        // Rotation2d.fromDegrees(0)), true));
        output.addCommands(new DriveToCommand(new Pose2d(4, 0, Rotation2d.fromDegrees(0))));


        // output.addCommands(new TurnToCommand(90));
        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(3, 0, Rotation2d.fromDegrees(0));
    }

}