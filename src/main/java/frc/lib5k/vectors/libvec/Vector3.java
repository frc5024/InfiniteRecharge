/**
 * This file is from Evan Pratten (@ewpratten)'s LibVEC Java library. Used with permission.
 */
package frc.lib5k.vectors.libvec;

import frc.lib5k.utils.Mathutils;

/**
 * Vector3 provides a small java 3D vector implementation.
 */
public class Vector3 {

    public double x, y, z;

    /**
     * Create a vector from components
     * 
     * @param x X component
     * @param y Y component
     * @param z Z component
     */
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;

    }

    /**
     * Copy constructor
     * 
     * @param other Copy source
     */
    public Vector3(Vector3 other) {
        this(other.x, other.y, other.z);
    }

    /**
     * Create a vector from one component
     * 
     * @param x X component
     */
    public Vector3(double x) {
        this(x, x, x);
    }

    /**
     * Create a vector of 0
     */
    public Vector3() {
        this(0.0);
    }

    /**
     * Get the vector length
     * 
     * @return Length
     */
    public double length() {
        return Math.sqrt(norm());
    }

    /**
     * Get the vector norm
     * 
     * @return Norm
     */
    public double norm() {
        return ((x * x) + (y * y) + (z * z));
    }

    /**
     * Get the vector normal
     * 
     * @return Normalized vector
     */
    public Vector3 normalize() {

        // Calculate the inverse length
        double inverseLength = (1.0 / length());

        // Build a new vector
        return new Vector3(x * inverseLength, y * inverseLength, z * inverseLength);
    }

    /**
     * Clamp all components
     * 
     * @param min Lowest value
     * @param max Highest value
     */
    public void clamp(double min, double max) {

        x = Mathutils.clamp(x, min, max);
        y = Mathutils.clamp(y, min, max);
        z = Mathutils.clamp(z, min, max);
    }

    /**
     * Check equality with another vector3
     * 
     * @param other Other vector
     * @return Is equal?
     */
    public boolean equals(Vector3 other) {
        return other.x == x && other.y == y && other.z == z;
    }

    /* Operators */

    /**
     * Get the dot product of the other Vector3
     * 
     * @param other Other Vector3
     * @return Dot product
     */
    public double dot(Vector3 other) {
        return (this.x * other.x) + (this.y * other.y) + (this.z * other.z);
    }

    /**
     * Get the cross product of the other Vector3
     * 
     * @param other Other Vector3
     * @return Cross product
     */
    public Vector3 cross(Vector3 other) {
        return new Vector3(this.y * other.z - this.z * other.y, this.z * other.x - this.x * other.z,
                this.x * other.y - this.y * other.x);
    }

    /**
     * Subtract two Vector3s
     * 
     * @param a First vector
     * @param b Second vector
     * @return Difference
     */
    public static Vector3 sub(Vector3 a, Vector3 b) {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    /**
     * Add two vectors
     * 
     * @param a First vector
     * @param b Second vector
     * @return Sum
     */
    public static Vector3 add(Vector3 a, Vector3 b) {
        return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    /**
     * Get the inverse of a vector
     * 
     * @param x Vector
     * @return Inverse
     */
    public static Vector3 negate(Vector3 x) {
        return new Vector3(-x.x, -x.y, -x.z);
    }

    /**
     * Multiply two vectors
     * 
     * @param a First vector
     * @param b Second vector
     * @return Product
     */
    public static Vector3 mul(Vector3 a, Vector3 b) {
        return new Vector3(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    /**
     * Multiply a vector and a scalar
     * 
     * @param a Vector
     * @param b Scalar
     * @return Product
     */
    public static Vector3 mul(Vector3 a, double b) {
        return new Vector3(a.x * b, a.y * b, a.z * b);
    }

}