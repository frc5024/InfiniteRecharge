package frc.lib5k.components.motors;

import java.util.function.Consumer;

import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.SpeedControllerGroup;

import frc.lib5k.components.motors.interfaces.IMotorCollection;
import frc.lib5k.components.motors.interfaces.IMotorGroupSafety;
import frc.lib5k.components.motors.interfaces.IRampRateController;
import frc.lib5k.components.motors.interfaces.IVoltageOutputController;
import frc.lib5k.components.motors.motorsensors.VictorEncoder;
import frc.lib5k.components.sensors.EncoderBase;
import frc.lib5k.components.sensors.IEncoderProvider;
import frc.lib5k.interfaces.Loggable;
import frc.lib5k.utils.Mathutils;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.lib5k.utils.telemetry.ComponentTelemetry;

public class VictorSPXCollection extends SpeedControllerGroup implements IMotorCollection, IEncoderProvider,
        IMotorGroupSafety, IVoltageOutputController, IRampRateController, Loggable {
    RobotLogger logger = RobotLogger.getInstance();

    /* Victor SPX Objects */
    private WPI_VictorSPX master;
    private WPI_VictorSPX[] slaves;

    /* Locals */
    private String name;
    private double output, currentThresh, currentHold, rampRate;
    private boolean inverted, voltageCompEnabled, currentLimited;

    /* Telemetry */
    private NetworkTable telemetryTable;

    public VictorSPXCollection(WPI_VictorSPX master, WPI_VictorSPX... slaves) {
        super(master, slaves);

        // Set victors
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
        name = String.format("VictorSPXCollection (Master ID %d)", master.getDeviceID());

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

        // Determine VictorSPX input bus voltage
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
    public void setRampRate(double secondsToFull) {
        rampRate = secondsToFull;

    }

    @Override
    public double getRampRate() {
        return rampRate;
    }

    @Override
    public void enableRampRateLimiting(boolean enabled) {

        // Handle VictorSPX configuration
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
     * Get the master VictorSPX
     * 
     * @return Master
     */
    public WPI_VictorSPX getMaster() {
        return master;

    }

    /**
     * For-Each over each slave controller
     * 
     * @param consumer Method to run
     */
    public void forEachSlave(Consumer<WPI_VictorSPX> consumer) {
        for (WPI_VictorSPX victor : slaves) {
            consumer.accept(victor);
        }
    }

    @Override
    public EncoderBase getDefaultEncoder() {
        return new VictorEncoder(master);
    }

    @Override
    public EncoderBase getEncoder(int id) {
        // Clamp the ID to the number of slaved + 1 (the master)
        id = (int) Mathutils.clamp(id, 0, slaves.length);

        // Check if the ID is for the master
        if (id == 0) {
            return new VictorEncoder(master);
        }

        // Otherwise, get the encoder from the list of slaves
        return new VictorEncoder(slaves[id]);
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