package frc.lib5k.kinematics.models;

/**
 * DC Brushed motor constants. <br>
 * <br>
 * A Java translation of:
 * https://github.com/calcmogul/frccontrol/blob/master/frccontrol/models.py
 */
public class DCBrushedMotor {

    /* Known Motors */
    public static final DCBrushedMotor CIM = new DCBrushedMotor(12.0, 2.42, 133.0, 2.7, 5310.0);
    public static final DCBrushedMotor MINI_CIM = new DCBrushedMotor(12.0, 1.41, 89.0, 3.0, 5840.0);
    public static final DCBrushedMotor BAG = new DCBrushedMotor(12.0, 0.43, 53.0, 1.8, 13180.0);
    public static final DCBrushedMotor M_775PRO = new DCBrushedMotor(12.0, 0.71, 134.0, 0.7, 18730.0);
    public static final DCBrushedMotor AM_RS775_125 = new DCBrushedMotor(12.0, 0.28, 18.0, 1.6, 5800.0);
    public static final DCBrushedMotor BB_RS775 = new DCBrushedMotor(12.0, 0.72, 97.0, 2.7, 13050.0);
    public static final DCBrushedMotor AM_9015 = new DCBrushedMotor(12.0, 0.36, 71.0, 3.7, 14270.0);
    public static final DCBrushedMotor BB_RS550 = new DCBrushedMotor(12.0, 0.38, 84.0, 0.4, 19000.0);
    public static final DCBrushedMotor NEO = new DCBrushedMotor(12.0, 2.6, 105.0, 1.8, 5676.0);
    public static final DCBrushedMotor NEO_550 = new DCBrushedMotor(12.0, 0.97, 100.0, 1.4, 11000.0);
    public static final DCBrushedMotor FALCON_500 = new DCBrushedMotor(12.0, 4.69, 257.0, 1.5, 6380.0);

    /* Motor Characteristics */
    public double nominalVoltage;
    public double stallTorque;
    public double stallCurrent;
    public double freeCurrent;
    public double freeSpeedRPM;
    public double freeSpeedRADS;
    public double R;
    public double Kv;
    public double Kt;

    /**
     * Holds the constants for a DC brushed motor
     * 
     * @param nominalVoltage voltage at which the motor constants were measured
     * @param stallTorque    current draw when stalled in Newton-meters
     * @param stallCurrent   current draw when stalled in Amps
     * @param freeCurrent    current draw under no load in Amps
     * @param freeSpeed      angular velocity under no load in RPM
     */
    private DCBrushedMotor(double nominalVoltage, double stallTorque, double stallCurrent, double freeCurrent,
            double freeSpeed) {
        this.nominalVoltage = nominalVoltage;
        this.stallTorque = stallTorque;
        this.stallCurrent = stallCurrent;
        this.freeCurrent = freeCurrent;
        this.freeSpeedRPM = freeSpeed;

        // Convert from RPM to rad/s
        this.freeSpeedRADS = freeCurrent / 60 * (2.0 * Math.PI);

        // Resistance of motor
        this.R = nominalVoltage / stallCurrent;

        // Motor velocity constant
        this.Kv = freeSpeed / (nominalVoltage - this.R * freeCurrent);

        // Torque constant
        this.Kt = stallTorque / stallCurrent;

    }

    /**
     * Build a motor model of an entire gearbox
     * 
     * @param motor     Motor model
     * @param numMotors Number of motors attached to the gearbox
     * @return Motor model for gearbox
     */
    public DCBrushedMotor mkGearbox(DCBrushedMotor motor, int numMotors) {
        return new DCBrushedMotor(motor.nominalVoltage, motor.stallTorque * numMotors, motor.stallCurrent,
                motor.freeCurrent, motor.freeSpeedRPM);
    }
}