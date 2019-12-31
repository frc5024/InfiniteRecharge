package frc.lib5k.simulation.mock.sensors;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.SpeedController;
import frc.lib5k.components.sensors.EncoderBase;
import frc.lib5k.interfaces.Loggable;
import frc.lib5k.roborio.FPGAClock;
import frc.lib5k.utils.ObjectCounter;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.telemetry.ComponentTelemetry;

/**
 * Simulate an encoder.
 */
public class MockEncoder extends EncoderBase implements Loggable {

    /* Telemetry */
    RobotLogger logger = RobotLogger.getInstance();
    NetworkTable telemetryTable;
    private static ObjectCounter idCounter = new ObjectCounter();
    private String name;

    /* Locals */
    private SpeedController controller;
    private int tpr, ticks;
    private double last_time;
    private double gearbox_ratio, max_rpm;

    /**
     * Create a simulated encoder (can be used for unit tests)
     * 
     * @param controller    SpeedController providing output for the encoder to
     *                      model
     * @param tpr           Encoder ticks per output revolution
     * @param gearbox_ratio Gearbox gearing ratio from motor to output
     * @param max_rpm       Motor maximum RPM
     */
    public MockEncoder(SpeedController controller, int tpr, double gearbox_ratio, double max_rpm) {

        // Set locals
        this.controller = controller;
        this.tpr = tpr;
        this.gearbox_ratio = gearbox_ratio;
        this.max_rpm = max_rpm;

        // Determine name for telemetry
        name = String.format("MockEncoder (ID %d)", idCounter.getNewID());

        // Get the telemetry NetworkTable
        telemetryTable = ComponentTelemetry.getInstance().getTableForComponent(name);
    }

    @Override
    public int getRawTicks() {
        return ticks;
    }

    /**
     * Calculate encoder position from motor input
     */
    @Override
    public void update() {
        // If this is the first loop, simply re-set the timer, and skip
        if (last_time == 0) {
            last_time = FPGAClock.getFPGASeconds();
            return;
        }

        // Determine dt
        double current_time = FPGAClock.getFPGASeconds();
        double dt = current_time - last_time;
        last_time = current_time;

        // Calc encoder position
        double rpm = (controller.get() * max_rpm) / gearbox_ratio;
        double revs = (rpm / 60.0) * dt; // RPM -> RPS -> Multiply by seconds to find rotations since last update
        ticks += revs * tpr;

        // Run other update code
        super.update();
    }

    @Override
    public void logStatus() {

        // Build statis string
        String status = String.format("Ticks: %d, TPR: %d, Gear Ratio: %.3f, Max RPM: %.2f", ticks, tpr, gearbox_ratio,
                max_rpm);

        // Log status string
        logger.log(name, status);

    }

    @Override
    public void updateTelemetry() {
        telemetryTable.getEntry("Ticks").setNumber(ticks);
        telemetryTable.getEntry("TPR").setNumber(tpr);
        telemetryTable.getEntry("Gear Ratio").setNumber(gearbox_ratio);
        telemetryTable.getEntry("Max RPM").setNumber(max_rpm);

    }

}