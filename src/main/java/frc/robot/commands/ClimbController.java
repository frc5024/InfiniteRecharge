package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.Climber;

public class ClimbController extends CommandBase {

    // Operator interface for reading driver inputs
    private OI m_oi = OI.getInstance();

    @Override
    public void initialize() {
        Climber.getInstance().unlock();
    }

    @Override
    public void execute() {

        // Tell the climber where to move to
        Climber.getInstance().setPosition(m_oi.getWantedClimbPosition());
    }

    @Override
    public void end(boolean interrupted) {
        // When the climb command finishes, the climber is set back to SERVICE
        Climber.getInstance().lock();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}