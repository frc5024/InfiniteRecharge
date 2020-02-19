package frc.lib5k.utils.telemetry;

import java.util.function.DoubleSupplier;

import edu.wpi.first.networktables.NetworkTable;

/**
 * A utility class for providing data to a robot telemetry client
 */
public class FlywheelTuner {

    // Loop trackers
    private boolean m_enabled, m_doLogs = false;

    // RPM supplier
    private DoubleSupplier m_rpmSource;

    // Component telemetry table
    private NetworkTable m_telemTable;

    // Tracker for system setpoints
    private double setpoint = 0.0;

    /**
     * Create a FlywheelTuner server
     * 
     * @param name      Tuner name
     * @param rpmSource RPM supplier
     */
    public FlywheelTuner(String name, DoubleSupplier rpmSource) {
        this.m_rpmSource = rpmSource;

        // Connect to component telemetry
        m_telemTable = ComponentTelemetry.getInstance().getTableForComponent(String.format("FlywheelTuner-%s", name));

    }

    /**
     * Set the system enable
     * 
     * @param enabled Should the FlywheelTuner be able to log?
     */
    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
        m_telemTable.getEntry("enabled").setBoolean(enabled);

    }

    /**
     * Enable a logging session
     * 
     * @param enabled Enable logging session
     */
    public void enableLogging(boolean enabled) {
        m_doLogs = enabled;

    }

    /**
     * Set the controller setpoint var
     * 
     * @param setpoint Setpoint
     */
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    /**
     * Update the output
     */
    public void update() {
        // Make sure we are enabled
        if (m_enabled) {

            // Publish the logger state
            m_telemTable.getEntry("running").setBoolean(m_doLogs);

            // If we should be logging, publish the rpm data
            if (m_doLogs) {
                m_telemTable.getEntry("rpm").setDouble(m_rpmSource.getAsDouble());
                m_telemTable.getEntry("setpoint").setDouble(setpoint);
            }
        }

    }
}