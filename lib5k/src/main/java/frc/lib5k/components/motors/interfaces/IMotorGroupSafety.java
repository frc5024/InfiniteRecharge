package frc.lib5k.components.motors.interfaces;

/**
 * Common interface for groups of motors with safety features
 */
public interface IMotorGroupSafety {

    /**
     * Set the motorsafety mode of the master motor controller
     * 
     * @param enabled MotorSafety enabled
     */
    public void setMasterMotorSafety(boolean enabled);

}