package frc.lib5k.components.drive;

import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.utils.Mathutils;

/**
 * Helpers for calculating differential drive kinematics
 */
public class DifferentialDriveCalculation {

    /**
     * Normalize a percent output DriveSignal
     * 
     * @param signal Input signal
     * @return Normalized signal
     */
    public static DriveSignal normalize(DriveSignal signal) {

        // Find the maximum magnitude between both wheels
        double magnitude = Math.max(Math.abs(signal.getL()), Math.abs(signal.getR()));

        // Scale back motors if the max magnitude is greater than the max output (1.0)
        if (magnitude > 1.) {
            signal.setL(signal.getL() / magnitude);
            signal.setR(signal.getR() / magnitude);
        }

        return signal;
    }

    /**
     * Calculate a percent motor output from speed and rotation inputs using
     * "semi-constant curvature" calculation
     * 
     * @param speed    Desired speed
     * @param rotation Desired rotation
     * @return Computed DriveSignal
     */
    public static DriveSignal semiConstCurve(double speed, double rotation) {

        // Stop speed from being NaN
        if (Double.isNaN(speed)) {
            speed = 0.0000001;
        }

        // Stop speed from being NaN
        if (Double.isNaN(rotation)) {
            rotation = 0.0000001;
        }

        // Calculate direct speed conversion (rate-based turning)
        double rate_l = (speed + rotation);
        double rate_r = (speed - rotation);

        // Calculate constant-curvature speeds
        double curv_l = (speed + Math.abs(speed) * rotation);
        double curv_r = (speed - Math.abs(speed) * rotation);

        // Determine average speeds
        double avg_l = (rate_l + curv_l) / 2;
        double avg_r = (rate_r + curv_r) / 2;

        // Clamp motor outputs
        avg_l = Mathutils.clamp(avg_l, -1., 1.);
        avg_r = Mathutils.clamp(avg_r, -1., 1.);

        // Create a DriveSignal from motor speeds
        DriveSignal signal = new DriveSignal(avg_l, avg_r);


        // Return the drive signal
        return signal;
    }

    /**
     * Calculate a percent motor output from speed and rotation inputs using "arcade
     * calculation"
     * 
     * @param speed    Desired speed
     * @param rotation Desired rotation
     * @return Computed DriveSignal
     */
    public static DriveSignal arcade(double speed, double rotation) {

        // Stop speed from being NaN
        if (Double.isNaN(speed)) {
            speed = 0.0000001;
        }

        // Stop speed from being NaN
        if (Double.isNaN(rotation)) {
            rotation = 0.0000001;
        }

        // Calculate direct speed conversion (rate-based turning)
        double l = (speed + rotation);
        double r = (speed - rotation);

        // Clamp motor outputs
        l = Mathutils.clamp(l, -1., 1.);
        r = Mathutils.clamp(r, -1., 1.);

        // Create a DriveSignal from motor speeds
        DriveSignal signal = new DriveSignal(l, r);

        // Normalize the signal
        signal = normalize(signal);

        // Return the drive signal
        return signal;
    }

}