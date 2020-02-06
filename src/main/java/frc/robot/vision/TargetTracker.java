package frc.robot.vision;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.roborio.FPGAClock;
import frc.robot.vision.Limelight2.LEDMode;

public class TargetTracker extends SubsystemBase {
    private static TargetTracker s_instance = null;

    // Limelight camera
    private Limelight2 m_limelight;

    // Target check mode
    private boolean m_shouldCheckForTargets = false;
    private double m_lastTimestampSecs = 0;
    private LimelightTarget m_lastSnapshot = new LimelightTarget(0, 0, 0, 0);

    private TargetTracker() {

        // Connect to limelight
        m_limelight = Limelight2.getInstance();

    }

    public static TargetTracker getInstance() {
        if (s_instance == null) {
            s_instance = new TargetTracker();
        }

        return s_instance;
    }

    @Override
    public void periodic() {
        double timestamp = FPGAClock.getFPGASeconds();

        // Handle target checking
        if (m_shouldCheckForTargets && (timestamp - m_lastTimestampSecs) > 1) {

            // Enable LEDs
            m_limelight.setLED(LEDMode.ON);

            // Read target data
            m_lastSnapshot.set(m_limelight.getXAngle(), m_limelight.getYAngle(), m_limelight.getArea(),
                    m_limelight.getSkew());

            // Disable check after a half second
            if ((timestamp - m_lastTimestampSecs) > 1.5) {

                // Set the new timestamp
                m_lastTimestampSecs = timestamp;

                // Disable the lights
                m_limelight.setLED(LEDMode.OFF);
            }

        }

    }

    /**
     * Should target checking be enabled?
     * 
     * When on, the limelight will briefly flash once every second and check for
     * goals.
     * 
     * @param on Enable target checking?
     */
    public void enableTargetChecking(boolean on) {
        m_shouldCheckForTargets = on;
    }

    /**
     * Get the latest target checking snapshot
     * 
     * @return Latest snapshot
     */
    public LimelightTarget getLastSnapshot() {
        return m_lastSnapshot;
    }

    /**
     * Return the current viewed target, or null if vision is disabled, or there is
     * no target to be found
     * 
     * @return Current target
     */
    public LimelightTarget getTarget() {

        // Handle no target or no vision
        if (!m_limelight.hasTarget()) {
            return null;
        }

        return new LimelightTarget(m_limelight.getXAngle(), m_limelight.getYAngle(), m_limelight.getArea(),
                m_limelight.getSkew());

    }

}