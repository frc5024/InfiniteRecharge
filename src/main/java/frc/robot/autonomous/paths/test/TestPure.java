package frc.robot.autonomous.paths.test;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.paths.AutonomousPath;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.DrivePath;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.VisionAlign;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;
import frc.robot.autonomous.paths.AutonomousPath;

public class TestPure extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {

        SequentialCommandGroup output = new SequentialCommandGroup();

        output.addCommands(new DrivePath(new Path(getStartingPose().getTranslation(), new Translation2d(1,0)),  0.2,
        new Translation2d(0.2, 0.2), 0.025, 0.2));


        
        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(0, 0, Rotation2d.fromDegrees(0));
    }

}