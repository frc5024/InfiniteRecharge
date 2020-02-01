package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.ramsete.MotionController;
import frc.lib5k.kinematics.systemdef.VelocityConstraint;
import frc.lib5k.kinematics.systemdef.VelocityDefinition;
import frc.robot.RobotConstants;

public class MotionCommand extends CommandBase {

    private Pose2d goal;
    private VelocityConstraint constraint;
    private MotionController controller;

    public MotionCommand(Pose2d goal, double turnRate, double maxTurn, VelocityConstraint constraint) {
        this.goal = goal;
        this.constraint = constraint;
        this.controller = new MotionController(new VelocityDefinition(RobotConstants.ControlGains.kMaxSpeedMetersPerSecond, RobotConstants.ControlGains.kMaxAccelerationMetersPerSecondSquared), turnRate, maxTurn, rotationP, distanceP)
        
    }

}