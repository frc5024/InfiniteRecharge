package frc.robot.commands.actions.alignment;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.AnalyticsEngine;
import frc.robot.OI;
import frc.robot.AnalyticsEngine.AnalyticEvent;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.cellmech.Shooter;
import frc.robot.vision.Limelight2;
import frc.robot.vision.LimelightTarget;
import frc.robot.vision.SimVision;
import frc.robot.vision.Limelight2.CameraMode;
import frc.robot.vision.Limelight2.LEDMode;

public class PivotAim extends CommandBase {

    // Vision target
    private LimelightTarget m_target;
    private double m_angle;

    public PivotAim() {
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

        // Read the target
        // Find a vision target
        m_target = Limelight2.getInstance().getTarget();

        // If we are simulating, find a simulated target
        if (SimVision.shouldSimulate()) {
            m_target = SimVision.getSimulatedTarget();
        }

        // Get the robot's angle
        double robotAngle = DriveTrain.getInstance().getPosition().getRotation().getDegrees();

        // Set the target angle
        if (m_target != null) {
            m_angle = (m_target.tx * -1) + robotAngle;
        } else {
            m_angle = 0.0;
            Limelight2.getInstance().setLED(LEDMode.BLINK);
        }

        AnalyticsEngine.trackEvent(AnalyticEvent.AIM_PIVOT);

    }

    @Override
    public void execute() {
        // We enable the LEDs here to prevent other commands from disabling it
        Limelight2.getInstance().setLED(LEDMode.DEFAULT);

        if (m_target != null) {

            // Tell the DriveTrain to auto-steer, send outcome to shooter
            Shooter.getInstance().setInPosition(DriveTrain.getInstance().face(Rotation2d.fromDegrees(m_angle), 4));
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

        // Reset target info
        m_target = null;
        m_angle = 0.0;
    }

    @Override
    public boolean isFinished() {

        // If button not held, stop aiming
        return !OI.getInstance().shouldPivotAim();
    }
}