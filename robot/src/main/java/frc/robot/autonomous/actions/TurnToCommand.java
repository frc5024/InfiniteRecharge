package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.utils.Mathutils;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;

/**
 * Command for handling autonomous turning with PID solve
 */
public class TurnToCommand extends CommandBase {

    // Default epsilon if not set in constructor
    private static final double DEFAULT_EPSILON = 2.0;

    // Hard cap percentage on turn speed
    private static final double TURN_SPEED_HARD_CAP = 1.0;

    // Number of cycles to wait before declaring this command "done"
    private static final int MIN_CYCLES = 5;

    // PID controller for angle solves
    private PIDController m_controller;

    // Angle setpoint
    private Rotation2d setpoint;

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

        // Set up the PID controller
        m_controller = new PIDController(RobotConstants.ControlGains.kPTurnVel, RobotConstants.ControlGains.kITurnVel,
                RobotConstants.ControlGains.kDTurnVel);

        // Set controller limits
        m_controller.setTolerance(epsilon);

        // Set the setpoint to 0, so we can calculate by error
        m_controller.setSetpoint(0.0);

    }

    @Override
    public void initialize() {

        // Reset the controller
        m_controller.reset();

        // Reset the cycle count
        cycles = 0;
    }

    private double getError() {

        // Convert the WPILib angles to Lib5K-compatible angles
        double setpointAngle = Mathutils.wpiAngleTo5k(setpoint.getDegrees());
        double currentAngle = Mathutils.wpiAngleTo5k(DriveTrain.getInstance().getPosition().getRotation().getDegrees());

        // Find error
        double error = Mathutils.getWrappedError(currentAngle, setpointAngle);

        return error;
    }

    @Override
    public void execute() {

        // Determine system error
        double error = getError();

        // Get the system output
        double output = m_controller.calculate(error, 0.0);

        // Clamp the output
        output = Mathutils.clamp(output, -1.0, 1.0);

        // Hard cap the turn speed
        output *= TURN_SPEED_HARD_CAP;

        // Send output data to motors
        DriveTrain.getInstance().setOpenLoop(new DriveSignal(output, -output));

        // Increase cycle count
        if (m_controller.atSetpoint()) {
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