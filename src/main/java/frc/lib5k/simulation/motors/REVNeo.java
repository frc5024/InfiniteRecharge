package frc.lib5k.simulation.motors;

/**
 * REV Robotics NEO brushless motor
 */
public class REVNeo implements IMotorProperty {

    @Override
    public double getMaxRPM() {
        return 5700;
    }

    @Override
    public double getKV() {
        return 473;
    }

}