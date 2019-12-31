package frc.lib5k.kinematics;

import com.ctre.phoenix.motorcontrol.NeutralMode;

public class DriveSignal {

    public enum DriveType {
        STANDARD, VELOCITY
    }

    private double l, r;
    private NeutralMode mode;
    private DriveType type;

    public DriveSignal(double l, double r) {
        this(l, r, DriveType.STANDARD);
    }

    public DriveSignal(double l, double r, DriveType type) {
        this(l, r, NeutralMode.Brake, type);
    }

    public DriveSignal(double l, double r, NeutralMode mode, DriveType type) {
        this.l = l;
        this.r = r;
        this.mode = mode;
        this.type = type;
    }

    public static DriveSignal fromArcadeInputs(double speed, double rotation, DriveType type) {
        return new DriveSignal((rotation + speed), (speed - rotation), type);
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getL() {
        return l;
    }

    public void setL(double l) {
        this.l = l;
    }

    public NeutralMode getMode() {
        return mode;
    }

    public void setMode(NeutralMode mode) {
        this.mode = mode;
    }

    public DriveType getType() {
        return type;
    }

    public void setType(DriveType type) {
        this.type = type;
    }

}