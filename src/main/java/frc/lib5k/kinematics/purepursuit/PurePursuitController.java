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
        DrivebaseState state = new DrivebaseState(robotPose);
        Translation2d rear = state.getRear(m_follower.getDrivebaseWidth());

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

        // Handle DivByZero
        if (LF == 0.0) {
            LF = 0.00000001;
        }

        // Determine delta
        double delta = Math.atan2(2.0 * m_follower.getDrivebaseWidth() * Math.sin(alpha) / LF, 1.0) * 0.12;

        double EPS = 86.0;

        System.out.println(delta);
        // System.out.println(Math.abs(Math.toDegrees(Mathutils.getWrappedError(Mathutils.wpiAngleTo5k(robotPose.getRotation().getDegrees()), delta))));

        // Limit dleta
        // if (Math.abs(Math.toDegrees(delta)) < EPS) {
        //     delta *= Math.abs(delta) * 0.09;
        // } else {
        //     delta *= Math.abs(delta) * 0.23;
        // }

        // Determine distance from goal pose
        double dist = state.getDistance(goal, m_follower.getDrivebaseWidth());

        // Determine scaling factor for the signal force based on the amount of
        // turning
        // needed. The more we have to turn, the slower we should drive to the point.
        double forceScale = Mathutils.clamp((1.0 - Math.abs(delta * 0.01)), 0.0, 1.0);

        // Determine a base movement speed from the distance
        double speed = Mathutils.clamp((dist * 1.5), -1, 1);

        // Scale back the speed if needed by large delta
        speed *= forceScale;

        // Handle mis-alignment
        // if(Math.toDegrees(delta) < EPS){
        //     speed *= 0.1;
        // }

        // Create a drive signal
        DriveSignal signal = new DriveSignal(speed + delta, speed - delta);

        // Normalize the signal
        signal = DifferentialDriveCalculation.normalize(signal);

        return signal;
    }

    public boolean isFinished(Pose2d robotPosition, Translation2d epsilon) {
        return Mathutils.epsilonEquals(robotPosition.getTranslation(), m_follower.getFinalPose(), epsilon);
    }
}