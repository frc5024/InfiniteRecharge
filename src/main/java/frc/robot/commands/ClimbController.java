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
        int requestedState = m_oi.getClimbHeight();

        switch (requestedState) {
        case 1:
            Climber.getInstance().setPosition(Climber.Position.HIGH_BAR);
            break;
        case 2:
            Climber.getInstance().setPosition(Climber.Position.LOW_BAR);
            break;
        default:
            Climber.getInstance().setPosition(Climber.Position.CURRENT);
            break;
        }
    }

    @Override
    public void end(boolean interrupted) {
        // When the climb command finishes, the climber is set back to SERVICE
        Climber.getInstance().setService();
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}