package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.kinematics.EasyTrajectory;
import frc.lib5k.kinematics.SpeedConstraint;
import frc.robot.autonomous.helpers.PathGenerator;

/**
 * A simple point-to-waypoint-to-point movement command.
 * 
 * This works by motion-profiling "desired" motor velocity control frames, then
 * sampling them, and comparing against the real motor data.
 * 
 * The TrianglePath will automatically get from the start pose to the end pose,
 * while finding the "best" way to pass through the midpoint
 */
public class TrianglePath extends SequentialCommandGroup {

	/**
	 * Generate a TrianglePath from a start pose, through a midpoint translation, to
	 * an end pose
	 * 
	 * @param start Starting pose (field-relative)
	 * @param mid   Midpoint translation (start-relative)
	 * @param end   End pose (field-relative)
	 */
	public TrianglePath(Pose2d start, Translation2d mid, Pose2d end) {
		this(start, mid, end, false);
	}

	/**
	 * Generate a TrianglePath from a start pose, through a midpoint translation, to
	 * an end pose
	 * 
	 * @param start    Starting pose (field-relative)
	 * @param mid      Midpoint translation (start-relative)
	 * @param end      End pose (field-relative)
	 * @param reversed Is the path to be followed backwards?
	 */
	public TrianglePath(Pose2d start, Translation2d mid, Pose2d end, boolean reversed) {
		this(start, mid, end, new SpeedConstraint(1, 1), reversed);
	}

	/**
	 * Generate a TrianglePath from a start pose, through a midpoint translation, to
	 * an end pose
	 * 
	 * @param start       Starting pose (field-relative)
	 * @param mid         Midpoint translation (start-relative)
	 * @param end         End pose (field-relative)
	 * @param constraints Constraints on profile speed
	 * @param reversed    Is the path to be followed backwards?
	 */
	public TrianglePath(Pose2d start, Translation2d mid, Pose2d end, SpeedConstraint constraints, boolean reversed) {

		double angleFlipOffset = (reversed) ? Math.PI : 0;

		// Determine angle from start to mid
		Rotation2d startToMidTheta = new Rotation2d(Math.atan2(mid.getY(), mid.getX()) - angleFlipOffset);

		// Add a Turn-to to get to start position
		addCommands(new TurnToCommand(startToMidTheta, 2.0));

		// Get from start to mid
		addCommands(
				PathGenerator.generate(new EasyTrajectory(start, new Pose2d(start.getTranslation().getX() + mid.getX(),
						start.getTranslation().getY() + mid.getY(), startToMidTheta)), constraints, false, false));

		// Determine angle from mid to end
		Rotation2d midToEndTheta = new Rotation2d(
				Math.atan2((end.getTranslation().getY() - (start.getTranslation().getY() + mid.getY())),
						(end.getTranslation().getX() - (start.getTranslation().getX() + mid.getX())))
						- angleFlipOffset);

		// Add a Turn-to to face end
		// addCommands(new TurnToCommand(midToEndTheta, 2.0));

		// // Get from mid2 to end
		addCommands(PathGenerator.generate(new EasyTrajectory(new Pose2d(start.getTranslation().getX() + mid.getX(),
				start.getTranslation().getY() + mid.getY(), midToEndTheta), end)));
	}

}