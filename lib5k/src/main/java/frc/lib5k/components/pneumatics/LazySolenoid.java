package frc.lib5k.components.pneumatics;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Solenoid;
import frc.lib5k.interfaces.Loggable;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.telemetry.ComponentTelemetry;

/**
 * Buffer solenoid commands to reduce CAN spam. For some reason, solenoid
 * commands take up a lot of compute
 */
public class LazySolenoid extends Solenoid implements Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Locals */
    private boolean lastState = false;

    /* Telemetry */
    private String name;
    private NetworkTable telemetryTable;

    public LazySolenoid(int moduleNumber, int channel) {
        super(moduleNumber, channel);

        // Determine component name
        name = String.format("LazySolenoid (%d:%d)", moduleNumber, channel);

        // Get telemetry table
        telemetryTable = ComponentTelemetry.getInstance().getTableForComponent(name);
    }

    /**
     * Set the value of a solenoid, but reduce CAN spam by only sending new data
     * 
     * @param on Should the solenoid turn on?
     */
    @Override
    public void set(boolean on) {

        // Check if there is a new command
        if (on != lastState) {

            // Set solenoid mode
            super.set(on);

            // Set last state
            lastState = on;
        }
    }

    /**
     * re-send the current state to flush CAN
     */
    public void flush() {
        super.set(lastState);
    }

    @Override
    public void logStatus() {

        // Build status string
        String status = String.format("Value: %b", lastState);

        // Log status
        logger.log(name, status);

    }

    @Override
    public void updateTelemetry() {
        telemetryTable.getEntry("Enabled").setBoolean(lastState);

    }

}