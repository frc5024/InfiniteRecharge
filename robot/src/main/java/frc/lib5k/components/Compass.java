package frc.lib5k.components;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.lib5k.kinematics.Error2D;
import frc.lib5k.kinematics.FieldPosition;
import frc.lib5k.spatial.LocalizationEngine;
import frc.lib5k.utils.Mathutils;

public class Compass extends SendableBase {
    FieldPosition point;

    /**
     * Create a Compass that points to the robot's location at time of creation
     */
    public Compass() {
        this(LocalizationEngine.getInstance().getRobotPosition());
    }

    /**
     * Create a Compass that points to a specific FieldPosition. The Compass
     * requires the LocalizationEngine to be active.
     * 
     * @param point FieldPosition to point to
     */
    public Compass(FieldPosition point) {
        this.point = point;

        // Handle sendable naming
        String name = getClass().getName();
        setName(name.substring(name.lastIndexOf('.') + 1));
    }

    public double getHeadingError() {
        // Read the robot's current location
        FieldPosition robotPos = LocalizationEngine.getInstance().getLocationObject();

        // Get the Error2D from the point
        Error2D error = getError(robotPos);

        // define theta
        double theta;

        // Make sure this next calculation is valid
        if (error.getY() != 0) {
            // Find theta
            theta = Math.toDegrees(Math.atan((error.getX() / error.getY())));
        } else {
            double xErr = error.getX();
            // Handle sharp turns
            if (xErr == 0) {
                theta = 0.0;
            } else if (xErr > 0) {
                theta = 90;
            } else {
                theta = 270;
            }

        }

        // Get robot heading
        double angle = robotPos.getTheta();

        // Deal with possible angle deadzone
        theta = Math.abs(theta);
        angle += (error.getX() >= 0) ? theta : -theta;

        // Handle angle binding
        // Some angles are not correctly calculated thanks to some constraints of math
        angle += (error.getY() < 0 && angle < 180) ? 90 : 0;
        angle -= (error.getY() < 0 && angle >= 180) ? 90 : 0;

        // Wrap the angle
        angle = Mathutils.wrapGyro(angle);

        // System.out.println(angle + " " + theta + " " + error.toString());

        // Return the calculated angle error
        // This must be negative
        return -Mathutils.getWrappedError(robotPos.getTheta(), angle);
    }

    /**
     * Get an Error2D object for the position error from the robot's position to the
     * Compass point
     * 
     * @return Error2D
     */
    public Error2D getError() {
        // Read the robot's current location
        FieldPosition robotPos = LocalizationEngine.getInstance().getLocationObject();

        // Return the error
        return getError(robotPos);

    }

    /**
     * Get an Error2D object for the position error from the robot's position to the
     * Compass point
     * 
     * @param robotPos Robot's current FieldPosition
     * @return Error2D
     */
    public Error2D getError(FieldPosition robotPos) {
        // Find the X and Y errors
        double xErr = point.getX() - robotPos.getX();
        double yErr = point.getY() - robotPos.getY();

        // Return as an Error2D
        return new Error2D(xErr, yErr);
    }

    /**
     * Set the Compass point
     * 
     * @param point FieldPosition
     */
    public void setPoint(FieldPosition point) {
        this.point = point;
    }

    /**
     * Get the Compass point
     * 
     * @return Point the Compass is pointing to
     */
    public FieldPosition getPoint() {
        return this.point;
    }

    /**
     * Make WPIlib think this is a Gyroscope, so it can be displayed as a compass
     * view in Shuffleboard
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Gyro");
        builder.addDoubleProperty("Value", this::getHeadingError, null);
    }
}