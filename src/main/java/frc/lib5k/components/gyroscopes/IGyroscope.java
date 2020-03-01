package frc.lib5k.components.gyroscopes;

import edu.wpi.first.wpilibj.geometry.Rotation2d;

public interface IGyroscope {

    /**
     * Get the gyroscope's raw angle
     * 
     * @return Gyro angle
     */
    public double getAngle();

    /**
     * Get the gyroscope's wrapped heading
     * 
     * @return Wrapped angle
     */
    public double getHeading();

    /**
     * Get the gyroscope's angle expressed as a Rotation vector
     * 
     * @return Gyo Rotation
     */
    public Rotation2d getRotation();
}