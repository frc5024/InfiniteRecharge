package frc.robot.autonomous.helpers;

import edu.wpi.first.wpilibj2.command.PIDCommand;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.RamseteController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.components.gyroscopes.NavX;
import frc.lib5k.kinematics.DriveSignal;
import frc.robot.RobotConstants;
import frc.robot.autonomous.helpers.EasyTrajectory;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.subsystems.DriveTrain;

public class PathGenerator {

	/**
	 * Generate a path following command group from a trajectory and a constraint
	 * 
	 * @param t          Trajectory to follow
	 * @param constraint trajectory constraints
	 * @return generated path following command
	 */
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

	/**
	 * Generate a rotation controller command
	 * 
	 * @param desiredHeading Desired robot heading at the end of the command
	 * @param epsilon        Allowed error in rotation
	 * @return Turning command
	 */
	public static SequentialCommandGroup generateInPlaceRotation(double desiredHeading, double epsilon) {

		// Create a PIDController for turning control
		PIDController turnController = new PIDController(RobotConstants.ControlGains.kPTurnVel,
				RobotConstants.ControlGains.kITurnVel, RobotConstants.ControlGains.kDTurnVel);

		// Set controller limits
		turnController.setTolerance(epsilon);

		// Create a command that will rotate the drivebase to an angle with a PID
		// controller
		PIDCommand rotateCommand = new PIDCommand(turnController, () -> {
			return NavX.getInstance().getAngle();
		},  desiredHeading, (output) -> {
			DriveTrain.getInstance().setOpenLoop(new DriveSignal(output, -output));
		}, DriveTrain.getInstance()) {
			@Override
			public void execute() {
				super.execute();

				if (m_controller.atSetpoint()) {
					System.out.println("Ended turning");
					end(false);
					cancel();
				}
			}
		};
		

		// Return the command
		return rotateCommand.andThen(DriveTrain.getInstance()::stop);
	}

}