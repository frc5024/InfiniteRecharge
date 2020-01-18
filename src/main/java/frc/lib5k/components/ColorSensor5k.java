package frc.lib5k.components;


import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.lib5k.components.sensors.ColorSensor;
import frc.lib5k.utils.ColorUtils;
import frc.lib5k.utils.Mathutils;

/**
 * Extensions to the REV ColorSensor V3
 */
public class ColorSensor5k extends ColorSensor {

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

     public Color8Bit getSensedColor() {
        int r = getRed();
        int g = getGreen();
        int b = getBlue();
        return new Color8Bit(r, g, b);
    }


    /**
     * Return the proximity of the sensor. 
     */
    public double getProx() {
        double proximity = this.getProximity();

        // input_low = 0
        // input high = 2047
        // output low = 1cm
        // output high = 10cm
        return proximity;


    }
}