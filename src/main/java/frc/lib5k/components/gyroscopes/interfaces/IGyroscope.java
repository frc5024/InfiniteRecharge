package frc.lib5k.components.gyroscopes.interfaces;

public interface IGyroscope {

    /**
     * Get the gyroscope angle (non-wrapping)
     * 
     * @return Gyro angle
     */
    public double getAngle();

    /**
     * Get the gyroscope angle, wrapped by 360 degrees
     * 
     * @return Wrapped gyro angle
     */
    public double getWrappedAngle();
}