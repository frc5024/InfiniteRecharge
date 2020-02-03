package frc.lib5k.kinematics.purepursuit;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.lib5k.components.drive.DifferentialDriveCalculation;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.kinematics.statespace.models.DrivebaseState;
import frc.lib5k.utils.Mathutils;

public class PurePursuitController {

    // Path follower
    private Follower m_follower;

    // Tracker for last robot pose
    private Pose2d m_lastPose = null;

    public PurePursuitController(Path path, double lookaheadDistance, double lookaheadGain, double drivebaseWidth) {

        // Configure follower
        m_follower = new Follower(path, lookaheadDistance, lookaheadGain, drivebaseWidth);
    }

    public void reset() {
        m_follower.reset();
        m_lastPose = null;
    }

    public void configLookahead(double lookaheadDistance) {
        m_follower.setLookaheadDistance(lookaheadDistance);
    }

    public void configLookahead(double lookaheadDistance, double lookaheadGain) {
        m_follower.setLookaheadDistance(lookaheadDistance);
        m_follower.setLookaheadGain(lookaheadGain);
    }

    public DriveSignal calculate(Pose2d robotPose) {

        // Get our goal pose
        Translation2d goal = m_follower.getNextPoint(robotPose);

        // Determine drivebase rear
        Translation2d rear = ((DrivebaseState) robotPose).getRear(m_follower.getDrivebaseWidth());

        // Determine goal alpha
        double alpha = Math.atan2(goal.getY() - rear.getY(), goal.getX() - rear.getX())
                - robotPose.getRotation().getRadians();

        // Determine our velocity from the last pose
        double v;
        if (m_lastPose != null) {
            double dx = robotPose.getTranslation().getX() - m_lastPose.getTranslation().getX();
            double dy = robotPose.getTranslation().getY() - m_lastPose.getTranslation().getY();

            // Calc V
            v = Math.hypot(dx, dy);
        } else {
            v = 0.0;
        }

        // Set the last pose
        m_lastPose = robotPose;

        // Determine lookahead
        double LF = m_follower.getLookaheadGain() * v * m_follower.getLookaheadDistance();

        // Determine delta
        double delta = Math.atan2(2.0 * m_follower.getDrivebaseWidth() * Math.sin(alpha) / LF, 1.0);

        // Create a drive signal
        DriveSignal signal = new DriveSignal(1.0 + delta, 1.0 - delta);

        // Normalize the signal
        signal = DifferentialDriveCalculation.normalize(signal);

        return signal;
    }

    public boolean isFinished(Pose2d robotPosition, Translation2d epsilon) {
        return Mathutils.epsilonEquals(robotPosition.getTranslation(), m_follower.getFinalPose(), epsilon);
    }
}