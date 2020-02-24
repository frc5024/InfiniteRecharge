package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.lib5k.kinematics.purepursuit.PurePursuitController;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;

public class DrivePath extends CommandBase {

    private PurePursuitController m_controller;

    private Translation2d m_epsilon;

    public DrivePath(Path path, double lookahead, Translation2d epsilon, double kP, double kSpeedCap) {

        this.m_epsilon = epsilon;

        m_controller = new PurePursuitController(path, lookahead, 0.1,
                RobotConstants.DriveTrain.Measurements.DRIVEBASE_LENGTH, kP, kSpeedCap);

    }

    @Override
    public void initialize() {
        m_controller.reset();
    }

    @Override
    public void execute() {

        // Get DriveSignal for drivebase
        DriveSignal signal = m_controller.calculate(DriveTrain.getInstance().getPosition());

        System.out.println(signal);

        DriveTrain.getInstance().setOpenLoop(signal);

    }

    @Override
    public void end(boolean interrupted) {
        DriveTrain.getInstance().stop();
        
    }

    @Override
    public boolean isFinished() {
        return m_controller.isFinished(DriveTrain.getInstance().getPosition(), m_epsilon);
    }
}