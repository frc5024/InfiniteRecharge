package frc.lib5k.components.limelight;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * Limelight interface tool
 */
public class Limelight {
    NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

    /**
     * Camera mode
     */
    public enum CameraMode {
        VISION(0), DRIVER(1);

        int val;

        CameraMode(int val) {
            this.val = val;
        }

        int getVal() {
            return val;
        }

    }

    /**
     * LED mode
     */
    public enum LEDMode {
        DEFAULT(0), ON(3), OFF(1), BLINK(2);

        int val;

        LEDMode(int val) {
            this.val = val;
        }

        int getVal() {
            return val;
        }
    }

    /* NT entries */
    NetworkTableEntry tv = table.getEntry("tv");
    NetworkTableEntry tx = table.getEntry("tx");
    NetworkTableEntry ty = table.getEntry("ty");
    NetworkTableEntry ta = table.getEntry("ta");
    NetworkTableEntry tl = table.getEntry("tl");
    NetworkTableEntry ts = table.getEntry("ts");
    NetworkTableEntry tshort = table.getEntry("tshort");
    NetworkTableEntry tlong = table.getEntry("tlong");
    NetworkTableEntry camtran = table.getEntry("camtran");
    NetworkTableEntry SLED = table.getEntry("ledMode");
    NetworkTableEntry cameraMode = table.getEntry("camMode");
    NetworkTableEntry setPipeId = table.getEntry("pipeline");
    NetworkTableEntry setStream = table.getEntry("stream");
    NetworkTableEntry enableSnapshot = table.getEntry("snapshot");
    NetworkTableEntry getPipe = table.getEntry("getpipe");

    // Orientation data
    boolean portrait = false;

    public Limelight() {
        this(false);
    }

    public Limelight(boolean is_portrait) {
        this.portrait = is_portrait;
    }

    /**
     * Whether the limelight has any valid targets
     * 
     * @return true or false
     */
    public boolean isTargetVisible() {
        return tv.getNumber(0).intValue() == 1;
    }

    /**
     * True active pipeline index of the camera
     * 
     * @return 0-9
     */
    public double getPipelineID() {
        return (double) getPipe.getNumber(0);
    }

    /**
     * returns a snap shot of limelight target data
     * 
     * @return limelightTarget
     */
    public LimelightTarget getTarget() {

        // Return a new target, and handle portrait mode
        return new LimelightTarget((portrait) ? ty.getDouble(0.0) : tx.getDouble(0.0),
                (portrait) ? tx.getDouble(0.0) : ty.getDouble(0.0), ta.getDouble(0.0), ts.getDouble(0.0),
                tshort.getDouble(0.0), tlong.getDouble(0.0), camtran.getString(null));
    }

    /**
     * The pipeline’s latency contribution
     * 
     * @return ms
     */
    public double getLatency() {
        return tl.getDouble(0.0);
    }

    /**
     * Sets limelight’s LED state
     */
    public void setLEDMode(LEDMode mode) {
        SLED.setNumber(mode.getVal());
    }

    /**
     * Sets limelight’s operation mode
     */
    public void setCameraMode(CameraMode mode) {
        cameraMode.setNumber(mode.getVal());
    }

    /**
     * Sets limelight’s current pipeline
     */
    public void setPipelineID(int id) {
        setPipeId.setNumber(id);
    }

    /**
     * Sets limelight’s streaming mode
     */
    public void setStreamMode(int mode) {
        setStream.setNumber(mode);
    }

    /**
     * Allows users to take snapshots during a match
     */
    public void enableSnapshots(boolean enabled) {
        enableSnapshot.setBoolean(enabled);
    }
}