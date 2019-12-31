package frc.lib5k.components.sensors;

public interface IEncoderProvider {

    /**
     * Get the default encoder
     * 
     * @return Default encoder
     */
    public EncoderBase getDefaultEncoder();

    /**
     * Get an encoder of a specific ID. This is useful if a system has more than one
     * sensor, or if it has more than one input, but only one sensor.
     * 
     * If the system only has one sensor, this should probably just return
     * getDefaultEncoder()
     * 
     * @param id Encoder ID
     * @return Encoder
     */
    public EncoderBase getEncoder(int id);
}