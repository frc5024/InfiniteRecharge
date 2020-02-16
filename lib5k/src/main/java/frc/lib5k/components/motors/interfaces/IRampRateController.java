package frc.lib5k.components.motors.interfaces;

/**
 * A common interface for devices with configurable output ramp rates
 */
public interface IRampRateController {

    /**
     * Set the controller ramp rate.
     * 
     * @param secondsToFull Minimum desired time to go from neutral to full output.
     */
    public void setRampRate(double secondsToFull);

    /**
     * Get the configured ramp rate
     * 
     * @return Configured rate in seconds
     */
    public double getRampRate();

    /**
     * Set if ramp rate limiting should be enabled for the controller
     * 
     * @param enabled Should enable limiting?
     */
    public void enableRampRateLimiting(boolean enabled);

}