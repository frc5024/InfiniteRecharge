package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.RobotConstants;
import frc.robot.subsystems.Climber;

public class ClimbProcess extends CommandBase {

    // Operator interface for reading driver inputs
    private OI m_oi = OI.getInstance();

    /**
     * ClimbProcess constructor
     */
    public ClimbProcess() {

        // Add the ClimbProcess as a subsystem requirement
        addRequirements(Climber.getInstance());

    }

    @Override
    public void initialize() {
        Climber.getInstance().unlock();
    }

    @Override
    public void execute() {
        // Pull the pin on the climber
        Climber.getInstance().setSolenoid(m_oi.ejectClimber());
        
        // Read driver inputs
        double speed = m_oi.retractClimber();

        // Send control data to the Climber
        Climber.getInstance().retractMotor(speed);
    }

    @Override
    public boolean isFinished() {
        Climber.getInstance().lock();
        return true;
    }
}
