package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.vision.Limelight2;
import frc.robot.vision.Limelight2.LEDMode;

public class WaitForLimelightShoot extends SequentialCommandGroup{

    public WaitForLimelightShoot(int cellCount){

        // Wait for a limelight target or 0.5 seconds
        addCommands(new CommandBase() {
            @Override
            public void initialize() {
                Limelight2.getInstance().setLED(LEDMode.DEFAULT);
            }

            @Override
            public boolean isFinished() {
                return Limelight2.getInstance().hasTarget();
            }
        }.withTimeout(0.5));

        // Shoot
        addCommands(new ShootCells(cellCount));
    }
}