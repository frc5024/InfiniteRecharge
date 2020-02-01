package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.cellmech.Hopper;
import frc.robot.subsystems.cellmech.Intake;
import frc.robot.subsystems.cellmech.Shooter;

/**
 * Command to manually control the superstructure using a controller on port 2 
 */
public class ManualControl extends CommandBase {

    /** Instance of intake */
    private Intake m_intake = Intake.getInstance();
    /** Instance of hopper */
    private Hopper m_hopper = Hopper.getInstance();
    /** Instance of shooter */
    private Shooter m_shooter = Shooter.getInstance();

    /** Instance of OI */
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

        // feed speeds into motors
        m_hopper.manuallyControlBelt(m_oi.getHopperBeltSpeed());

        m_intake.manuallyControlArm(m_oi.getHarversterArmSpeed());

        m_intake.manuallyControlArm(m_oi.getHarversterRollerSpeed());
        
    }

}