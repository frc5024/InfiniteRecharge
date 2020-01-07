package frc.lib5k.components.gyroscopes;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.geometry.Rotation2d;

/**
 * A wrapper for the AHRS / NavX gyroscope
 */
public class NavX extends AHRS {

    private static NavX m_instance = null;

    private boolean inverted = false;

    public NavX() {
        this(Port.kMXP);
    }

    public NavX(SPI.Port port) {
        super(port);

    }

    /**
     * Get the Default NavX instance
     * 
     * @return NavX instance
     */
    public static NavX getInstance() {
        if (m_instance == null) {
            m_instance = new NavX();
        }

        return m_instance;
    }

    /**
     * Set if the NavX readings should be inverted
     * 
     * @param inverted Is NavX inverted?
     */
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    /**
     * Returns the heading of the robot.
     *
     * @return the robot's heading in degrees, from 180 to 180
     */
    public double getHeading() {
        return Math.IEEEremainder(getAngle(), 360) * (inverted ? -1.0 : 1.0);
    }

    @Override
    public double getRate() {
        return super.getRate() * (inverted ? -1.0 : 1.0);
    }

    /**
     * Get the gyro angle, wrapped by 360 degrees
     * 
     * @return Wrapped angle
     */
    public double getWrappedAngle() {
        return getAngle() % 360;
    }

    /**
     * Get the NavX heading as a Rotation2d object
     * 
     * @return Heading
     */
    public Rotation2d getRotation() {
        return Rotation2d.fromDegrees(getHeading());
    }

}