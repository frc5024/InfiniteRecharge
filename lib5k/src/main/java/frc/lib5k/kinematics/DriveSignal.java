package frc.lib5k.kinematics;

/**
 * A collection of differential drivebase motor values
 */
public class DriveSignal {

    private double l, r;

    /**
     * Create a DriveSignal from values. These do not have to be direct outputs
     * 
     * @param l Left value
     * @param r Right value
     */
    public DriveSignal(double l, double r) {
        this.l = l;
        this.r = r;
    }

    /**
     * Complete a very simple solve from [speed, rotation] to [L, R] vectors. This
     * does not handle smoothing or normalization.
     * 
     * @param speed    Speed component
     * @param rotation Rotation component
     * @return DriveSignal
     */
    public static DriveSignal fromArcadeInputs(double speed, double rotation) {
        return new DriveSignal((rotation + speed), (speed - rotation));
    }

    /**
     * Get the right value
     * 
     * @return Right value
     */
    public double getR() {
        return r;
    }

    /**
     * Set the right value
     * 
     * @param r Right value
     */
    public void setR(double r) {
        this.r = r;
    }

    /**
     * Get the left value
     * 
     * @return Left value
     */
    public double getL() {
        return l;
    }

    /**
     * Set the left value
     * 
     * @param l Left value
     */
    public void setL(double l) {
        this.l = l;
    }

    @Override
    public String toString() {
        return String.format("<%.2f, %.2f>", l, r);
    }

}