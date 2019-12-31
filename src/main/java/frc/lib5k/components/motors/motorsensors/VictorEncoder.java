package frc.lib5k.components.motors.motorsensors;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import frc.lib5k.components.sensors.EncoderBase;

/**
 * Wrap a VictorSPX encoder as an EncoderBase
 */
public class VictorEncoder extends EncoderBase {

    private WPI_VictorSPX victor;

    public VictorEncoder(WPI_VictorSPX victor) {
        this.victor = victor;

    }

    @Override
    public int getRawTicks() {
        return victor.getSelectedSensorPosition();
    }

}