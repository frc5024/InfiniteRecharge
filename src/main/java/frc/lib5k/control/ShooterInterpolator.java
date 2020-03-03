package frc.lib5k.control;

import edu.wpi.first.wpiutil.math.MathUtil;
import frc.lib5k.utils.Mathutils;
import frc.lib5k.vectors.libvec.Point2;
import frc.lib5k.vectors.libvec.Vector3;

/**
 * A
 */
public class ShooterInterpolator {

    /* Quadratic Coefficients */
    private double A, B, C;

    /**
     * Create a linear ShooterInterpolator
     * 
     * @param min Min point on an XY plane
     * @param max Max point on an XY plane
     */
    public ShooterInterpolator(Point2 min, Point2 max) {

        // Solve midpoint for linear interpolator
        this(min, new Point2(Vector3.mul(Vector3.sub(max, min), 0.5)), max);

    }

    /**
     * Create a quadratic ShooterInterpolator
     * 
     * @param min Min point on an XY plane
     * @param mid An interior point along a parabola connecting the min an max on an
     *            XY plane
     * @param max Max point on an XY plane
     */
    public ShooterInterpolator(Point2 min, Point2 mid, Point2 max) {

        // Determine coefficients
        double[] coeffs = Mathutils.calcCoeffs(min, mid, max);
        this.A = coeffs[0];
        this.B = coeffs[1];
        this.C = coeffs[2];

    }

    /**
     * Determine the optimal output for the controller given an X coordinate along
     * the interpolator's XY plane, clamping at 0 and the max output.
     * 
     * @param x         X coordinate
     * @param maxOutput Maximum system output
     * @return Optimal output
     */
    public double calculate(double x, double maxOutput) {

        // Calculate for X
        double y = (this.A * Math.pow(x, 2)) + (this.B * x) + this.C;

        // Clamp Y
        y = MathUtil.clamp(y, 0, maxOutput);

        return y;
    }

}