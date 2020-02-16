package frc.lib5k.components.sensors;

import java.util.ArrayList;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.SpeedController;
import frc.lib5k.control.SlewLimiter;
import frc.lib5k.interfaces.PeriodicComponent;
import frc.lib5k.roborio.FPGAClock;

/**
 * Base for encoders
 */
public abstract class EncoderBase implements PeriodicComponent {
    int encoder_offset;
    private int speed = 0;
    private int previous_ticks;
    private ArrayList<Integer> pastSpeeds = new ArrayList<Integer>();
    private final int MAX_READINGS = 5;

    /* Simulation vars */
    private SpeedController controller;
    private int tpr;
    private double last_time;
    private double gearbox_ratio, max_rpm;

    /* Simulation */
    private SimDevice m_simDevice;
    private SimDouble m_simTicks;
    private SimDouble m_simRotations;
    private static int s_instanceCount = 0;
    private SlewLimiter m_simSlew;

    public void initSimulationDevice(SpeedController controller, int tpr, double gearbox_ratio, double max_rpm, double ramp_time) {
        // Set locals
        this.controller = controller;
        this.tpr = tpr;
        this.gearbox_ratio = gearbox_ratio;
        this.max_rpm = max_rpm;
        this.m_simSlew = new SlewLimiter(ramp_time);

        // Init sim device
        m_simDevice = SimDevice.create("EncoderBase", s_instanceCount + 1);

        if (m_simDevice != null) {
            m_simTicks = m_simDevice.createDouble("Ticks", true, 0.0);
            m_simRotations = m_simDevice.createDouble("Rotations", true, 0.0);
        }

        // Move to next instance
        s_instanceCount++;
    }

    protected abstract int getSensorReading();

    /**
     * Get the raw sensor reading from encoder
     * 
     * @return Raw sensor reading
     */
    public int getRawTicks() {

        // If we are simulating, we can return the simulated tick value
        if (m_simDevice != null) {
            return (int) m_simTicks.get();
        }

        return getSensorReading();
    }

    /**
     * Get sensor reading accounting for offsets
     * 
     * @return Sensor reading
     */
    public int getTicks() {

        return getRawTicks() - encoder_offset;
    }

    /**
     * Get sensor distance traveled in meters
     * 
     * @param tpr                 Encoder ticks per revolution
     * @param wheel_circumference Circumference of wheel (or gear) attached to
     *                            encoder
     * @return Meters traveled
     */
    public double getMeters(int tpr, double wheel_circumference) {
        return (((double) getTicks() / tpr) * wheel_circumference);
    }

    /**
     * Get sensor distance traveled in meters since last cycle
     * 
     * @param tpr                 Encoder ticks per revolution
     * @param wheel_circumference Circumference of wheel (or gear) attached to
     *                            encoder
     * @return Meters traveled in last cycle
     */
    public double getMetersPerCycle(int tpr, double wheel_circumference) {
        return (((double) getSpeed() / tpr) * wheel_circumference);
    }

    /**
     * Set the current encoder position as "0"
     */
    public void zero() {
        encoder_offset = getRawTicks();
        speed = 0;
        previous_ticks = 0;
        pastSpeeds.clear();
    }

    /**
     * Reset the encoder "0" position
     */
    public void fullReset() {
        encoder_offset = 0;
    }

    /**
     * Get the speed of the encoder in Ticks per cycle (usually 20ms)
     * 
     * @return Rotation speed
     */
    public int getSpeed() {
        return speed;
    }

    public double getAverageSpeed() {
        double output = 0.0;

        for (Integer speed : pastSpeeds) {
            output += speed;
        }

        return output / pastSpeeds.size();
    }

    @Override
    public void update() {
        // Handle simulation updates
        if (m_simDevice != null) {
            // If this is the first loop, simply re-set the timer, and skip
            if (last_time == 0) {
                last_time = FPGAClock.getFPGASeconds();
                return;
            }

            // Determine dt
            double current_time = FPGAClock.getFPGASeconds();
            double dt = current_time - last_time;
            last_time = current_time;

            // Calc encoder position
            double rpm = (m_simSlew.feed(controller.get()) * max_rpm) / gearbox_ratio;
            double revs = (rpm / 60.0) * dt; // RPM -> RPS -> Multiply by seconds to find rotations since last update
            m_simTicks.set((int) (m_simTicks.get() + (revs * tpr)));
            m_simRotations.set((m_simRotations.get() + revs));
        }

        // Determine current speed
        int current_ticks = getTicks();
        speed = current_ticks - previous_ticks;
        previous_ticks = current_ticks;

        // Clean up average
        if (pastSpeeds.size() >= MAX_READINGS) {
            pastSpeeds.remove(0);
        }

        pastSpeeds.add(speed);

    }

}