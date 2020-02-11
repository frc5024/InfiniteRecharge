package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.DriveDistance;
import frc.robot.autonomous.actions.DriveToCommand;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.paths.AutonomousPath;

/**
 * Score a lot of balls
 */
public class ScoreBig extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {
        // Commands
        SequentialCommandGroup output = new SequentialCommandGroup();

        // Some constants to make positioning easier
        double startx = getStartingPose().getTranslation().getX();
        double starty = getStartingPose().getTranslation().getY();
        Rotation2d startr = getStartingPose().getRotation();

        // Make sim happy
        output.addCommands(new TurnToCommand(startr));

        // Get to the opponent's balls
        output.addCommands(new DriveToCommand(new Pose2d(startx + 2, starty, startr)));

        // Get out of the trench
        output.addCommands(new DriveDistance(-2.0, 0.15, 0.9));

        // Turn at our next goal
        output.addCommands(new TurnToCommand(Rotation2d.fromDegrees(-72), 15));

        return null;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_GOAL_OPP, Rotation2d.fromDegrees(-180));
        
    }

}