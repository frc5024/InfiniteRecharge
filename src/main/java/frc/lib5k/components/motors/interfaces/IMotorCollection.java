package frc.lib5k.components.motors.interfaces;

import edu.wpi.first.wpilibj.SpeedController;

/**
 * Common interface for any collection of motor controllers
 */
public interface IMotorCollection extends SpeedController {

    /**
     * Only set on new data
     * 
     * @param speed Motor speed
     */
    public void setBuffer(double speed);
}