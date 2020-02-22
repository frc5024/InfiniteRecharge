package frc.lib5k.control;

import edu.wpi.first.wpilibj.Timer;

/**
 * A flywheel velocity controller designed by team 254, adapted and ported for
 * use by team 5024. <br>
 * <br>
 * 
 * To tune the controller, start with kLoadRatio at 1.0, kJ at a small non-zero
 * number, and kF at 0.0 (this is used to get to the setpoint faster)
 * 
 * <ul>
 * <li>If voltage dips between balls decrease, increase kJ.</li>
 * <li>Voltage can be compensated up set kLoadRatio to (setpoint -
 * lowestRPM)</li>
 * </ul>
 * 
 */
public class JRADController {

    private double kJ, kF, kLoadRatio;
    private double setpoint, lastVoltage;
    private Timer m_timer;
    private double m_prevTime = 0;
    // private double m_pre

    /**
     * Create a JRAD controller.
     * 
     * @param kJ         Compensation factor for sequential balls (should be a small
     *                   non-zero number)
     * @param kF         Multiplier to get to setpoint faster (Can probably be 0.0)
     * @param kLoadRatio A load ratio multiplier. This should be larger than 1.0
     */
    public JRADController(double kJ, double kF, double kLoadRatio) {
        this.kJ = kJ;
        this.kF = kF;
        this.kLoadRatio = kLoadRatio;

        // Setup the timer
        m_timer = new Timer();
        m_timer.reset();
        m_timer.start();

    }

    /**
     * Set a voltage setpoint for the controller
     * 
     * @param voltageSetpoint Voltage controller setpoint
     */
    public void setSetpoint(double voltageSetpoint) {
        this.setpoint = voltageSetpoint;
    }

    /**
     * Calculate a system output voltage
     * 
     * @param voltage Current system voltage
     * @return System output (in volts)
     */
    public double calculate(double voltage) {

        // Calculate the time difference
        double curTime = m_timer.get();
        double dt = curTime - m_prevTime;

        // Calculate the next voltage
        double nextVoltage = kF * setpoint  + kJ * dt * (kLoadRatio * setpoint - voltage);

        // Set the previous time
        m_prevTime = curTime;

        // Set the previous voltage
        lastVoltage = nextVoltage;

        return nextVoltage;

    }

}