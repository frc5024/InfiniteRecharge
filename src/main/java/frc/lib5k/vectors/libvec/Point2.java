/**
 * This file is from Evan Pratten (@ewpratten)'s LibVEC Java library. Used with permission.
 */
package frc.lib5k.vectors.libvec;

/**
 * A 2D floating point. Replacement for javax.vecmath.Point2f
 */
public class Point2 extends Vector3 {

    /**
     * Create a 2D point represented as a 3D vector
     * 
     * @param x X component
     * @param y Y component
     */
    public Point2(double x, double y) {
        super(x, y, 0);
    }

    public Point2(Vector3 v){
        super(v);
    }
}