package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.cellmech.Shooter;
import frc.robot.vision.Limelight2;
import frc.robot.vision.LimelightTarget;
import frc.robot.vision.SimVision;
import frc.robot.vision.Limelight2.CameraMode;
import frc.robot.vision.Limelight2.LEDMode;

public class PivotAutoAim extends CommandBase {

    public PivotAutoAim() {
        addRequirements(DriveTrain.getInstance());
    }

    @Override
    public void initialize() {
        Limelight2.getInstance().use(true);
        DriveTrain.getInstance().stop();
        DriveTrain.getInstance().setBrakes(false);
        OI.getInstance().rumbleDriver(0.5);
        // Put the limelight in "Main" mode for driver assist
        Limelight2.getInstance().setCamMode(CameraMode.PIP_MAIN);
    }

    @Override
    public void execute() {
        // We enable the LEDs here to prevent other commands from disabling it
        Limelight2.getInstance().setLED(LEDMode.DEFAULT);

        // Find a vision target
        LimelightTarget target = Limelight2.getInstance().getTarget();

        // If we are simulating, find a simulated target
        if (SimVision.shouldSimulate()) {
            target = SimVision.getSimulatedTarget();
        }

        // Get the robot's angle
        double robotAngle = DriveTrain.getInstance().getPosition().getRotation().getDegrees();

        if (target != null) {

            // Tell the DriveTrain to auto-steer, send outcome to shooter
            Shooter.getInstance()
                    .setInPosition(DriveTrain.getInstance().face(Rotation2d.fromDegrees(target.tx + robotAngle), 0.8));
        } else {
            // Blink the light if we are the only user, and there is no target
            if (Limelight2.getInstance().users == 1) {
                Limelight2.getInstance().setLED(LEDMode.BLINK);
            }
        }
    }

    @Override
    public void end(boolean interrupted) {

        // Turn off the LEDs
        Limelight2.getInstance().setLED(LEDMode.OFF);

        Limelight2.getInstance().use(false);
        DriveTrain.getInstance().stop();
        DriveTrain.getInstance().setBrakes(true);
        OI.getInstance().rumbleDriver(0.0);

        // Put the limelight in "Secondary" mode for driver assist
        Limelight2.getInstance().setCamMode(CameraMode.PIP_SECONDARY);
    }

    @Override
    public boolean isFinished() {

        // If button not held, stop aiming
        return !OI.getInstance().shouldAutoAim();
    }
}