package frc.lib5k.components.sensors;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.lib5k.utils.ColorUtils;

/**
 * Extensions to the REV ColorSensor V3
 */
public class ColorSensor5k extends ColorSensorV3 {

    public ColorSensor5k(Port port) {
        super(port);

    }

    /**
     * Check if the sensor reading is roughly equal to a specified color
     * 
     * @param c   Color
     * @param eps Comparison epsilon
     * @return Is color equal?
     */
    public boolean isReadingEqual(Color8Bit c, double eps) {
        return isReadingEqual(new Color(c), eps);
    }

    /**
     * Check if the sensor reading is roughly equal to a specified color
     * 
     * @param c   Color
     * @param eps Comparison epsilon
     * @return Is color equal?
     */
    public boolean isReadingEqual(Color c, double eps) {
        return ColorUtils.epsilonEquals(getColor(), c, eps);
    }

}