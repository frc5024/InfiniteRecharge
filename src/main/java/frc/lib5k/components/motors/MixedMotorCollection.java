package frc.lib5k.components.motors;

import java.util.function.Consumer;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import frc.lib5k.components.motors.interfaces.IMotorCollection;
import frc.lib5k.components.motors.interfaces.IRampRateController;
import frc.lib5k.components.motors.interfaces.IVoltageOutputController;
import frc.lib5k.control.TimedSlewLimiter;
import frc.lib5k.interfaces.Loggable;
import frc.lib5k.utils.ObjectCounter;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.lib5k.utils.telemetry.ComponentTelemetry;

/**
 * Collection of multiple motor controllers of mixed types that wraps a
 * SpeedControllerGroup
 */
public class MixedMotorCollection extends SpeedControllerGroup
        implements IMotorCollection, IVoltageOutputController, IRampRateController, Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Motor controllers */
    private SpeedController master;
    private SpeedController[] slaves;

    /* Telemetry */
    private double output;
    private boolean inverted;
    private String name;
    private NetworkTable telemetryTable;

    /* ID tracking */
    private static ObjectCounter idCounter = new ObjectCounter();

    /* Locals */
    private TimedSlewLimiter slewLimiter;

    public MixedMotorCollection(SpeedController master, SpeedController... slaves) {
        super(master, slaves);

        // Set locals
        this.master = master;
        this.slaves = slaves;

        // Configure a slew limiter with no slew
        slewLimiter = new TimedSlewLimiter(0.0);
        slewLimiter.setEnabled(false);

        // Determine name
        name = String.format("MixedMotorCollection (Master ID %d)", idCounter.getNewID());

        // Get the telemetry NetworkTable
        telemetryTable = ComponentTelemetry.getInstance().getTableForComponent(name);

    }

    @Override
    public void set(double speed) {
        output = speed;

        super.set(speed);
    }

    @Override
    public void setBuffer(double speed) {
        if (speed != output) {
            set(speed);
        }

    }

    @Override
    public void setInverted(boolean isInverted) {
        inverted = isInverted;
        super.setInverted(isInverted);

    }

    @Override
    public void pidWrite(double output) {
        this.output = output;
        super.pidWrite(output);

    }

    @Override
    public void setVoltage(double volts) {

        // Determine Robot bus voltage
        double busVoltage = RobotController.getBatteryVoltage();

        // Just stop the motor if the bus is at 0V
        // Many things would go wrong otherwise (do you really want a div-by-zero error
        // on your drivetrain?)
        if (busVoltage == 0.0) {
            set(0.0);
            return;
        }

        // Convert voltage to a motor speed
        double calculated_speed = volts / busVoltage;

        // Set the output to the calculated speed
        set(calculated_speed);

    }

    @Override
    public double getEstimatedVoltage() {

        // Determine Robot bus voltage
        double busVoltage = RobotController.getBatteryVoltage();

        // Convert percent output to voltage
        double voltage_estimate = get() * busVoltage;

        return voltage_estimate;
    }

    @Override
    public void setRampRate(double secondsToFull) {
        slewLimiter.setRate(secondsToFull);

    }

    @Override
    public double getRampRate() {
        return slewLimiter.getRate();
    }

    @Override
    public void enableRampRateLimiting(boolean enabled) {
        slewLimiter.setEnabled(enabled);

    }

    /**
     * For-Each over each slave controller
     * 
     * @param consumer Method to run
     */
    public void forEachSlave(Consumer<SpeedController> consumer) {
        for (SpeedController slave : slaves) {
            consumer.accept(slave);
        }
    }

    @Override
    public void logStatus() {
        // Build info string
        String data = String.format("Output: %.2f, Inverted %b", output, inverted);

        // Log string
        logger.log(name, data, Level.kInfo);

    }

    @Override
    public void updateTelemetry() {
        telemetryTable.getEntry("Output").setNumber(output);
        telemetryTable.getEntry("Is Inverted").setBoolean(inverted);

    }

}