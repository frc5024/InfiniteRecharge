package frc.robot.autonomous.helpers;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
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

/**
 * Trajectory-based path follower command generator. For generating WPILib command to drive the robot form point to point
 */
public class PathGenerator {

	/**
	 * Generate a path following command group from a trajectory
	 * 
	 * @param t Trajectory to follow
	 * @return generated path following command
	 */
	public static SequentialCommandGroup generate(EasyTrajectory t) {
		return generate(t, new SpeedConstraint(1.0, 1.0));
	}

	/**
	 * Generate a path following command group from a trajectory and a constraint
	 * 
	 * @param t Trajectory to follow
	 * @param constraint trajectory constraints
	 * @return generated path following command
	 */
	public static SequentialCommandGroup generate(EasyTrajectory t, SpeedConstraint constraint) {
		return generate(t, constraint, false);
	}

	/** 
	 * 
	 * @param t	Trajectory to follow
	 * @param constraint Trajectory constraints
	 * @param reversed Should the path be reversed
	 * @return generated path following command
	 */
	public static SequentialCommandGroup generate(EasyTrajectory t, SpeedConstraint constraint, boolean reversed){
		return generate(t, constraint, reversed , true);
	}



	
	/**
	 * 
	 * @param t	Trajectory to follow
	 * @param constraint Trajectory constraints
	 * @param reversed Should the path be reversed
	 * @param stop Should the path stop after
	 * @return generated path following command
	 */
	public static SequentialCommandGroup generate(EasyTrajectory t, SpeedConstraint constraint, boolean reversed, boolean stop) {

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
		RamseteCommand ramseteCommand = new RamseteCommand(trajectory, () -> {
			Pose2d pose = DriveTrain.getInstance().getPosition();
			return new Pose2d(pose.getTranslation(), new Rotation2d(pose.getRotation().getRadians()));// - ((reversed) ?
																										// Math.PI :
																										// 0)));
		}, new RamseteController(RobotConstants.ControlGains.kRamseteB, RobotConstants.ControlGains.kRamseteZeta),
				feedforward, RobotConstants.ControlGains.kDriveKinematics, DriveTrain.getInstance()::getWheelSpeeds,
				new PIDController(RobotConstants.ControlGains.kPDriveVel, RobotConstants.ControlGains.kIDriveVel,
						RobotConstants.ControlGains.kDDriveVel),
				new PIDController(RobotConstants.ControlGains.kPDriveVel, RobotConstants.ControlGains.kIDriveVel,
						RobotConstants.ControlGains.kDDriveVel),
				DriveTrain.getInstance()::setVoltage, DriveTrain.getInstance());

		return(stop ? ramseteCommand.andThen(DriveTrain.getInstance()::stop) : new SequentialCommandGroup(ramseteCommand));
	}

}