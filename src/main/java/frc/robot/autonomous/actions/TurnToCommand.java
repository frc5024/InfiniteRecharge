package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.utils.Mathutils;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;

/**
 * Command for handling autonomous turning
 */
public class TurnToCommand extends CommandBase {

    private PIDController m_controller;
    private Rotation2d setpoint;

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

        // Send output data to motors
        DriveTrain.getInstance().setOpenLoop(new DriveSignal(output, -output));
    }

    @Override
    public void end(boolean interrupted) {

        // Stop the drivetrain
        DriveTrain.getInstance().stop();
    }

    @Override
    public boolean isFinished() {

        // If we reached the setpoint, we are finished
        return m_controller.atSetpoint();
    }
}