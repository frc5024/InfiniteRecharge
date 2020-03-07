package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.lib5k.utils.RobotLogger;
import frc.robot.subsystems.cellmech.Shooter;

/**
 * This class is to provide simple shorthand for configuring shooter output
 * during autonomous.
 */
public class SetShooterOutput extends InstantCommand {

    /**
     * Set the shooter output speed (only works during autonomous)
     * 
     * @param outputRPM Output velocity in RPM
     */
    public SetShooterOutput(double outputRPM) {
        super(() -> {
            RobotLogger.getInstance().log("SetShooterOutput",
                    String.format("Setting shooter output to: %.2f", outputRPM));
            Shooter.getInstance().setAutonomousOutput(outputRPM);
        });
    }
}