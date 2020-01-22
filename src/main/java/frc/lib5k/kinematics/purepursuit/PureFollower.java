package frc.lib5k.kinematics.purepursuit;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.lib5k.kinematics.DriveSignal;

/**
 * A Pure Pursuit path following implementation
 */
public class PureFollower {

    // Path to follow
    private Path path;

    // Lookahead distance
    private double m_lookahead;

    // Tracker for path completeion
    private boolean isFinished = false;

    // PID controller for speed
    private PIDController m_controller;

    // Rotational P gain
    private double m_RP;

    /**
     * Create a PureFollower
     * 
     * @param path        Path to follow
     * @param movementPID Robot movement PID
     * @param rotationP   Robot path control P Gain
     * @param lookahead   Lookahead distance
     */
    public PureFollower(Path path, PIDController movementPID, double rotationP, double lookahead) {
        this.path = path;
        this.m_lookahead = lookahead;
        this.m_controller = movementPID;
        this.m_RP = rotationP;

    }

    /**
     * Return the sign of a number, forcing 0's sign to be +1
     * 
     * @param n Number
     * @return Sign
     */
    private double signum(double n) {
        if (n == 0)
            return 1;
        else
            return Math.signum(n);
    }

    /**
     * Calculate a lookahead pose from the robot's current pose
     * 
     * This was adapted from https://github.com/xiaoxiae/PurePursuitAlgorithm
     * 
     * @param robot Current robot pose
     * @return Lookahead translation (field-relative) or null
     */
    public Translation2d getLookaheadPose(Pose2d robot) {

        // Create an output pose
        Translation2d output = null;

        // iterate through all pairs of points
        for (int i = 0; i < path.getPoses().size() - 1; i++) {
            // form a segment from each two adjacent points
            Translation2d segmentStart = path.getPoses().get(i).getTranslation();
            Translation2d segmentEnd = path.getPoses().get(i + 1).getTranslation();

            // translate the segment to the origin
            Translation2d p1 = new Translation2d(segmentStart.getX() - robot.getTranslation().getX(),
                    segmentStart.getY() - robot.getTranslation().getY());
            Translation2d p2 = new Translation2d(segmentEnd.getX() - robot.getTranslation().getX(),
                    segmentEnd.getY() - robot.getTranslation().getY());

            // calculate an intersection of a segment and a circle with radius r (lookahead)
            // and origin (0, 0)
            double dx = p2.getX() - p1.getX();
            double dy = p2.getY() - p1.getY();
            double d = Math.sqrt(dx * dx + dy * dy);
            double D = p1.getX() * p2.getY() - p2.getX() * p1.getY();

            // if the discriminant is zero or the points are equal, there is no intersection
            double discriminant = m_lookahead * m_lookahead * d * d - D * D;
            if (discriminant < 0 || p1.equals(p2)) {
                continue;
            }

            // the x components of the intersecting points
            double x1 = (double) (D * dy + signum(dy) * dx * Math.sqrt(discriminant)) / (d * d);
            double x2 = (double) (D * dy - signum(dy) * dx * Math.sqrt(discriminant)) / (d * d);

            // the y components of the intersecting points
            double y1 = (double) (-D * dx + Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);
            double y2 = (double) (-D * dx - Math.abs(dy) * Math.sqrt(discriminant)) / (d * d);

            // whether each of the intersections are within the segment (and not the entire
            // line)
            boolean validIntersection1 = Math.min(p1.getX(), p2.getX()) < x1 && x1 < Math.max(p1.getX(), p2.getX())
                    || Math.min(p1.getY(), p2.getY()) < y1 && y1 < Math.max(p1.getY(), p2.getY());
            boolean validIntersection2 = Math.min(p1.getX(), p2.getX()) < x2 && x2 < Math.max(p1.getX(), p2.getX())
                    || Math.min(p1.getY(), p2.getY()) < y2 && y2 < Math.max(p1.getY(), p2.getY());

            // remove the old lookahead if either of the points will be selected as the
            // lookahead
            if (validIntersection1 || validIntersection2)
                output = null;

            // select the first one if it's valid
            if (validIntersection1) {
                output = new Translation2d(x1 + robot.getTranslation().getX(), y1 + robot.getTranslation().getY());
            }

            // select the second one if it's valid and either lookahead is none,
            // or it's closer to the end of the segment than the first intersection
            if (validIntersection2) {
                if (output == null || Math.abs(x1 - p2.getX()) > Math.abs(x2 - p2.getX())
                        || Math.abs(y1 - p2.getY()) > Math.abs(y2 - p2.getY())) {
                    output = new Translation2d(x2 + robot.getTranslation().getX(), y2 + robot.getTranslation().getY());
                }
            }
        }

        // special case for the very last point on the path
        if (path.getPoses().size() > 0) {
            Translation2d lastPoint = path.getPoses().get(path.getPoses().size() - 1).getTranslation();

            double endX = lastPoint.getX();
            double endY = lastPoint.getY();

            // if we are closer than lookahead distance to the end, set it as the lookahead
            if (Math.sqrt((endX - robot.getTranslation().getX()) * (endX - robot.getTranslation().getX())
                    + (endY - robot.getTranslation().getY()) * (endY - robot.getTranslation().getY())) <= m_lookahead) {
                return new Translation2d(endX, endY);
            }
        }

        return output;
    }

    /**
     * Reset the follower
     */
    public void reset() {
        isFinished = false;
        m_controller.reset();
    }

    /**
     * Has the robot reached it's goal?
     * 
     * @return Goal reached?
     */
    public boolean atEnd() {
        return isFinished;
    }

    /**
     * Calculate drivebase outputs based on robot location
     * 
     * @param robot   Robot position on the field
     * @param epsilon End position epsilon
     * @return Drivebase signal
     */
    public DriveSignal follow(Pose2d robot, double epsilon) {

        // Determine out next target
        Translation2d target = getLookaheadPose(robot);

        // Only calculate if the target exists
        if (target == null) {
            // Set path completion to True
            isFinished = true;

            // Return an empty driveSignal
            return new DriveSignal(0, 0);
        }

        // Calculate movement delta
        Translation2d delta = new Translation2d(target.getX() - robot.getTranslation().getX(),
                target.getY() - robot.getTranslation().getY());

        // Determine distance from the target to the robot
        double distance = 2 * Math.sqrt(Math.pow(delta.getX(), 2) + Math.pow(delta.getY(), 2));

        // Handle the robot reaching it's goal
        if (distance < epsilon) {
            // Set path completion to True
            isFinished = true;

            // Return an empty driveSignal
            return new DriveSignal(0, 0);
        }

        /* Handle robot movement command */

        // Determine rotational error
        double rotation = Math.toDegrees(Math.atan2(delta.getY(), target.getX()));

        // Adjust by the robot's rotation
        rotation += robot.getRotation().getDegrees();

        // Adjust by Rotation P
        rotation *= m_RP;

        // Determine speed
        double speed = m_controller.calculate(-distance, 0.0);

        // Convert Speed/Rotation to L/R
        double L = speed + Math.abs(speed) * rotation;
        double R = speed - Math.abs(speed) * rotation;

        System.out.println(L + " " + R);

        // Return a DriveSignal with new movement data
        return new DriveSignal(L, R);

    }
}