package frc.lib5k.control;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.lib5k.kinematics.PIDProfile;

/**
 * An implementation of team 1114's
 * <a href="https://lynkz.me/HqhJIuG">SimPID</a>, that works with lib5k's
 * kinematics systems, and provides telemetry
 */
public class PIDv2 extends SendableBase {

    private double kp, ki, kd;
    private double setpoint;
    protected double previousError;
    private double errorSum;
    private double maxOutput;
    private double minOutput;
    private int restPeriod;
    private int currentCycleCount;
    private boolean firstCycle;
    protected boolean debug;
    private double lastTime;
    private double deltaTime;
    private double iRange;

    /**
     * Create a PIDv2 object using a PIDProfile
     * 
     * @param profile PIDProfile containing gains
     */
    public PIDv2(PIDProfile profile) {
        this(profile.kp, profile.ki, profile.kd);
    }

    /**
     * Create a PIDv2 object using gains
     * 
     * @param p P gain
     * @param i I gain
     * @param d D gain
     */
    public PIDv2(double p, double i, double d) {
        this.kp = p;
        this.ki = i;
        this.kd = d;
        this.setpoint = 0.0;
        this.firstCycle = true;
        this.maxOutput = 1.0;
        this.currentCycleCount = 0;
        this.restPeriod = 5;
        this.debug = false;
        this.minOutput = 0;
        this.iRange = 1000; // Default high number to always apply I

        // Handle sendable naming
        String name = getClass().getName();
        setName(name.substring(name.lastIndexOf('.') + 1));
    }

    /**
     * Configure the PID controller with a new PIDProfile
     * 
     * @param profile New PIDProfile contaning PID gains
     */
    public void config(PIDProfile profile) {
        this.config(profile.kp, profile.ki, profile.kd);
    }

    /**
     * Configure the PID controller with new gains
     * 
     * @param p P gain
     * @param i I gain
     * @param d D gain
     */
    public void config(double p, double i, double d) {
        this.kp = p;
        this.ki = i;
        this.kd = d;
    }

    /**
     * Set the PID controller's setpoint (goal position)
     * 
     * @param setpoint
     */
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
    }

    /**
     * Set a maximum output value
     * 
     * @param max Maximum output
     */
    public void setOutputConstraints(double max) {
        this.maxOutput = max;
    }

    /**
     * Set both a maximum, and minimum output value
     * 
     * @param min Maximum output
     * @param max Minimum output
     */
    public void setOutputConstraints(double min, double max) {
        this.maxOutput = max;
        this.minOutput = min;
    }

    /**
     * Set the number of cycles the PID controller should wait before declaiming the
     * system "finished"
     * 
     * @param num Number of cycles to wait
     */
    public void setRestPeriod(int num) {
        this.restPeriod = num;
    }

    public void resetErrorSum() {
        this.errorSum = 0.0;
    }

    public double getSetpoint() {
        return this.setpoint;
    }

    public void setIRange(double iRange) {
        this.iRange = iRange;
    }

    public double getIRange() {
        return this.iRange;
    }

    /**
     * Feed the system with the current sensor reading
     * 
     * @param current Current sensor reading
     * @return System output
     */
    public double feed(double current) {
        return calculate(this.setpoint - current);
    }

    /**
     * Calculate a system output with a custom error value
     * 
     * @param error Error value
     * @return System output
     */
    public double calculate(double error) {
        double pVal = 0.0;
        double iVal = 0.0;
        double dVal = 0.0;

        if (this.firstCycle) {
            this.previousError = error;
            this.firstCycle = false;
            this.lastTime = System.currentTimeMillis();
            this.deltaTime = 20.0;
        } else {
            double currentTime = System.currentTimeMillis();
            this.deltaTime = currentTime - lastTime;
            this.lastTime = currentTime;
        }

        this.deltaTime = (this.deltaTime / 20.0); // 20ms is normal and should be 1

        /////// P Calc///////
        pVal = this.kp * error;

        /////// I Calc///////
        if (Math.abs(error) < Math.abs(this.iRange)) { // Within desired range for using I
            this.errorSum += error * this.deltaTime;
        } else {
            this.errorSum = 0.0;
        }
        iVal = this.ki * this.errorSum;

        /////// D Calc///////
        double deriv = (error - this.previousError) / this.deltaTime;
        dVal = this.kd * deriv;

        // Overall PID calc
        double output = pVal + iVal + dVal;

        // limit the output
        // output = Math.min(output, this.maxOutput);
        // output = Math.max(output, -this.maxOutput);

        if (output > 0) {
            if (output < this.minOutput) {
                output = this.minOutput;
            }
        } else {
            if (output > -this.minOutput) {
                output = -this.minOutput;
            }
        }

        output = Math.max(-maxOutput, Math.min(maxOutput, output));

        // store current value as previous for next cycle
        this.previousError = error;

        // System.out.println("LMT: "+output);

        return output;
    }

    /**
     * Check if the system is finished
     * 
     * @param epsilon Acceptable error in the system
     * @return Has the system finished?
     */
    public boolean isFinished(double epsilon) {
        double currError = Math.abs(this.previousError);

        // close enough to target
        if (currError <= epsilon) {
            this.currentCycleCount++;
        }
        // not close enough to target
        else {
            this.currentCycleCount = 0;
        }

        return this.currentCycleCount > this.restPeriod;
    }

    /**
     * Reset the system (effective next cycle)
     */
    public void reset() {
        this.firstCycle = true;
    }

    /**
     * This is for use by WPIlib. Basically tricking it into thinking this is a
     * built-in class.
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("PIDController");
        builder.setSafeState(this::reset);
        builder.addDoubleProperty("p", () -> kp, (double p) -> kp = p);
        builder.addDoubleProperty("i", () -> ki, (double i) -> ki = i);
        builder.addDoubleProperty("d", () -> kd, (double d) -> kd = d);
        builder.addDoubleProperty("setpoint", () -> setpoint, (double setpoint) -> this.setpoint = setpoint);
    }

}