package frc.lib5k.control;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.lib5k.utils.Mathutils;

public class BoostBackController {

    // Tracker for the system settling
    private boolean hasSettled = false;
    private Timer settleTimer = new Timer();

    // System constraints
    private double maxVoltage;
    private double maxRPM;
    private double kv;

    // Controller gains
    private double settleGain;
    private double boostGain;

    // Controller locals
    private double epsilon = 40.0;

    public BoostBackController(double maxVoltage, double maxRPM, double settleGain, double boostGain) {
        // Set system defs
        this.maxVoltage = maxVoltage;
        this.maxRPM = maxRPM;
        this.kv = maxRPM / maxVoltage;

        // Set gains
        this.settleGain = settleGain;
        this.boostGain = boostGain;

        // Reset timer
        this.settleTimer.reset();

    }

    public void configEpsilon(double epsilon) {
        this.epsilon = epsilon;

    }

    public double calculate(double desiredRPM, double currentRPM) {

        // Define an output
        double output;

        // Clamp the desired RPM to 0 and max
        output = MathUtil.clamp(desiredRPM, 0, maxRPM);

        // Determine a voltage output for this RPM
        output /= kv;

        // Check if we are within epsilon of our desired RPM
        if (Mathutils.epsilonEquals(desiredRPM, currentRPM, epsilon)) {
            
        }

        return 0.0;
    }

    public void reset() {

    }
}