package frc.lib5k.components.drive;

/**
 * Interface for a simple differential drivebase
 */
public interface IDifferentialDrivebase {

    /**
     * Get left side distance traveled in meters
     * @return Left distance
     */
    public double getLeftMeters();

    /**
     * Get right side distance traveled in meters
     * @return Right distance
     */
    public double getRightMeters();

    /**
     * Get track width in meters
     * @return Track width
     */
    public double getWidthMeters();

}