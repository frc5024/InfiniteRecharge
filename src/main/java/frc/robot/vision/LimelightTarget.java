package frc.robot.vision;

public class LimelightTarget {

    public double tx, ty, ta, ts;

    public LimelightTarget(double tx, double ty, double ta, double ts) {
        this.tx = tx;
        this.ty = ty;
        this.ta = ta;
        this.ts = ts;
    }

    public void set(double tx, double ty, double ta, double ts) {
        this.tx = tx;
        this.ty = ty;
        this.ta = ta;
        this.ts = ts;
    }
}