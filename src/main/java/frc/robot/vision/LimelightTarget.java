package frc.robot.vision;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

public class LimelightTarget {

    public double tx, ty, ta, ts;

    /**
     * Create a LimelightTarget
     * 
     * @param tx Target X angle
     * @param ty Target Y angle
     * @param ta Target area
     * @param ts Target skew
     */
    public LimelightTarget(double tx, double ty, double ta, double ts) {
        this.tx = tx;
        this.ty = ty;
        this.ta = ta;
        this.ts = ts;
    }

    /**
     * Set target params
     * 
     * @param tx Target X angle
     * @param ty Target Y angle
     * @param ta Target area
     * @param ts Target skew
     */
    public void set(double tx, double ty, double ta, double ts) {
        this.tx = tx;
        this.ty = ty;
        this.ta = ta;
        this.ts = ts;
    }

    /**
     * Get the target rotation
     * 
     * @return Target rotation
     */
    public Rotation2d getRotation() {
        return Rotation2d.fromDegrees(tx);
    }
}