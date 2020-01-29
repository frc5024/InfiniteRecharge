package frc.lib5k.kinematics.purepursuit;

import edu.wpi.first.wpilibj.geometry.Rotation2d;

/**
 * A movement is an error in pose, denoted by a distance and angle
 */
public class Movement {

    private double distance;
    private Rotation2d heading;

    /**
     * Create a Movement
     * 
     * @param distance Distance error
     * @param degrees  Heading error in degrees
     */
    public Movement(double distance, double degrees) {
        this(distance, Rotation2d.fromDegrees(degrees));
    }

    /**
     * Create a Movement
     * 
     * @param distance Distance error
     * @param heading  Heading error
     */
    public Movement(double distance, Rotation2d heading) {
        this.distance = distance;
        this.heading = heading;
    }

    /**
     * Get the distance component
     * 
     * @return Distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Get the heading component
     * 
     * @return Heading
     */
    public Rotation2d getHeading() {
        return heading;
    }

}