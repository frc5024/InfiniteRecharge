package frc.lib5k.components.motors.motorsensors;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import frc.lib5k.components.sensors.EncoderBase;

/**
 * Wrap a TalonSRX encoder as an EncoderBase
 */
public class TalonEncoder extends EncoderBase {

    private WPI_TalonSRX talon;

    public TalonEncoder(WPI_TalonSRX talon) {
        this.talon = talon;

    }

    @Override
    public int getRawTicks() {
        return talon.getSelectedSensorPosition();
    }

}