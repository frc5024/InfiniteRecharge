package frc.robot.commands.actions;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.actions.alignment.PivotAim;
import frc.robot.vision.Limelight2;
import frc.robot.vision.Limelight2.LEDMode;

public class PivotAutoAim extends SequentialCommandGroup {

    public PivotAutoAim() {

        // Turn on limelight
        addCommands(new InstantCommand(() -> {
            Limelight2.getInstance().setLED(LEDMode.DEFAULT);
        }));

        // Wait for target
        addCommands(new WaitCommand(0.5));

        // Turn to target
        addCommands(new PivotAim());
    }

}