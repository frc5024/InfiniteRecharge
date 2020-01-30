package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.Climber;

/**
 * Command for handling the climber with Xbox controller
 * inputs.
 */
public class ClimbProcess extends CommandBase {

    // Operator interface for reading driver inputs
    private OI m_oi = OI.getInstance();

    private ClimbController m_ClimbController = new ClimbController();

    /**
     * ClimbProcess constructor
     */
    public ClimbProcess() {

    }

    @Override
    public void execute() {
        // SystemState is set to DEPLOYING

        // Pull the pin on the climber
        if (m_oi.ejectClimber()) {
            System.out.println("Does it work?");
            m_ClimbController.schedule();
        }
    }

    @Override
    public boolean isFinished() {
        // The robot doesn't finish climbing until it is killed
        return false;
    }
}
