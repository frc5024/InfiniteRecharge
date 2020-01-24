package frc.lib5k.components;


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
    public boolean isReadingEqual( Color8Bit c,  double eps) {
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
        return ColorUtils.epsilonEquals(new Color8Bit(getColor()), new Color8Bit(c), eps);
    }

    /**
     * Return a sensed colour without the weird magnitude.
     */
    public Color8Bit getSensedColor8Bit() {
        int r = getRed();
        int g = getGreen();
        int b = getBlue();
        return new Color8Bit(r, g, b);
    }

    /**
     * Returns a Color objects instead of a Color8Bit.
     * @return
     */
    public Color getSensedColor() {
        int r = getRed();
        int g = getGreen();
        int b = getGreen();
        return new Color(r, g, b);
    }

    /**
     * Return the proximity of the sensor.
     * Math to convert for 0-2047 to 0-10cm
     */
    @Override
    public int getProximity() {


        return super.getProximity();
    }

    
}