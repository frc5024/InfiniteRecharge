package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.RobotConstants;
import frc.robot.subsystems.Climber;
import edu.wpi.first.wpilibj.Servo;

/**
 * Default command for controlling the robot climber with Xbox controller
 * inputs.
 */
public class ClimbSystem extends CommandBase {

    // Operator interface object for reading driver inputs
    private OI m_oi = OI.getInstance();

    /**
     * ClimbSystem constructor
     */
    public ClimbSystem() {

        // Add the Climber as a subsystem requirement
        addRequirements(Climber.getInstance());
    }

    @Override
    public void execute() {
    }

    @Override
    public boolean isFinished() {
        // This is designed to be a "default command", so it must always return false;
        return false;
    }
}
