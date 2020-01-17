package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.cellmech.Shooter;

/**
 * Test code for the shooter. This will be replaced soon
 */
public class ShooterTester extends CommandBase {

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
    }

}