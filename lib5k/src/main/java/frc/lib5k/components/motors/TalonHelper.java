package frc.lib5k.components.motors;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class TalonHelper {

    /**
     * Configure a WPI_TalonSRX current limiting
     * 
     * @param talon     Talon to configure
     * @param threshold Threshold to trigger limit
     * @param hold      Amperage to hold the controller at while limiting
     * @param duration  How long the value must pass the threshold to be limited
     * @param timeout   CAN timeout (can be 0)
     */
    public static void configCurrentLimit(WPI_TalonSRX talon, int threshold, int hold, int duration, int timeout) {
        talon.configPeakCurrentLimit(threshold, timeout);
        talon.configPeakCurrentDuration(duration, timeout);
        talon.configContinuousCurrentLimit(hold, timeout);
    }
}