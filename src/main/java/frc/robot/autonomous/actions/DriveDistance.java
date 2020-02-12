package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.utils.MathUtils;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;

public class DriveDistance extends CommandBase {

    private double end, start;
    private double err_offset, eps;
    private PIDController m_distanceController;
    private double speed;

    public DriveDistance(double meters, double epsilon, double speed) {
        this.end = meters;
        this.eps = epsilon;
        this.speed = speed;

        this.m_distanceController = new PIDController(RobotConstants.ControlGains.kPDriveVel,
                RobotConstants.ControlGains.kIDriveVel, RobotConstants.ControlGains.kDDriveVel);

    }

    /**
     * Get positional error between encoder readings
     */
    private double getEncoderError() {
        return DriveTrain.getInstance().getLeftMeters() - DriveTrain.getInstance().getRightMeters();
    }

    private double getDistance() {
        return (DriveTrain.getInstance().getLeftMeters() + DriveTrain.getInstance().getRightMeters()) / 2;
    }

    @Override
    public void initialize() {

        // Set the error offset
        err_offset = getEncoderError();

        // Set the start pose
        start = getDistance();

        // Reset the PID controller
        m_distanceController.reset();

        // Set the ramp rate low to slowly speed up
        DriveTrain.getInstance().setRampRate(0.25);

    }

    @Override
    public void execute() {

        // Calculate turning corrective power
        double error = getEncoderError() - err_offset;
        double turnPower = error * RobotConstants.ControlGains.kRP;

        // Calculate distance power
        double power = m_distanceController.calculate(getDistance() - start, end);

        power = MathUtils.clamp(power, -1, 1);
        power *= this.speed;

        DriveTrain.getInstance().setOpenLoop(new DriveSignal(power + turnPower, power - turnPower));

    }

    @Override
    public void end(boolean interrupted) {
        DriveTrain.getInstance().stop();
    }

    @Override
    public boolean isFinished() {
        return MathUtils.epsilonEquals(getDistance() - start, end, eps);
    }
}