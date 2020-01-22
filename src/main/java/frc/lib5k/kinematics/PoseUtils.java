package frc.lib5k.kinematics;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

public class PoseUtils {

    /**
     * Rotate a Translation around it's origin by and angle
     * 
     * @param origin Translation
     * @param angle  Angle to rotate by
     * @return Rotated Translation
     */
    public static Translation2d rotate(Translation2d origin, Rotation2d angle) {

        // Find new coords
        double x = origin.getX() * Math.cos(angle.getRadians()) - origin.getY() * Math.sin(angle.getRadians());
        double y = origin.getX() * Math.sin(angle.getRadians()) + origin.getY() * Math.cos(angle.getRadians());

        // Return the new Translation
        return new Translation2d(x, y);
    }

    /**
     * Get the rotated error between two poses
     * 
     * @param origin Origin pose
     * @param target Target pose
     * @return Rotated error
     */
    public static Translation2d getRotatedError(Pose2d origin, Pose2d target) {

        // Rotate poses
        Translation2d rotatedOrigin = rotate(origin.getTranslation(), target.getRotation());
        Translation2d rotatedTarget = rotate(target.getTranslation(), target.getRotation());

        // Determine X and Y errors between origin and goal
        double x = rotatedTarget.getX() - rotatedOrigin.getX();
        double y = rotatedTarget.getY() - rotatedOrigin.getY();

        // Return the error
        return new Translation2d(x, y);

    }

}