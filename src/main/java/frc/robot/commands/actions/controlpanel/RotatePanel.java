package frc.robot.commands.actions.controlpanel;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.utils.Mathutils;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.PanelManipulator;

public class RotatePanel extends CommandBase {

    // Count of rotations to rotate panel
    private double rotations = 3;

    // Epsilon on robot movement
    private Translation2d robotEpsilon;

    // Starting robot pose
    private Translation2d robotStartPose;

    public RotatePanel(double rotations, Translation2d robotEpsilon) {
        this.rotations = rotations;
        this.robotEpsilon = robotEpsilon;

    }

    @Override
    public void initialize() {

        // Read the robot's pose for auto-kill reference
        robotStartPose = DriveTrain.getInstance().getPosition().getTranslation();

        // Tell the panel manipulator to rotate the panel
        PanelManipulator.getInstance().rotateTo(rotations);

    }

    @Override
    public void end(boolean interrupted) {
        // Stop the manipulator
        PanelManipulator.getInstance().stop();
    }

    @Override
    public boolean isFinished() {
        // Check for the robot leaving the trench
        Translation2d robotPose = DriveTrain.getInstance().getPosition().getTranslation();
        boolean hasRobotLeftTrench = Mathutils.epsilonEquals(robotPose.getX(), robotStartPose.getX(),
                robotEpsilon.getX())
                || Mathutils.epsilonEquals(robotPose.getY(), robotStartPose.getY(), robotEpsilon.getY());

        // Check for system finish
        boolean systemFinished = PanelManipulator.getInstance().isIdle();

        return systemFinished; //|| !hasRobotLeftTrench;
    }
}