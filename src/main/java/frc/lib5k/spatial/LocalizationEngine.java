package frc.lib5k.spatial;

import frc.lib5k.kinematics.Error2D;
import frc.lib5k.kinematics.FieldPosition;
import frc.lib5k.utils.Mathutils;

public class LocalizationEngine {
    /* Current instance */
    private static LocalizationEngine m_instance = null;

    /* Robot position information */
    private FieldPosition m_robotPosition;
    private double m_lastPosition = 0.0;

    /**
     * Initialize the {@link LocalizationEngine}
     */
    private LocalizationEngine() {
        m_robotPosition = new FieldPosition(0.0, 0.0, 0.0);
    }

    /**
     * Get the current {@link LocalizationEngine} instance
     * 
     * @return
     */
    public static LocalizationEngine getInstance() {
        if (m_instance == null) {
            m_instance = new LocalizationEngine();
        }

        return m_instance;
    }

    /**
     * Calculates the current robot location given some sensor readings. This should
     * be called at least once every 20ms
     * 
     * @param leftMeters  Total distance in meters traveled by the left side of the
     *                    robot
     * @param rightMeters Total distance in meters traveled by the right side of the
     *                    robot
     * @param heading     The robot's current heading/angle
     */
    public void calculate(double leftMeters, double rightMeters, double heading) {

        // Determine the total distance traveled by the robot
        double position = (leftMeters + rightMeters) / 2.0;

        // Determine the distance traveled since the last loop
        double distance = position - m_lastPosition;

        // Set the previous position
        m_lastPosition = position;

        // Get the last FieldPosition
        double lastX = m_robotPosition.getX();
        double lastY = m_robotPosition.getY();

        // Add the new location to the old location
        m_robotPosition.setX(lastX + (distance * Math.sin(Math.toRadians(heading))));
        m_robotPosition.setY(lastY + (distance * Math.cos(Math.toRadians(heading))));

        //TODO: a heading diffrence may be needed here
        // Set the robot heading
        m_robotPosition.setTheta(Mathutils.wrapGyro(heading));

        // System.out.println(m_robotPosition);

    }

    /**
     * Get the rotated 2D error from the robot's current location to a goal position
     * 
     * @param goalPosition Goal position (where the robot wants to be)
     * @return Error from current position to goal
     */
    public Error2D getRotatedError(FieldPosition goalPosition) {

        // Get the current position of the robot
        FieldPosition robotPosition = getRobotPosition();

        // Get the current and goal positions as Error2D objects
        Error2D currentPosition = new Error2D(robotPosition.getX(), robotPosition.getY());
        Error2D finalPosition = new Error2D(goalPosition.getX(), goalPosition.getY());

        // Rotate both Errors to the goal theta
        currentPosition.rotateBy(goalPosition.getTheta());
        finalPosition.rotateBy(goalPosition.getTheta());

        // Determine the X and Y errors
        double xError = finalPosition.getX() - currentPosition.getX();
        double yError = finalPosition.getY() - currentPosition.getY();

        // Return a new Error2D object containing the position error
        return new Error2D(xError, yError);

    }

    /**
     * Force-set the robot location
     * 
     * @param position Robot position
     */
    public void setRobotPosition(FieldPosition position) {
        m_robotPosition = position;
    }

    /**
     * Get the robot's current field-relative position
     * 
     * @return A COPY of the current position object
     */
    public FieldPosition getRobotPosition() {
        return new FieldPosition(m_robotPosition);
    }

    /**
     * Get a reference to the current {@link FieldPosition} object. This should only
     * be used if you know what you are doing.
     * 
     * @return Local object reference
     */
    public FieldPosition getLocationObject() {
        return m_robotPosition;
    }
}