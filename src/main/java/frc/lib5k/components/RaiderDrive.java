package frc.lib5k.components;

import frc.lib5k.control.CubicDeadband;
import frc.lib5k.control.SlewLimiter;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.utils.Mathutils;

public class RaiderDrive {
    private CubicDeadband m_speedDeadband;
    private CubicDeadband m_turnDeadband;
    private SlewLimiter m_speedFilter;

    /**
     * Create a RaiderDrive object.
     * 
     * RaiderDrive contains methods used by 5024 to convert joystick data to motor
     * outputs. This includes our movement smoothing system.
     * 
     * @param speedDeadband Deadband / modifier for forward-backward movement
     * @param turnDeadband  Deadband / modifier for rotational movement
     */
    public RaiderDrive(CubicDeadband speedDeadband, CubicDeadband turnDeadband) {

        // Set each deadband
        m_speedDeadband = speedDeadband;
        m_turnDeadband = turnDeadband;

        // Set the ramp rate to the suggested rate (what we used on HATCHfield)
        m_speedFilter = new SlewLimiter(0.2);

    }

    /**
     * Change the movement smoothing ramp rate
     * 
     * @param rate Maximum change in speed allowed
     */
    public void setRampRate(double rate) {
        m_speedFilter.setRate(rate);
    }

    public DriveSignal normalize(DriveSignal signal) {

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
     * Compute motor outputs in a similar way to WPILib's ArcadeDrive
     * 
     * @param speed    Desired robot speed (in percentage from -1 to 1)
     * @param rotation Desired turning rate (in percentage from -1 to 1)
     * @return DriveSignal Motor outputs
     */
    public DriveSignal computeArcade(double speed, double rotation) {

        // Slew limit speed
        speed = m_speedFilter.feed(speed);

        // Clamp the inputs
        speed = Mathutils.clamp(speed, -1., 1.);
        rotation = Mathutils.clamp(rotation, -1., 1.);

        // Apply deadbands
        speed = m_speedDeadband.feed(speed);
        rotation = m_turnDeadband.feed(rotation);

        // Create signal form control speeds
        DriveSignal signal = new DriveSignal(speed + rotation, speed - rotation);

        // Normalize the signal
        signal = normalize(signal);

        // Return the signal
        return signal;

    }

    /**
     * Compute motor outputs with a constant-curvature drive method. This results in
     * a very similar feel to 254's robots. The robot will drive like a car (and
     * cannot turn in place)
     * 
     * @param speed    Desired robot speed (in percentage from -1 to 1)
     * @param rotation Movement path curvature amount (in percentage from -1 to 1)
     * @return DriveSignal Motor outputs
     */
    public DriveSignal computeConstCurve(double speed, double rotation) {

        // Slew limit speed
        speed = m_speedFilter.feed(speed);

        // Clamp the inputs
        speed = Mathutils.clamp(speed, -1., 1.);
        rotation = Mathutils.clamp(rotation, -1., 1.);

        // Apply deadbands
        speed = m_speedDeadband.feed(speed);
        rotation = m_turnDeadband.feed(rotation);

        // Create signal form control speeds
        DriveSignal signal = new DriveSignal(speed + Math.abs(speed) * rotation, speed - Math.abs(speed) * rotation);

        // Normalize the signal
        signal = normalize(signal);

        // Return the signal
        return signal;

    }

    /**
     * Compute semi-constant-curvature drive. This will give the feel of a
     * constant-curvature drive, without removing the robot's ability to turn on the
     * spot
     * 
     * @param speed          Desired robot speed (in percentage from -1 to 1)
     * @param rotation       Movement path curvature amount (in percentage from -1
     *                       to 1)
     * @param enableDeadband Should the CubicDeadband be enabled?
     * @return DriveSignal Motor output
     */
    public DriveSignal computeSemiConst(double speed, double rotation, boolean enableDeadband) {

        // Optionally apply deadbands
        if (enableDeadband) {

            speed = Math.copySign(speed * speed, speed);
            rotation = Math.copySign(rotation * rotation, rotation);
        }

        // TODO: Move these to CubicDeadband
        // Stop speed from being NaN
        if (Double.isNaN(speed)) {
            speed = 0.0000001;
        }

        // Stop speed from being NaN
        if (Double.isNaN(rotation)) {
            rotation = 0.0000001;
        }

        // Calculate direct speed conversion (rate-based turning)
        double rate_l = speed + rotation;
        double rate_r = speed - rotation;

        // System.out.println(rate_l);

        // Calculate constant-curvature speeds
        double curv_l = speed + Math.abs(speed) * rotation;
        double curv_r = speed - Math.abs(speed) * rotation;

        // Determine average speeds
        double avg_l = (rate_l + curv_l) / 2;
        double avg_r = (rate_r + curv_r) / 2;

        // double avg_l = (2*rate_l + curv_l) / 3;
        // double avg_r = (2*rate_r + curv_r) / 3;

        // Clamp motor outputs
        avg_l = Mathutils.clamp(avg_l, -1., 1.);
        avg_r = Mathutils.clamp(avg_r, -1., 1.);

        // Create a DriveSignal
        DriveSignal signal = new DriveSignal(avg_l, avg_r);

        // Normalize the signal
        signal = normalize(signal);

        // Return the drive signal
        return signal;

    }

    public void reset() {
        m_speedFilter.reset();
    }

}