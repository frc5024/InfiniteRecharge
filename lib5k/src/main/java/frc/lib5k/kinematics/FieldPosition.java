package frc.lib5k.kinematics;

/**
 * Used to denote a robot position
 */
public class FieldPosition {
    double x, y, theta;

    /**
     * Copy constructor for a FieldPosition
     * 
     * @param position FieldPosition to copy from
     */
    public FieldPosition(FieldPosition position) {
        this(position.getX(), position.getY(), position.getTheta());
    }

    /**
     * A field-relative point in space (in meters)
     * 
     * @param x Left-right position from driverstation glass
     * @param y Forward position from driverstation glass
     */
    public FieldPosition(double x, double y) {
        this(x, y, 0);
    }

    /**
     * A field-relative point in space (in meters)
     * 
     * @param x     Left-right position from driverstation glass
     * @param y     Forward position from driverstation glass
     * @param theta Angle from driverstation glass
     */
    public FieldPosition(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }

    /**
     * Strafe distance
     */
    public double getX() {
        return x;

    }

    /**
     * Forward distance
     * 
     * @return
     */
    public double getY() {
        return y;
    }

    public double getTheta() {
        return theta;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    /**
     * Get the rotated 2D error from the robot's current location to a goal position
     * 
     * @param goalPosition Goal position (where the robot wants to be)
     * @return Error from current position to goal
     */
    public Error2D getRotatedError(FieldPosition goalPosition) {

        // Get the current and goal positions as Error2D objects
        Error2D currentPosition = new Error2D(this.getX(), this.getY());
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
     * Get a new field-relative position from origin + rel
     * 
     * @param origin Original field-relative point
     * @param rel    Point relative to origin to transform by
     * @return New point
     */
    public static FieldPosition transformBy(FieldPosition origin, FieldPosition rel) {
        return new FieldPosition(origin.getX() + rel.getY() * Math.cos(rel.getX() + origin.getTheta()),
                origin.getY() + rel.getY() * Math.cos(rel.getX() + origin.getTheta()),
                origin.getTheta() + rel.getTheta());
    }

    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", x, y, theta);
    }

}