package frc.lib5k.components.limelight;

/**
 * A class for storing data about a Limelight vision target
 */
public class LimelightTarget {
    double tx, ty;
    double ta, ts;
    double tshort, tlong;
    String camtran;

    /**
     * Create a LimelightTarget
     * 
     * @param tx      Target X
     * @param ty      Target Y
     * @param ta      Target Area
     * @param ts      Target skew
     * @param tshort  Shortest side
     * @param tlong   Longest side
     * @param camtran 3D Positioning data
     */
    public LimelightTarget(double tx, double ty, double ta, double ts, double tshort, double tlong, String camtran) {
        this.tx = tx;
        this.ty = ty;
        this.ta = ta;
        this.ts = ts;
        this.tshort = tshort;
        this.tlong = tlong;
        this.camtran = camtran;
    }

    /**
     * Horizontal Offset From Crosshair To Target
     * 
     * @return -27 degrees to 27 degrees
     */
    public double getX() {
        return tx;
    }

    /**
     * Vertical Offset From Crosshair To Target
     * 
     * @return -20.5 degrees to 20.5 degrees
     */
    public double getY() {
        return ty;
    }

    /**
     * Target Area
     * 
     * @return 0% of image to 100% of image
     */
    public double getArea() {
        return ta;
    }

    /**
     * Skew or rotation
     * 
     * @return -90 degrees to 0 degrees
     */
    public double getSkew() {
        return ts;
    }

    /**
     * Sidelength of shortest side of the fitted bounding box
     * 
     * @return pixels
     */
    public double getShortestSide() {
        return tshort;
    }

    /**
     * Sidelength of longest side of the fitted bounding box
     * 
     * @return pixels
     */
    public double getLongestSide() {
        return tlong;
    }

    /**
     * Results of a 3D position solution
     * 
     * @return 6 numbers: Translation (x,y,y) Rotation(pitch,yaw,roll)
     */
    public String getTranslationOrNull() {
        return camtran;
    }
}