package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.Climber;

/**
 * Command for handling the climber with Xbox controller inputs.
 */
public class ClimbProcess extends CommandBase {

    // Operator interface for reading driver inputs
    private OI m_oi = OI.getInstance();

    private ClimbController m_climbController = new ClimbController();

    @Override
    public void initialize() {

        // Lock the climber
        Climber.getInstance().lock();
    }

    @Override
    public void execute() {
        // Pull the pin on the climber
        if (m_oi.shouldEjectClimber()) {
            m_climbController.schedule();
        } else if (m_oi.shouldCancelClimb()) {
            m_climbController.cancel();
        }

    }

    @Override
    public boolean isFinished() {
        // The robot doesn't finish climbing until it is killed
        return false;
    }

    /**
     * Kill the climbing command
     */
    public void killClimbCommand() {
        m_climbController.cancel();
    }
}
