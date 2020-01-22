package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;
import frc.robot.vision.Limelight2;
import frc.robot.vision.Limelight2.LEDMode;

public class VisionAlign extends CommandBase {

    // Epsilon
    private static final double DEFAULT_EPSILON = 2.0;

    // Number of cycles to wait before declaring this command "done"
    private static final int MIN_CYCLES = 5;

    // Limelight for targeting
    private Limelight2 m_limelight;

    // Angle setpoint fallback
    private Rotation2d fallback;

    // Counter for controller rest cycles (Thanks 1114 for this idea)
    private int cycles = 0;

    // Angle epsilon
    private double epsilon;

    /**
     * Turn to align with Limelight target if it exists. If none exists, turn to a
     * default rotation.
     * 
     * @param defaultRotation Default rotation, in degrees.
     */
    public VisionAlign(Rotation2d defaultRotation) {
        this(defaultRotation, DEFAULT_EPSILON);
    }

    /**
     * Turn to align with Limelight target if it exists. If none exists, turn to a
     * default rotation.
     * 
     * @param defaultRotation Default rotation, in degrees.
     * @param epsilon         Acceptable error in angle
     */
    public VisionAlign(Rotation2d defaultRotation, double epsilon) {
        this.fallback = defaultRotation;
        this.epsilon = epsilon;

        // Connect to limelight
        m_limelight = Limelight2.getInstance();
    }

    @Override
    public void initialize() {

        // Reset the cycle count
        cycles = 0;

        // Enable Limelight vision tracking
        m_limelight.enableVision(true);
        m_limelight.setLED(LEDMode.ON);
    }

    @Override
    public void execute() {

        // Create a setpoint
        Rotation2d setpoint;

        // Check for a limelight target
        if (m_limelight.hasTarget()) {
            // Set the setpoint to that of the target
            setpoint = Rotation2d.fromDegrees(m_limelight.getXAngle());
        } else {
            // Use fallback angle if no target is found
            setpoint = fallback;
        }

        // Turn to setpoint and rest
        if (DriveTrain.getInstance().face(setpoint, epsilon)) {
            cycles++;
        }
    }

    @Override
    public void end(boolean interrupted) {
        // Stop the drivetrain
        DriveTrain.getInstance().stop();

        // Disable Limelight LED
        m_limelight.setLED(LEDMode.OFF);
    }

    @Override
    public boolean isFinished() {

        // If we reached the setpoint, and are at it for at least n cycles, we have
        // finished
        return cycles > MIN_CYCLES;
    }
}