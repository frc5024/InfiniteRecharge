package frc.lib5k.components.sensors;

import java.util.ArrayList;

import frc.lib5k.interfaces.PeriodicComponent;

/**
 * Base for encoders
 */
public abstract class EncoderBase implements PeriodicComponent {
    int encoder_offset;
    private int speed = 0;
    private int previous_ticks;
    private ArrayList<Integer> pastSpeeds = new ArrayList<Integer>();
    private final int MAX_READINGS = 5;

    /**
     * Get the raw sensor reading from encoder
     * 
     * @return Raw sensor reading
     */
    public abstract int getRawTicks();

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
        return (((double) getTicks() / tpr) * wheel_circumference) / 100.0;
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
        return (((double) getSpeed() / tpr) * wheel_circumference) / 100.0;
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