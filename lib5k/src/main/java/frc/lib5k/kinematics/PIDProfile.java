package frc.lib5k.kinematics;

/**
 * Used to store PID control data
 */
public class PIDProfile {
    public double kp, ki, kd;

    public PIDProfile(double kp) {
        this(kp, 0.0);
    }

    public PIDProfile(double kp, double ki) {
        this(kp, ki, 0.0);
    }

    public PIDProfile(double kp, double ki, double kd) {
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
    }

    /**
     * Generate a PIDProfile using the Zeigler-Nichols tuning method.
     * 
     * More info:
     * https://frc-pdr.readthedocs.io/en/latest/control/pid_control.html#tuning-methods
     * 
     * @param max_p      Maximum P to cause oscollation in the system
     * @param osc_period Period of oscillation. This must be a full cycle of the
     *                   system (in whatever unit. Generally, seconds)
     * @return Generated PIDProfile
     */
    public static PIDProfile autoConfig(double max_p, double osc_period) {
        return new PIDProfile(.6 * max_p, 1.2 * (max_p / osc_period), 3 * max_p * osc_period / 40);
    }

    /**
     * Modify the profile
     * 
     * @param mod Profile to add
     * @return This profile
     */
    public PIDProfile modify(PIDProfile mod) {
        this.kp += mod.kp;
        this.ki += mod.ki;
        this.kd += mod.kd;

        return this;
    }

}