package frc.lib5k.kinematics.systemdef;

/**
 * System definition for velocity, and acceleration
 */
public class VelocityDefinition {
    public double maxVelocity, maxAcceleration;

    /**
     * Create a VelocityDefinition
     * 
     * @param maxVelocity     Maximum system velocity
     * @param maxAcceleraiton Maximum system acceleration
     */
    public VelocityDefinition(double maxVelocity, double maxAcceleraiton) {
        this.maxVelocity = maxVelocity;
        this.maxAcceleration = maxAcceleraiton;
    }

}