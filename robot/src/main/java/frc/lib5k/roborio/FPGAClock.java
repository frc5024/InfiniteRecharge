package frc.lib5k.roborio;

import edu.wpi.first.wpilibj.Timer;

/**
 * Tools for interacting with the FPGA's high-precision clock
 */
public class FPGAClock {

    /**
     * Get the number of seconds since the robot timer started
     * 
     * @return Seconds since timer start
     */
    public static double getFPGASeconds() {
        return Timer.getFPGATimestamp();
    }

    /**
     * Get the number of milliseconds since the robot timer started
     * 
     * @return Milliseconds since timer start
     */
    public static double getFPGAMilliseconds() {
        return getFPGASeconds() * 100;
    }

    /**
     * A utility for anything that needs to toggle every N milliseconds (for
     * example, a blinking light)
     * 
     * @param period Number of milliseconds to wait before toggling the output
     * @return Output
     */
    public static boolean getMillisecondCycle(double period) {
        return ((getFPGAMilliseconds() % (period * 2)) - period) >= 0;
    }
}