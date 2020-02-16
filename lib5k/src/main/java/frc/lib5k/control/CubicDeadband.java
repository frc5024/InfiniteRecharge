package frc.lib5k.control;

/**
 * This Scaling method was inspired by this blog post:
 * 
 * http://www.mimirgames.com/articles/games/joystick-input-and-using-deadbands/ 
 * 
 * Formula:
 * y = ((w * (x ^ 3)  + (1.0 - w) * x) - (abs(x) / x) * (w * (d ^ 3) + (1.0 - w) * d)) / (1.0 - (w * (d ^ 3) + (1.0 - w) * d))
 * 
 * This can be visualized on desmos:
 *https://www.desmos.com/calculator/awcputalxe
 */

public class CubicDeadband {
    double deadband;
    double percision;

    /**
     * Cubic scaling deadband
     * 
     * @param deadband Deadband size
     * @param percision This number should be set to user preference. The higher it is, the more precice small movements will be and the less precice big movements will be
     */
    public CubicDeadband(double deadband, double percision) {
        this.deadband = deadband;
        this.percision = percision;
    }

    /**
     * Get the weighted cube of a vlaue
     * 
     * @param x The input value
     * @param weight The weight of the function. A higher weight will give more sensitivity at lower speeds and less at higher speeds
     * 
     * @return Output of the scaling function
     */
    private double cubic(double x, double weight) {
        return weight * x * x * x + (1.0 - weight) * x;
    }
    
    /**
     * Pass an input through a cubic scaling function
     * 
     * @param input Raw input
     * 
     * @return Scaled output
     */
    public double feed(double input) {
        if (Math.abs(input) < this.deadband) {
            return 0.0;
        }
        return (cubic(input, this.percision) - (Math.abs(input) / input) * cubic(this.deadband, this.percision))
                / (1.0 - cubic(this.deadband, this.percision));
    }

}