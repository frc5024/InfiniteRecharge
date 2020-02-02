package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.kinematics.ramsete.MotionController;
import frc.lib5k.kinematics.systemdef.VelocityConstraint;
import frc.lib5k.kinematics.systemdef.VelocityDefinition;
import frc.lib5k.utils.Mathutils;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;

public class MotionCommand extends CommandBase {

    private Pose2d goal, epsilon;
    private VelocityConstraint constraint;
    private MotionController controller;

    private PIDController leftVelController, rightVelController;

    public MotionCommand(Pose2d goal, double turnRate, double maxTurn, VelocityConstraint constraint, Pose2d epsilon) {
        this.goal = goal;
        this.epsilon = epsilon;
        this.constraint = constraint;
        this.controller = new MotionController(
                new VelocityDefinition(RobotConstants.ControlGains.kMaxSpeedMetersPerSecond,
                        RobotConstants.ControlGains.kMaxAccelerationMetersPerSecondSquared),
                turnRate, maxTurn, RobotConstants.ControlGains.kPTurnVel, 0.1);

        this.leftVelController = new PIDController(RobotConstants.ControlGains.kPDriveVel,
                RobotConstants.ControlGains.kIDriveVel, RobotConstants.ControlGains.kDDriveVel);
        this.rightVelController = new PIDController(RobotConstants.ControlGains.kPDriveVel,
                RobotConstants.ControlGains.kIDriveVel, RobotConstants.ControlGains.kDDriveVel);

    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {

        // Determine robot pose
        Pose2d robotPose = DriveTrain.getInstance().getPosition();
        // robotPose = new Pose2d(robotPose.getTranslation().getY(), robotPose.getTranslation().getX(), robotPose.getRotation());

        // Determine wheelspeeds
        DifferentialDriveWheelSpeeds speeds = controller.calculate(robotPose, goal, constraint);

        // Get current wheelspeeds
        DifferentialDriveWheelSpeeds current = DriveTrain.getInstance().getWheelSpeeds();

        // Calculate outputs from PID
        double leftADJ = leftVelController.calculate(current.leftMetersPerSecond, speeds.leftMetersPerSecond);
        double rightADJ = rightVelController.calculate(current.rightMetersPerSecond, speeds.rightMetersPerSecond);

        // Adjust the velocities
        leftADJ += current.leftMetersPerSecond;
        rightADJ += current.rightMetersPerSecond;

        // Convert to motor voltages
        leftADJ = (leftADJ / RobotConstants.ControlGains.kMaxSpeedMetersPerSecond) * 10;
        rightADJ = (rightADJ / RobotConstants.ControlGains.kMaxSpeedMetersPerSecond) * 10;

        // Write outputs to drivebase
        DriveSignal signal = new DriveSignal(leftADJ, rightADJ);
        // System.out.println(signal);
        DriveTrain.getInstance().setOpenLoop(signal);
    }

    @Override
    public void end(boolean interrupted) {
        DriveTrain.getInstance().stop();
    }

    @Override
    public boolean isFinished() {
        return Mathutils.epsilonEquals(DriveTrain.getInstance().getPosition(), goal, epsilon);
    }

}