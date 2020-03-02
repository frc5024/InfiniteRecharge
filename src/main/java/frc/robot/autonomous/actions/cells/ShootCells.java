package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.vision.Limelight2;
import frc.robot.vision.Limelight2.LEDMode;

public class ShootCells extends SequentialCommandGroup {

    public ShootCells() {
        this(5);
    }

    public ShootCells(int count) {
        addCommands(new InstantCommand(() -> {
            Limelight2.getInstance().setLED(LEDMode.DEFAULT);
        }));
        addCommands(new ShootCellsBackend(count));
        addCommands(new InstantCommand(() -> {
            Limelight2.getInstance().setLED(LEDMode.OFF);
        }));
    }
}