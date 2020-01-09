package frc.robot.autonomous.helpers;


import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotConstants;
import frc.robot.autonomous.helpers.EasyTrajectory;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.subsystems.DriveTrain;

public class PathGenerator {

	

	public static SequentialCommandGroup generate(EasyTrajectory t, SpeedConstraint constraint) {

		SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(RobotConstants.ControlGains.ksVolts,
				RobotConstants.ControlGains.kvVoltsSecondsPerMeter,
				RobotConstants.ControlGains.kaVoltsSecondsSquaredPerMeter);

		// Creates constrains for motor controller voltage
		DifferentialDriveVoltageConstraint voltageConstraint = new DifferentialDriveVoltageConstraint(feedforward,
				RobotConstants.ControlGains.kDriveKinematics, 10);

		// Creates configuration for trajectory
		TrajectoryConfig config = new TrajectoryConfig(
				RobotConstants.ControlGains.kMaxSpeedMetersPerSecond * constraint.maxSpeedPercent,
				RobotConstants.ControlGains.kMaxAccelerationMetersPerSecondSquared * constraint.maxAccelPercent)
						.setKinematics(RobotConstants.ControlGains.kDriveKinematics).addConstraint(voltageConstraint);

		// Determine trajectory type
		Trajectory trajectory;

		if (t.isQuintic) {
			// Configure Quintic spline
			trajectory = TrajectoryGenerator.generateTrajectory(t.getABSPoints(), config);
		} else {
			// Configure cubic spline
			trajectory = TrajectoryGenerator.generateTrajectory(t.getABSPoints().get(0), t.getInteriorPoints(),
					t.getABSPoints().get(0), config);
		}

		// returns a new command that follows the trajectory
		return new RamseteCommand(trajectory, DriveTrain.getInstance()::getPosition,
				new RamseteController(RobotConstants.ControlGains.kRamseteB, RobotConstants.ControlGains.kRamseteZeta),
				feedforward, RobotConstants.ControlGains.kDriveKinematics, DriveTrain.getInstance()::getWheelSpeeds,
				new PIDController(RobotConstants.ControlGains.kPDriveVel, RobotConstants.ControlGains.kIDriveVel,
						RobotConstants.ControlGains.kDDriveVel),
				new PIDController(RobotConstants.ControlGains.kPDriveVel, RobotConstants.ControlGains.kIDriveVel,
						RobotConstants.ControlGains.kDDriveVel),
				DriveTrain.getInstance()::setVoltage, DriveTrain.getInstance()).andThen(DriveTrain.getInstance()::stop);

	}

}