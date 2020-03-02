package frc.lib5k.kinematics;

/**
 * Constraint on a path speed
 */
public class SpeedConstraint {
    public double maxSpeedPercent;
    public double maxAccelPercent;

    /**
     * Create a SpeedConstraint
     * 
     * @param max Max speed and accel i
     */
    public SpeedConstraint(double max) {
        this(max, max);
    }

    /**
     * Create a SpeedConstraint
     * 
     * @param maxSpeedPercent Max robot speed percentage
     * @param maxAccelPercent Max robot acceleration percentage
     */
    public SpeedConstraint(double maxSpeedPercent, double maxAccelPercent) {
        this.maxAccelPercent = maxAccelPercent;
        this.maxSpeedPercent = maxSpeedPercent;

    }
}