package frc.lib5k.components.motors.interfaces;

/**
 * A common interface for devices that can be controlled with a desired output
 * voltage
 */
public interface IVoltageOutputController {

    /**
     * Set desired controller output in volts. Negative voltage will result in
     * reverse output
     * 
     * @param volts Controller output
     */
    public void setVoltage(double volts);

    /**
     * Estimate controller output voltage from speed
     * 
     * @return Controller output voltage
     */
    public double getEstimatedVoltage();

}