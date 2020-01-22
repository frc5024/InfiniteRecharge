package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

/**
 * Command for handling autonomous turning with PID solve
 */
public class TurnToCommand extends CommandBase {

    // Default epsilon if not set in constructor
    private static final double DEFAULT_EPSILON = 2.0;

    // Number of cycles to wait before declaring this command "done"
    private static final int MIN_CYCLES = 5;

    // Angle setpoint
    private Rotation2d setpoint;

    // Rotation epsilon
    private double epsilon;

    // Counter for controller rest cycles (Thanks 1114 for this idea)
    private int cycles = 0;

    /**
     * Turn to a field-relative angle
     * 
     * @param setpoint Desired angle in degrees (field-relative)
     */
    public TurnToCommand(double angleDegs) {
        this(angleDegs, DEFAULT_EPSILON);
    }

    /**
     * Turn to a field-relative angle
     * 
     * @param setpoint Desired angle in degrees (field-relative)
     * @param epsilon  Allowed error (in degrees)
     */
    public TurnToCommand(double angleDegs, double epsilon) {
        this(Rotation2d.fromDegrees(angleDegs), epsilon);
    }

    /**
     * Turn to a field-relative angle
     * 
     * @param setpoint Desired angle as rotation vector (field-relative)
     */
    public TurnToCommand(Rotation2d setpoint) {
        this(setpoint, DEFAULT_EPSILON);
    }

    /**
     * Turn to a field-relative angle
     * 
     * @param setpoint Desired angle as rotation vector (field-relative)
     * @param epsilon  Allowed error (in degrees)
     */
    public TurnToCommand(Rotation2d setpoint, double epsilon) {

        // Set locals
        this.setpoint = setpoint;
        this.epsilon = epsilon;

    }

    @Override
    public void initialize() {

        // Reset the cycle count
        cycles = 0;
    }

    @Override
    public void execute() {

        // Handle drive and settle
        if (DriveTrain.getInstance().face(setpoint, epsilon)) {
            System.out.println(cycles);
            cycles++;
        }
    }

    @Override
    public void end(boolean interrupted) {

        // Stop the drivetrain
        DriveTrain.getInstance().stop();
    }

    @Override
    public boolean isFinished() {

        // If we reached the setpoint, and are at it for at least n cycles, we have
        // finished
        return cycles > MIN_CYCLES;
    }
}