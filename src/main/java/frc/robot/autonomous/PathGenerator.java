package frc.robot.autonomous;

import java.util.List;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;

public class PathGenerator {

	public static class SpeedConstraint {
		public double maxSpeedPercent;
		public double maxAccelPercent;

		public SpeedConstraint(double maxSpeedPercent, double maxAccelPercent) {
			this.maxAccelPercent = maxAccelPercent;
			this.maxSpeedPercent = maxSpeedPercent;

		}
	}

	public static SequentialCommandGroup generate(Pose2d start, Pose2d end,
			SpeedConstraint constraint) {

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


		// Creates a trajectory to follow
		Trajectory trajectory = TrajectoryGenerator.generateTrajectory(new Pose2d(0, 0, new Rotation2d(0)),
        // Pass through these two interior waypoints, making an 's' curve path
        List.of(
            new Translation2d(1, 0),
            new Translation2d(2, 0)
        ),
        // End 3 meters straight ahead of where we started, facing forward
        new Pose2d(3, 0, new Rotation2d(0)), config);
		// Trajectory trajectory = TrajectoryGenerator.generateTrajectory(List.of(start, end), config);

		// returns a new command that follows the trajectory
		return new RamseteCommand(trajectory, DriveTrain.getInstance()::getPosition,
				new RamseteController(RobotConstants.ControlGains.kRamseteB, RobotConstants.ControlGains.kRamseteZeta),
				feedforward, RobotConstants.ControlGains.kDriveKinematics, DriveTrain.getInstance()::getWheelSpeeds,
				new PIDController(RobotConstants.ControlGains.kPDriveVel, RobotConstants.ControlGains.kIDriveVel, RobotConstants.ControlGains.kDDriveVel),
				new PIDController(RobotConstants.ControlGains.kPDriveVel, RobotConstants.ControlGains.kIDriveVel, RobotConstants.ControlGains.kDDriveVel), DriveTrain.getInstance()::setVoltage,
				DriveTrain.getInstance()).andThen(DriveTrain.getInstance()::stop);

	}

}