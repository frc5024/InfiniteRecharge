package frc.lib5k.utils;

import edu.wpi.first.wpilibj.util.Color;
import frc.lib5k.utils.Mathutils;

public class ColorUtils {


    public static boolean epsilonEquals(Color a, Color b, double eps){
        return (Mathutils.epsilonEquals(a.red, b.red, eps) && Mathutils.epsilonEquals(a.green, b.green, eps) && Mathutils.epsilonEquals(a.blue, b.blue, eps));
    }


}