package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.lib5k.kinematics.purepursuit.PureFollower;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;

public class FollowPath extends CommandBase {

    // Path follower
    private PureFollower m_follower;

    // Path end epsilon
    private double m_eps;

    public FollowPath(Path path, double lookahead, double epsilon) {
        this.m_eps = epsilon;

        // Create a follower
        m_follower = new PureFollower(path, new PIDController(RobotConstants.ControlGains.kPDriveVel,
                RobotConstants.ControlGains.kIDriveVel, RobotConstants.ControlGains.kDDriveVel),
                RobotConstants.ControlGains.kRP, lookahead);
    }

    @Override
    public void initialize() {

        // Reset the follower
        m_follower.reset();
    }

    @Override
    public void execute() {

        // Determine current robot pose
        Pose2d currentPose = DriveTrain.getInstance().getPosition();

        // Calculate the needed speeds for the drivebase
        DriveSignal signal = m_follower.follow(currentPose, m_eps);

        // TODO: Maybe we should normalize the signal?

        // Apply the signal to the DriveTrain
        DriveTrain.getInstance().setOpenLoop(signal);
    }

    @Override
    public void end(boolean interrupted) {
        DriveTrain.getInstance().stop();
    }

    @Override
    public boolean isFinished() {
        return m_follower.atEnd();
    }
}