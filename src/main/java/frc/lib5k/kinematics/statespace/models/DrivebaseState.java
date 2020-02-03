package frc.lib5k.kinematics.statespace.models;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.geometry.Twist2d;

public class DrivebaseState extends Pose2d {

    /**
     * Create a DrivebaseState identity {0,0,0}
     */
    public DrivebaseState() {
        this(0, 0, 0);
    }

    /**
     * Create a DrivebaseState from a coordinate
     * 
     * @param x X component
     * @param y Y component
     */
    public DrivebaseState(double x, double y) {
        this(x, y, 0);
    }

    /**
     * Create a DrivebaseState from a translation
     * 
     * @param t Translation
     */
    public DrivebaseState(Translation2d t) {
        this(t.getX(), t.getY(), 0);
    }

    /**
     * Create a DrivebaseState from a twist
     * 
     * @param t Twist
     */
    public DrivebaseState(Twist2d twist) {
        this(twist.dx, twist.dy, twist.dtheta);
    }

    /**
     * Create a DrivebaseState from a coordinate and angle
     * 
     * @param x       X component
     * @param y       Y component
     * @param degrees Angle in degrees
     */
    public DrivebaseState(double x, double y, double degrees) {
        this(x, y, Rotation2d.fromDegrees(degrees));
    }

    /**
     * Create a DrivebaseState from a coordinate and rotation
     * 
     * @param x   X component
     * @param y   Y component
     * @param rot Rotation
     */
    public DrivebaseState(double x, double y, Rotation2d rot) {
        this(new Translation2d(x, y), rot);
    }

    /**
     * Create a DrivebaseState from a translation and rotation
     * 
     * @param t   Translation
     * @param rot Rotation
     */
    public DrivebaseState(Translation2d trans, Rotation2d rot) {
        super(trans, rot);
    }

    /**
     * Get rear chassis translation
     * 
     * @param drivebaseWidth Drivebase width
     * @return Rear translation
     */
    public Translation2d getRear(double drivebaseWidth) {
        double rx = getTranslation().getX() - ((drivebaseWidth / 2) * Math.cos(getRotation().getRadians()));
        double ry = getTranslation().getY() - ((drivebaseWidth / 2) * Math.sin(getRotation().getRadians()));

        return new Translation2d(rx, ry);

    }

    /**
     * Get the distance from the chassis rear to a point
     * 
     * @param point          Point
     * @param drivebaseWidth Drivebase width
     * @return Distance
     */
    public double getDistance(Translation2d point, double drivebaseWidth) {

        // Determine the rear pose
        Translation2d rear = getRear(drivebaseWidth);

        // Calculate differences
        double dx = rear.getX() - point.getX();
        double dy = rear.getY() - point.getY();

        // Return the distance
        return Math.hypot(dx, dy);
    }
}