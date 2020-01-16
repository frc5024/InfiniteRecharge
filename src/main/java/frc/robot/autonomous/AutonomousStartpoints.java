package frc.robot.autonomous;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.util.Units;
import frc.robot.FieldConstants;
import frc.robot.RobotConstants;

/**
 * Auton startpoints are calculated from the point at the centre of the robot's
 * drivebase. From the alliance wall, forward is positive X, and the Y axis
 * starts at the field centre. So, pressed against the right wall, facing away
 * from the drivers could be calculated as:
 * 
 * (FIELD_WIDTH / 2) - (DRIVEBASE_WIDTH / 2)
 */
public class AutonomousStartpoints {

    public static final Pose2d ZERO = new Pose2d(0, 0, Rotation2d.fromDegrees(0));

    /**
     * This auton startpoint is where the robot is sitting just past the opposing
     * side's sector line, against the right wall, facing towards the centre line of
     * the field.
     */
    public static final Pose2d SECTOR_LINE_RIGHT = new Pose2d(
            Units.inchesToMeters(120) - (RobotConstants.DriveTrain.Measurements.DRIVEBASE_LENGTH / 2)
                    - Units.inchesToMeters(8),
            ((FieldConstants.FIELD_WIDTH / 2) - RobotConstants.DriveTrain.Measurements.DRIVEBASE_WIDTH / 2
                    - Units.inchesToMeters(14)) * -1,
            Rotation2d.fromDegrees(0));
    // new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0));
    // new Pose2d(
    // FieldConstants.ALLIANCE_WALL_TO_FAR_SECTOR +
    // (RobotConstants.DriveTrain.Measurements.DRIVEBASE_WIDTH / 2),
    // ((FieldConstants.FIELD_WIDTH / 2) -
    // (RobotConstants.DriveTrain.Measurements.DRIVEBASE_LENGTH / 2)),
    // Rotation2d.fromDegrees(-90.0));
}