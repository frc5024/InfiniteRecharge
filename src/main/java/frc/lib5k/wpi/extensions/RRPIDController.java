package frc.lib5k.wpi.extensions;

import edu.wpi.first.wpilibj.controller.PIDController;
import frc.lib5k.kinematics.PIDProfile;

/**
 * Extensions to WPILib's PIDController
 */
public class RRPIDController extends PIDController {

    /**
     * Create a PIDController from a PIDProfile
     * 
     * @param profile PID gains profile
     */
    public RRPIDController(PIDProfile profile) {
        this(profile.kp, profile.ki, profile.kd);
    }

    /**
     * Create a PIDController from a PIDProfile
     * 
     * @param profile PID gains profile
     * @param period  System period
     */
    public RRPIDController(PIDProfile profile, double period) {
        this(profile.kp, profile.ki, profile.kd, period);
    }

    public RRPIDController(double Kp, double Ki, double Kd) {
        super(Kp, Ki, Kd);
    }

    public RRPIDController(double Kp, double Ki, double Kd, double period) {
        super(Kp, Ki, Kd, period);
    }

}