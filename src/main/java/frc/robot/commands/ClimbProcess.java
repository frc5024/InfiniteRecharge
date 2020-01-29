package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.RobotConstants;
import frc.robot.subsystems.Climber;

public class ClimbProcess extends CommandBase {

    // Operator interface object for reading driver inputs
    private OI m_oi = OI.getInstance();

    /**
     * ClimbProcess constructor
     */
    public ClimbProcess() {

        // Add the Climber as a subsystem requirement
        addRequirements(Climber.getInstance());
    }

    @Override
    public void initialize() {

        // Unlock the climber on initialization
        Climber.getInstance().unlock();
    }

    @Override
    public void execute() {

        // Reads the driver inputs

        // Send control data to the Climber
    }

    @Override
    public boolean isFinished() {
        // When this command is finished, the climber will be put into SERVICE mode
        
        return true;
    }
}