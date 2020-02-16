package frc.lib5k.control;

/**
 * A tool for smoothing out joystick information (enforces a maximum rate of
 * change)
 */
public class SlewLimiter {
    double limit;
    double output = 0.0;

    /**
     * Built a SlewLimier object
     * 
     * @param limit Maximum amount of change allowed by the system (lower numbers
     *              will be more aggressive)
     */
    public SlewLimiter(double limit) {
        this.limit = limit;
    }

    /**
     * Limit a value, and update the system
     * 
     * @param value Input value
     * @return Output value
     */
    public double feed(double value) {
        double error = value - output;
        if (error > limit) {
            error = limit;
        } else if (error < (limit * -1)) {
            error = limit * -1;
        }
        output += error;
        return output;
    }

    /**
     * Reset the system (stops accidental movement after a system restart)
     */
    public void reset() {
        output = 0.0;
    }

    /**
     * Re-set the maximum amount of change allowed by the system
     * 
     * @param rate Maximum change
     */
    public void setRate(double rate) {
        limit = rate;
    }

    /**
     * Get the maximum amount of change allowed by the system
     * 
     * @return Maximum change
     */
    public double getRate() {
        return limit;
    }

}