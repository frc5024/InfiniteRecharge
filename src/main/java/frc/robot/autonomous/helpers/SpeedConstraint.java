package frc.robot.autonomous.helpers;

public class SpeedConstraint {
    public double maxSpeedPercent;
    public double maxAccelPercent;

    public SpeedConstraint(double maxSpeedPercent, double maxAccelPercent) {
        this.maxAccelPercent = maxAccelPercent;
        this.maxSpeedPercent = maxSpeedPercent;

    }
}