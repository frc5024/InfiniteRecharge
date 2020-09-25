package frc.lib5k.drivebase;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

public interface IDrivebase {

    /**
     * Drive to a field-relative point
     * 
     * @param point Point
     */
    public void driveTo(Translation2d point);

    /**
     * Drive to a robot-relative point
     * 
     * @param point Point
     */
    public void driveTowards(Translation2d point);

    /**
     * Twist to an angle
     * 
     * @param angle Angle to face
     */
    public void twist(Rotation2d angle);

    /**
     * Face a field-relative point
     * 
     * @param point Point
     */
    public void face(Translation2d point);

    /**
     * Get the chassis location (field-relative)
     * 
     * @return Chassis location
     */
    public Translation2d getPoint();

    /**
     * Get the chassis heading (field-relative)
     * 
     * @return Chassis heading
     */
    public Rotation2d getHeading();

    /**
     * Get the chassis position (field-relative)
     * 
     * @return Chassis pose
     */
    public Pose2d getPosition();

    /**
     * Reset the chassis position (field-relative)
     * 
     * @param pose Chassis pose
     */
    public void setPosition(Pose2d pose);
}