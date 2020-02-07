package frc.lib5k.components.sensors.interfaces;

/**
 * Interface for binary sensors
 */
public interface IBinarySensor {

    /**
     * Get binary sensor reading
     * 
     * @return Is sensor on?
     */
    public boolean get();
}