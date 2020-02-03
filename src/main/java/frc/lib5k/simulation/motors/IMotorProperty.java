package frc.lib5k.simulation.motors;

public interface IMotorProperty {

    /**
     * Get the motor's fre-spinning maximum RPM
     * 
     * @return Max RPM
     */
    public double getMaxRPM();

    /**
     * Get the motor's free-spinning KV
     * 
     * @return KV
     */
    public double getKV();
}