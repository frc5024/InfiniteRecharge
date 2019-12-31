package frc.lib5k.components.motors;

import java.util.function.Consumer;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import frc.lib5k.components.motors.interfaces.ICurrentController;
import frc.lib5k.components.motors.interfaces.IMotorCollection;
import frc.lib5k.components.motors.interfaces.IMotorGroupSafety;
import frc.lib5k.components.motors.interfaces.IRampRateController;
import frc.lib5k.components.motors.interfaces.IVoltageOutputController;
import frc.lib5k.components.motors.motorsensors.TalonEncoder;
import frc.lib5k.components.sensors.EncoderBase;
import frc.lib5k.components.sensors.IEncoderProvider;
import frc.lib5k.interfaces.Loggable;
import frc.lib5k.utils.Mathutils;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.lib5k.utils.telemetry.ComponentTelemetry;

/**
 * Collection of multiple WPI_TalonSRX controllers that wraps a
 * SpeedControllerGroup
 */
public class TalonSRXCollection extends SpeedControllerGroup implements IMotorCollection, ICurrentController,
        IEncoderProvider, IMotorGroupSafety, IVoltageOutputController, IRampRateController, Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Talon SRX Objects */
    private WPI_TalonSRX master;
    private WPI_TalonSRX[] slaves;

    /* Locals */
    private String name;
    private double output, currentThresh, currentHold, rampRate;
    private boolean inverted, voltageCompEnabled, currentLimited;

    /* Telemetry */
    private NetworkTable telemetryTable;

    public TalonSRXCollection(WPI_TalonSRX master, WPI_TalonSRX... slaves) {
        super(master, slaves);

        // Set talons
        this.master = master;
        this.slaves = slaves;

        // Defult the master
        master.configFactoryDefault();

        // Slave each slave
        forEachSlave((slave) -> {
            slave.configFactoryDefault();
            slave.follow(master);

        });

        // Determine name
        name = String.format("TalonSRXCollection (Master ID %d)", master.getDeviceID());

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

        // Determine TalonSRX input bus voltage
        double busVoltage = master.getBusVoltage();

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
        return master.getMotorOutputVoltage();

    }

    @Override
    public void setCurrentLimit(int threshold, int duration, int hold, int timeout) {
        // Set telem data
        currentHold = hold;
        currentThresh = threshold;

        // Set master limits
        TalonHelper.configCurrentLimit(master, threshold, hold, duration, timeout);

        // Set each slave
        forEachSlave((slave) -> {
            TalonHelper.configCurrentLimit(slave, threshold, hold, duration, timeout);
        });

        // Enable limits
        enableCurrentLimit(true);
    }

    /**
     * Set if current limiting should be enabled
     * 
     * @param on Should enable?
     */
    @Override
    public void enableCurrentLimit(boolean on) {
        currentLimited = on;

        // Set master mode
        master.enableCurrentLimit(on);

        // Set each slave
        forEachSlave((slave) -> {
            slave.enableCurrentLimit(on);
        });

    }

    /**
     * Set if voltage compensation should be enabled
     * 
     * @param on Should enable?
     */
    @Override
    public void setCompensation(boolean on) {
        voltageCompEnabled = on;

        // Set master mode
        master.enableVoltageCompensation(on);

        // Set each slave
        forEachSlave((slave) -> {
            slave.enableVoltageCompensation(on);
        });

    }

    @Override
    public void setRampRate(double secondsToFull) {
        rampRate = secondsToFull;

    }

    @Override
    public double getRampRate() {
        return rampRate;
    }

    @Override
    public void enableRampRateLimiting(boolean enabled) {

        // Handle TalonSRX configuration
        if (enabled) {
            master.configOpenloopRamp(rampRate);
        } else {
            master.configOpenloopRamp(0.0);
        }

    }

    @Override
    public void setMasterMotorSafety(boolean enabled) {
        master.setSafetyEnabled(enabled);

    }

    /**
     * Get the master TalonSRX
     * 
     * @return Master
     */
    public WPI_TalonSRX getMaster() {
        return master;

    }

    /**
     * For-Each over each slave controller
     * 
     * @param consumer Method to run
     */
    public void forEachSlave(Consumer<WPI_TalonSRX> consumer) {
        for (WPI_TalonSRX talon : slaves) {
            consumer.accept(talon);
        }
    }

    @Override
    public EncoderBase getDefaultEncoder() {
        return new TalonEncoder(master);
    }

    @Override
    public EncoderBase getEncoder(int id) {
        // Clamp the ID to the number of slaved + 1 (the master)
        id = (int) Mathutils.clamp(id, 0, slaves.length);

        // Check if the ID is for the master
        if (id == 0) {
            return new TalonEncoder(master);
        }

        // Otherwise, get the encoder from the list of slaves
        return new TalonEncoder(slaves[id]);
    }

    public void setNeutralMode(NeutralMode mode) {

        // Set master mode
        master.setNeutralMode(mode);

        // Set slaves modes
        forEachSlave((talon) -> {
            talon.setNeutralMode(mode);
        });

    }

    /**
     * Log all component data with RobotLogger
     */
    @Override
    public void logStatus() {

        // Build info string
        String data = String.format(
                "Output: %.2f, Inverted %b, Current limited: %b, Voltage Compensation: %b, Current threshold: %.2f, Current hold: %.2f, Ramp rate: %.2f",
                output, inverted, currentLimited, voltageCompEnabled, currentThresh, currentHold, rampRate);

        // Log string
        logger.log(name, data, Level.kInfo);

    }

    @Override
    public void updateTelemetry() {
        telemetryTable.getEntry("Output").setNumber(output);
        telemetryTable.getEntry("Is Inverted").setBoolean(inverted);
        telemetryTable.getEntry("Is Current Limited").setBoolean(currentLimited);
        telemetryTable.getEntry("Voltage Compensation").setBoolean(voltageCompEnabled);
        telemetryTable.getEntry("Curent Threshold").setNumber(currentThresh);
        telemetryTable.getEntry("Current Hold").setNumber(currentHold);
        telemetryTable.getEntry("Ramp Rate").setNumber(rampRate);

    }

}