package frc.lib5k.kinematics.purepursuit;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.lib5k.kinematics.statespace.models.DrivebaseState;

public class Follower {

    // Path to follow
    private Path m_path;

    // Lookahead settings
    private double m_lookaheadDist, m_lookaheadGain;
    private Integer m_lastLookaheadIndex = null;

    // Drivebase info
    private double m_drivebaseWidth;
    private Pose2d m_lastPose = null;

    /**
     * Create a path follower
     * 
     * @param path              Path to follow
     * @param lookaheadDistance Lookahead distance
     * @param lookaheadGain     Lookahead gain
     * @param drivebaseWidth    Drivebase width
     */
    public Follower(Path path, double lookaheadDistance, double lookaheadGain, double drivebaseWidth) {
        this.m_path = path;
        this.m_lookaheadDist = lookaheadDistance;
        this.m_lookaheadGain = lookaheadGain;
        this.m_drivebaseWidth = drivebaseWidth;

    }

    /**
     * Reset the follower
     */
    public void reset() {
        m_lastLookaheadIndex = null;
        m_lastPose = null;
    }

    public Translation2d getNextPoint(Pose2d robotPose) {

        // Cast the pose up to a drivebase state
        DrivebaseState state = (DrivebaseState) robotPose;
        Translation2d stateRear = state.getRear(m_drivebaseWidth);

        // Max nearest pose
        Translation2d nearest = new Translation2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);

        // Check if this is the first run
        if (m_lastLookaheadIndex == null) {

            // Search for the nearest pose
            for (int i = 0; i < m_path.getPoses().length; i++) {

                // Determine diff
                double dx = stateRear.getX() - m_path.getPoses()[i].getX();
                double dy = stateRear.getY() - m_path.getPoses()[i].getY();

                // Determine distance
                double d = Math.hypot(dx, dy);

                // If d is closer than the nearest, we have found a new nearest point
                if (d < Math.hypot(stateRear.getX() - nearest.getX(), stateRear.getY() - nearest.getY())) {

                    // Set the nearest translation
                    nearest = m_path.getPoses()[i];

                    // Set the lookahead index
                    m_lastLookaheadIndex = i;
                }
            }

        }

    }
}