package frc.lib5k.control;

import frc.lib5k.roborio.FPGAClock;

/**
 * An extension of SlewLimiter that acts a bit more like TalonSRX's rampRate
 * setting, and respects non 20ms periods
 */
public class TimedSlewLimiter extends SlewLimiter {

    /* Locals */
    private boolean enabled;
    private double lastTime;

    /**
     * Create a TimedSlewLimiter
     * 
     * @param secondsToFull Minimum desired time to go from neutral to full output
     */
    public TimedSlewLimiter(double secondsToFull) {
        super(secondsToFull);
    }

    /**
     * Set the ramp rate
     * 
     * @param secondsToFull Minimum desired time to go from neutral to full output
     */
    @Override
    public void setRate(double secondsToFull) {
        super.setRate(secondsToFull);
    }

    /**
     * Enable or disable the system
     * 
     * @param enabled Should the system be enabled?
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (!enabled) {
            reset();
        }
    }

    /**
     * Limit a value, and update the system. NOTE: This will be much lower
     * resolution than the TalonSRX built-in limiter, but we have yet to have
     * problems with it.
     * 
     * @param value Input value
     * @return Output value
     */
    @Override
    public double feed(double value) {

        // Simply return the value if the limiter is disabled or there is no ramp
        if (!enabled || limit == 0.0) {
            return value;
        }

        // Find dt
        double dt = FPGAClock.getFPGASeconds() - lastTime;
        lastTime = FPGAClock.getFPGASeconds();

        // Determine allotted change for time step
        double max_change = (1 / limit) * dt;

        // Calculate slew
        double error = value - output;
        if (error > max_change) {
            error = max_change;
        } else if (error < (max_change * -1)) {
            error = max_change * -1;
        }
        output += error;

        return output;
    }

}