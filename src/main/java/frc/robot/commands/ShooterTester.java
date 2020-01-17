package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.cellmech.Shooter;

/**
 * Test code for the shooter. This will be replaced soon
 */
public class ShooterTester extends CommandBase {

    @Override
    public void execute() {

        // Set shooting mode
        if (OI.getInstance().shouldShoot()) {
            Shooter.getInstance().setOutputPercent(0.85);
        } else {
            Shooter.getInstance().stop();
        }
    }

}