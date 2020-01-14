package frc.lib5k.components.motors.interfaces;

/**
 * Common interface for devices that with current output controls
 */
public interface ICurrentController {

    /**
     * Configure a current limiting
     * 
     * @param threshold Threshold to trigger limit
     * @param hold      Amperage to hold the controller at while limiting
     * @param duration  How long the value must pass the threshold to be limited
     * @param timeout   Timeout (can be 0)
     */
    public void setCurrentLimit(int threshold, int duration, int hold, int timeout);

    public void setCompensation(boolean on);

    public void enableCurrentLimit(boolean on);
}