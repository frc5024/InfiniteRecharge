package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.cellmech.Hopper;
import frc.robot.subsystems.cellmech.Intake;

/**
 * Test code for the intake/shooter superstructure. This will be replaced soon
 */
public class ManualControl extends CommandBase {

    /** Instance of OI */
    private OI m_oi = OI.getInstance();

    private Intake m_intake = Intake.getInstance();

    private Hopper m_hopper = Hopper.getInstance();

    @Override
    public void execute() {

        m_intake.manuallySetArmSpeed(m_oi.getIntakeArmSpeed());

        m_intake.manuallySetRollerSpeed(m_oi.getIntakeRollerSpeed());

        m_hopper.manuallyControlBelt(m_oi.getHopperSpeed());

    }

    @Override
    public void end(boolean interrupted) {

        m_intake.manuallySetArmSpeed(0.0);

        m_intake.manuallySetRollerSpeed(0.0);

        m_hopper.manuallyControlBelt(0.0);
        
    }

}