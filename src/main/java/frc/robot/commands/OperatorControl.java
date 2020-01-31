package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.cellmech.Hopper;
import frc.robot.subsystems.cellmech.Intake;
import frc.robot.subsystems.cellmech.Shooter;

/**
 * Test code for the intake/shooter superstructure. This will be replaced soon
 */
public class OperatorControl extends CommandBase {


    private Intake m_intake = Intake.getInstance();
    private Hopper m_hopper = Hopper.getInstance();

    boolean last = true;

    @Override
    public void execute() {

        boolean current = OI.getInstance().shouldShoot();

        if (current == last) {
            return;
        }

        last = current;

        // Set shooting mode
        if (current) {
            Shooter.getInstance().setOutputPercent(0.85);
        } else {
            Shooter.getInstance().stop();
        }

        m_hopper.manuallyControlBelt(OI.getInstance().getHopperBeltSpeed());

        m_intake.manuallyControlArm(OI.getInstance().getHarversterArmSpeed());

        m_intake.manuallyControlArm(OI.getInstance().getHarversterRollerSpeed());
        
    }

}