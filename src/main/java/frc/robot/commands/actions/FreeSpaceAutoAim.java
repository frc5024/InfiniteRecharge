package frc.robot.commands.actions;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.actions.alignment.FreeAim;

/**
 * A wrapper for free space auto aim
 */
public class FreeSpaceAutoAim extends SequentialCommandGroup {

    public FreeSpaceAutoAim() {
        
        // Move to target
        addCommands(new FreeAim());

    }
}