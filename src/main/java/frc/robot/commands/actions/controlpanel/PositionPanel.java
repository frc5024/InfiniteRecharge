package frc.robot.commands.actions.controlpanel;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.utils.MathUtils;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.PanelManipulator;

public class PositionPanel extends CommandBase {

    // Epsilon on robot movement
    private Translation2d robotEpsilon;

    // Starting robot pose
    private Translation2d robotStartPose;

    // Tracker for failed action
    private boolean failed = false;

    public PositionPanel(Translation2d robotEpsilon) {
        this.robotEpsilon = robotEpsilon;

    }

    @Override
    public void initialize() {

        // Read the robot's pose for auto-kill reference
        robotStartPose = DriveTrain.getInstance().getPosition().getTranslation();

        // Read the FMS color
        String fmsColor = DriverStation.getInstance().getGameSpecificMessage();

        // Fail is no FMS color
        if (fmsColor.equals("")) {
            failed = true;
            return;
        }

        // Tell the panel manipulator to rotate the panel
        PanelManipulator.getInstance().goToColor(fmsColor);

    }

    @Override
    public void end(boolean interrupted) {
        // Stop the manipulator
        PanelManipulator.getInstance().stop();

        // Reset the fail tracker
        failed = false;
    }

    @Override
    public boolean isFinished() {
        // Check for the robot leaving the trench
        Translation2d robotPose = DriveTrain.getInstance().getPosition().getTranslation();
        boolean hasRobotLeftTrench = MathUtils.epsilonEquals(robotPose.getX(), robotStartPose.getX(),
                robotEpsilon.getX())
                || MathUtils.epsilonEquals(robotPose.getY(), robotStartPose.getY(), robotEpsilon.getY());

        // Check for system finish
        boolean systemFinished = PanelManipulator.getInstance().isIdle();

        return systemFinished || hasRobotLeftTrench || failed;
    }
}