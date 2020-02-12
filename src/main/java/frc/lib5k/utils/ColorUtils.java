package frc.lib5k.utils;

import edu.wpi.first.wpilibj.util.Color;
import frc.lib5k.utils.MathUtils;

public class ColorUtils {


    public static boolean epsilonEquals(Color a, Color b, double eps){
        return (MathUtils.epsilonEquals(a.red, b.red, eps) && MathUtils.epsilonEquals(a.green, b.green, eps) && MathUtils.epsilonEquals(a.blue, b.blue, eps));
    }


}