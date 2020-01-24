package frc.lib5k.utils;

import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import frc.lib5k.utils.Mathutils;

public class ColorUtils {


    /**
     * Finds the difference of the color between the color a and color b.
     * 
     * @param a Color 1
     * @param b Color 2
     * @param eps Threshold
     * @return boolean if its about the same color.  
     */
    public static boolean epsilonEquals(Color8Bit a, Color8Bit b, double eps){
        return (Mathutils.epsilonEquals(a.red, b.red, eps) && Mathutils.epsilonEquals(a.green, b.green, eps) && Mathutils.epsilonEquals(a.blue, b.blue, eps));
    }


}