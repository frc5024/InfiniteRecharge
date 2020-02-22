package frc.lib5k.utils;

import com.revrobotics.ColorMatch;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;

public class Color5k {

    // Internal color tracker
    private Color c;

    public Color5k(int r, int g, int b) {
        this.c = new Color(new Color8Bit(r, g, b));
    }

    public Color5k(double r, double g, double b) {
        this.c = ColorMatch.makeColor(r, g, b);
    }

    public double similarity(Color5k o) {
        return Math.sqrt((Math.pow(c.red - o.toColor().red, 2) + Math.pow(c.green - o.toColor().green, 2)
                + Math.pow(c.blue - o.toColor().blue, 2)) / 2);
    }

    public Color toColor() {
        return this.c;

    }

    public Color8Bit toColor8Bit() {
        return new Color8Bit(this.c);

    }
}