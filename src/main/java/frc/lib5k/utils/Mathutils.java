package frc.lib5k.utils;

import java.util.HashMap;

import edu.wpi.first.wpiutil.CircularBuffer;

public class Mathutils {

    public static void main(String[] args) {
        System.out.println(getWrappedError(270, 0.0));
        System.out.println(getWrappedError(0.0, 270.0));
        System.out.println(wpiAngleTo5k(-90));
        System.out.println(wpiAngleTo5k(90));
    }

    /**
     * This allows the angle to respect 'wrapping', where 360 and 0 are the same
     * value
     * 
     * @param angle Gyroscope angle
     * @return Wrapped value
     */
    public static double wrapGyro(double angle) {
        // Wrap the angle by 360degs
        angle %= 360.0;

        // Handle offset
        if (Math.abs(angle) > 180.0) {
            angle = (angle > 0) ? angle - 360 : angle + 360;
        }

        return angle;
    }

    /**
     * Gets the error between two angles and allows crossing the 360/0 degree
     * boundary
     * 
     * @param currentAngle Current angle
     * @param desiredAngle Desired/goal angle
     * @return Difference
     */
    public static double getWrappedError(double currentAngle, double desiredAngle) {
        double phi = Math.abs(currentAngle - desiredAngle) % 360; // This is either the distance or 360 - distance
        double distance = phi > 180 ? 360 - phi : phi;

        // Determine the sign (is the difference positive of negative)
        int sign = (currentAngle - desiredAngle >= 0 && currentAngle - desiredAngle <= 180)
                || (currentAngle - desiredAngle <= -180 && currentAngle - desiredAngle >= -360) ? 1 : -1;

        // Return the final difference
        return distance * sign;

    }

    /**
     * Convert from the [-180-180] angles used by WPILib to the [0-360] angles used
     * by lib5k
     * 
     * @param angle Angle
     * @return Angle
     */
    public static double wpiAngleTo5k(double angle) {
        if (angle < 0) {
            return 360 + angle;
        } else {
            return angle;
        }

    }

    /**
     * Returns value clamped between low and high boundaries.
     *
     * @param value Value to clamp.
     * @param low   The lower boundary to which to clamp value.
     * @param high  The higher boundary to which to clamp value.
     */
    public static double clamp(double value, double low, double high) {
        return Math.max(low, Math.min(value, high));
    }

    /**
     * Checks if two values are roughly equal to each other.
     *
     * @param a
     * @param b
     * @param epsilon
     * @return true if a and b are within epsilon
     */
    public static boolean epsilonEquals(double a, double b, double epsilon) {
        return (a - epsilon <= b) && (a + epsilon >= b);
    }

    public static double map(double value, double input_low, double input_high, double output_low, double output_high) {
        return (value - input_low) * (output_high - output_low) / (input_high - input_low) + output_low;
    }

    public static int mode(int[] array) {
        HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
        int max = 1;
        int temp = 0;

        for (int i = 0; i < array.length; i++) {

            if (hm.get(array[i]) != null) {

                int count = hm.get(array[i]);
                count++;
                hm.put(array[i], count);

                if (count > max) {
                    max = count;
                    temp = array[i];
                }
            }

            else
                hm.put(array[i], 1);
        }
        return temp;
    }

    public static int mode(CircularBuffer array, int size) {
        HashMap<Integer, Integer> hm = new HashMap<Integer, Integer>();
        int max = 1;
        int temp = 0;

        for (int i = 0; i < size; i++) {

            if (hm.get((int) array.get(i)) != null) {

                int count = hm.get((int) array.get(i));
                count++;
                hm.put((int) array.get(i), count);

                if (count > max) {
                    max = count;
                    temp = (int) array.get(i);
                }
            }

            else
                hm.put((int) array.get(i), 1);
        }
        return temp;
    }

}