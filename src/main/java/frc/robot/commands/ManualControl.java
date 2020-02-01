package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.cellmech.Hopper;
import frc.robot.subsystems.cellmech.Intake;
import frc.robot.subsystems.cellmech.Shooter;

/**
 * Test code for the intake/shooter superstructure. This will be replaced soon
 */
public class ManualControl extends CommandBase {

    private Intake m_intake = Intake.getInstance();
    private Hopper m_hopper = Hopper.getInstance();
    private Shooter m_shooter = Shooter.getInstance();

    private OI m_oi = OI.getInstance();

    boolean last = true;

    @Override
    public void execute() {
        boolean current = m_oi.shouldShoot();

        if (current == last) {
            return;
        }
    
        // Set shooting mode
        if (current) {
            m_shooter.setOutputPercent(0.85);
        } else {
            m_shooter.stop();
        }

        m_hopper.manuallyControlBelt(m_oi.getHopperBeltSpeed());

        m_intake.manuallyControlArm(m_oi.getHarversterArmSpeed());

        m_intake.manuallyControlArm(m_oi.getHarversterRollerSpeed());
        
    }

}