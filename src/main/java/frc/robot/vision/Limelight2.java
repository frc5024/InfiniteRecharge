package frc.robot.vision;

import edu.wpi.first.networktables.EntryListenerFlags;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpiutil.net.PortForwarder;

public class Limelight2 {

    private static Limelight2 s_instance = null;

    public enum CameraMode {
        STANDARD(0), PIP_MAIN(1), PIP_SECONDARY(2);

        public int val;

        CameraMode(int val) {
            this.val = val;
        }

    }

    public enum LEDMode {
        DEFAULT(0), ON(3), OFF(1), BLINK(2);

        public int val;

        LEDMode(int val) {
            this.val = val;
        }
    }

    // Vision NT table
    private NetworkTable m_table;

    /* Camera data */
    private boolean m_hasTarget, m_isPortrait;
    private double m_angleX, m_angleY, m_area, m_skew;

    private Limelight2() {

        // Connect to NT server
        m_table = NetworkTableInstance.getDefault().getTable("limelight");

        // Portforward the device configuration panel
        PortForwarder.add(5801, "10.50.24.11", 5801);

        /* Add NT listeners */
        m_table.addEntryListener("tv", (table, key, entry, value, flags) -> {
            m_hasTarget = value.getBoolean();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        m_table.addEntryListener("tx", (table, key, entry, value, flags) -> {
            m_angleX = value.getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        m_table.addEntryListener("ty", (table, key, entry, value, flags) -> {
            m_angleY = value.getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        m_table.addEntryListener("ta", (table, key, entry, value, flags) -> {
            m_area = value.getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

        m_table.addEntryListener("ts", (table, key, entry, value, flags) -> {
            m_skew = value.getDouble();
        }, EntryListenerFlags.kNew | EntryListenerFlags.kUpdate);

    }

    public static Limelight2 getInstance() {
        if (s_instance == null) {
            s_instance = new Limelight2();
        }

        return s_instance;
    }

    /**
     * Does the limelight have a target in sight?
     * 
     * @return Has target
     */
    public boolean hasTarget() {
        return m_hasTarget;
    }

    /**
     * Get the current target, or null if no target
     * 
     * @return Current target
     */
    public LimelightTarget getTarget() {
        // Handle no target
        if (!hasTarget()) {
            return null;
        }

        return new LimelightTarget(getXAngle(), getYAngle(), getArea(), getSkew());
    }

    /**
     * Get horizontal target angle [from -29.8 t0 29.8 degrees]
     * 
     * @return Target angle
     */
    public double getXAngle() {
        return (m_isPortrait) ? m_angleY : m_angleX;
    }

    /**
     * Get horizontal target angle [from -24.85 to 24.85 degrees]
     * 
     * @return Target angle
     */
    public double getYAngle() {
        return (m_isPortrait) ? m_angleX : m_angleY;
    }

    /**
     * Get target area [0% to 100%]
     * 
     * @return Target area
     */
    public double getArea() {
        return m_area;
    }

    /**
     * Get target skew / rotation [-90 to 0 degrees]
     * 
     * @return Target skew
     */
    public double getSkew() {
        return m_skew;
    }

    /**
     * Set camera portrait mode
     * 
     * @param isPortrait Is camera in portrait mode?
     */
    public void setPortrait(boolean isPortrait) {
        m_isPortrait = isPortrait;
    }

    /**
     * Set the limelight LED mode
     * 
     * @param mode LED mode
     */
    public void setLED(LEDMode mode) {
        m_table.getEntry("ledMode").setNumber(mode.val);

    }

    /**
     * Switch between vision and driver mode
     * 
     * @param on Should be vision tracking?
     */
    public void enableVision(boolean on) {
        m_table.getEntry("camMode").setNumber((on) ? 0 : 1);

    }

    /**
     * Set vision pipeline ID
     * 
     * @param pipelineID Pipeline ID
     */
    public void setPipeline(int pipelineID) {
        m_table.getEntry("pipeline").setNumber(pipelineID);
    }

    /**
     * Set the camera stream mode
     * 
     * @param mode Stream mode
     */
    public void setCamMode(CameraMode mode) {
        m_table.getEntry("stream").setNumber(mode.val);
    }

}