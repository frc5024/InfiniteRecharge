package frc.lib5k.kinematics.systemdef;

/**
 * Constraint on velocity
 */
public class VelocityConstraint {
    public double maxSpeedPercent;
    public double maxAccelPercent;

    /**
     * Create a VelocityConstraint
     * 
     * @param max Max speed and accel i
     */
    public VelocityConstraint(double max) {
        this(max, max);
    }

    /**
     * Create a VelocityConstraint
     * 
     * @param maxSpeedPercent Max robot speed percentage
     * @param maxAccelPercent Max robot acceleration percentage
     */
    public VelocityConstraint(double maxSpeedPercent, double maxAccelPercent) {
        this.maxAccelPercent = maxAccelPercent;
        this.maxSpeedPercent = maxSpeedPercent;

    }

    /**
     * Get an absolute velocity definition from a reference velocity
     * 
     * @param ref Reference (max) velocity
     * @return Absolute velocity definition
     */
    public VelocityDefinition asAbsolute(VelocityDefinition ref) {
        return new VelocityDefinition(ref.maxVelocity * maxSpeedPercent, ref.maxAcceleration * maxAccelPercent);
    }
}