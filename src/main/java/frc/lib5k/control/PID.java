package frc.lib5k.control;

import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import frc.lib5k.kinematics.PIDProfile;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;

/**
 * Our custom PID controller implementation.
 * 
 * Based off of faceincake's PID code from 2018 and 2019
 */
public class PID extends SendableBase {
    RobotLogger logger = RobotLogger.getInstance();

    private double kP, kI, kD;
    private double integral = 0.0;
    private double previous_error = 0.0;
    private double setpoint;
    private boolean has_constraints = false;
    private double min_output, max_output;
    private int pause_duration = 0;

    /**
     * Create a PID controller using a PIDProfile
     * 
     * @param profile PIDProfile
     */
    public PID(PIDProfile profile) {
        this(profile.kp, profile.ki, profile.kd);
    }

    /**
     * PID Constructor
     * 
     * @param kp P gain
     * @param ki I gain
     * @param kd D gain
     */
    public PID(double kp, double ki, double kd) {
        this.kP = kp;
        this.kI = ki;
        this.kD = kd;

        logger.log("PID gains have been set to: " + kp + ", " + ki + ", " + kd, Level.kLibrary);

        this.setpoint = 0.0;

        // Handle sendable naming
        String name = getClass().getName();
        setName(name.substring(name.lastIndexOf('.') + 1));

    }

    /**
     * Change the target value for the PID controller to reach.
     * 
     * This could be an angle for turning, or an encoder value for moving an
     * elevator.
     * 
     * @param setpoint The optimal result (ex. the angle to turn to)
     */
    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;
        logger.log("PID setpoint has been set to: " + setpoint, Level.kLibrary);
    }

    /**
     * Set PID gains with a PIDProfile
     * 
     * @param profile New PID gains
     */
    public void setGains(PIDProfile profile) {
        setGains(profile.kp, profile.ki, profile.kd);
    }

    /**
     * Change the P, I and D gains on the fly.
     * 
     * This is for applications where shifting gains are required. If you do not
     * need to change the gains more then once, don't use this.
     * 
     * @param kp P gain
     * @param ki I gain
     * @param kd D gain
     */
    public void setGains(double kp, double ki, double kd) {
        this.kP = kp;
        this.kI = ki;
        this.kD = kd;

        logger.log("PID gains have been reset to: " + kp + ", " + ki + ", " + kd, Level.kLibrary);
    }

    /**
     * Set constraints on PID output. If set, the feed() method will never return a
     * value outside of the range.
     * 
     * @param min Minimum output
     * @param max Maximum output
     */
    public void setOutputConstraints(double min, double max) {
        // Tell the system about the new constraints
        has_constraints = true;
        min_output = min;
        max_output = max;
    }

    /**
     * Feed the controller with a sensor reading
     * 
     * @param sensor_reading The current reading of the input sensor (ex. gyro
     *                       angle)
     * 
     * @return The output of the controller. This generally should be fed directly
     *         into a motor
     */
    public double feed(double sensor_reading) {

        return feedError(this.setpoint - sensor_reading); // Error = Target - Actual

    }

    /**
     * Feed the controller with a sensor error
     * 
     * @param sensor_reading The current control loop error
     * 
     * @return The output of the controller. This generally should be fed directly
     *         into a motor
     */
    public double feedError(double error) {
        /* Do the math */

        this.integral += (error * 0.02);
        double derivative = (error - this.previous_error) / 0.02;

        this.previous_error = error;

        double output = (this.kP * error) + (this.kI * this.integral) + (this.kD * derivative);

        // Bind output to constraints
        if (has_constraints) {
            // Set min value
            output = Math.max(min_output, output);

            // Set max value
            output = Math.min(max_output, output);
        }

        return output;
    }

    public double getError() {
        return previous_error;
    }

    /**
     * Checks if the error is within a reasonable range, then waits a bit before returning completion
     * 
     * @param epsilon Accepted error
     * @return Has the loop finished?
     */
    public boolean isFinished(double epsilon) {
		double error = Math.abs(previous_error);

		// close enough to target
		if (error <= epsilon) {
			pause_duration++;
		}
		// not close enough to target
		else {
			pause_duration = 0;
		}

		return pause_duration > 5; // Make sure this value has settled
	}

    public void reset() {
        has_constraints = false;
        integral = 0.0;
        previous_error = 0.0;
        pause_duration = 0;
    }

    /**
     * This is for use by WPIlib. Basically tricking it into thinking this is a
     * built-in class.
     */
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("PIDController");
        builder.setSafeState(this::reset);
        builder.addDoubleProperty("p", () -> kP, (double p) -> kP = p);
        builder.addDoubleProperty("i", () -> kI, (double i) -> kI = i);
        builder.addDoubleProperty("d", () -> kD, (double d) -> kD = d);
        builder.addDoubleProperty("setpoint", () -> setpoint, (double setpoint) -> this.setpoint = setpoint);
    }

}