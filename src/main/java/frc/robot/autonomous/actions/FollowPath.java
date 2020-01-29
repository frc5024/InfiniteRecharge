package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.purepursuit.Path;
import frc.robot.subsystems.DriveTrain;

public class FollowPath extends CommandBase {

    private Path m_path;
    private double m_lookahead, m_epsilon;

    public FollowPath(Path path, double lookahead, double epsilon) {
        this.m_path = path;
        this.m_lookahead = lookahead;
    }

    @Override
    public void initialize() {
        System.out.println("INIT PATH");
        DriveTrain.getInstance().setPath(m_path, m_lookahead);
    }

    @Override
    public void end(boolean interrupted) {
        DriveTrain.getInstance().stop();
    }

    @Override
    public boolean isFinished() {
        return Math.abs(DriveTrain.getInstance().getPathingError()) < m_epsilon && false;
    }
}