package frc.lib5k.kinematics.purepursuit;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;

public class Follower {

    // Path to follow
    private Path m_path;

    public Follower(Path path) {
        this.m_path = path;
    }

    /**
     * Set the following path
     * 
     * @param path Path to follow
     */
    public void setPath(Path path) {
        this.m_path = path;
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
     * Get the goal pose inside a lookahead
     * 
     * @param robotPose       Robot's pose
     * @param lookaheadMeters Lookahead distance
     * @return Goal pose
     */
    private Translation2d getGoalPose(Pose2d robotPose, double lookaheadMeters) {
        // Create an output pose
        Translation2d output = null;

        // iterate through all pairs of points
        for (int i = 0; i < m_path.getPoses().size() - 1; i++) {
            // form a segment from each two adjacent points
            Translation2d segmentStart = m_path.getPoses().get(i).getTranslation();
            Translation2d segmentEnd = m_path.getPoses().get(i + 1).getTranslation();

            // translate the segment to the origin
            Translation2d p1 = new Translation2d(segmentStart.getX() - robotPose.getTranslation().getX(),
                    segmentStart.getY() - robotPose.getTranslation().getY());
            Translation2d p2 = new Translation2d(segmentEnd.getX() - robotPose.getTranslation().getX(),
                    segmentEnd.getY() - robotPose.getTranslation().getY());

            // calculate an intersection of a segment and a circle with radius r (lookahead)
            // and origin (0, 0)
            double dx = p2.getX() - p1.getX();
            double dy = p2.getY() - p1.getY();
            double d = Math.sqrt(dx * dx + dy * dy);
            double D = p1.getX() * p2.getY() - p2.getX() * p1.getY();

            // if the discriminant is zero or the points are equal, there is no intersection
            double discriminant = lookaheadMeters * lookaheadMeters * d * d - D * D;
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
                output = new Translation2d(x1 + robotPose.getTranslation().getX(),
                        y1 + robotPose.getTranslation().getY());
            }

            // select the second one if it's valid and either lookahead is none,
            // or it's closer to the end of the segment than the first intersection
            if (validIntersection2) {
                if (output == null || Math.abs(x1 - p2.getX()) > Math.abs(x2 - p2.getX())
                        || Math.abs(y1 - p2.getY()) > Math.abs(y2 - p2.getY())) {
                    output = new Translation2d(x2 + robotPose.getTranslation().getX(),
                            y2 + robotPose.getTranslation().getY());
                }
            }
        }

        // special case for the very last point on the path
        if (m_path.getPoses().size() > 0) {
            Translation2d lastPoint = m_path.getPoses().get(m_path.getPoses().size() - 1).getTranslation();

            double endX = lastPoint.getX();
            double endY = lastPoint.getY();

            // if we are closer than lookahead distance to the end, set it as the lookahead
            if (Math.sqrt((endX - robotPose.getTranslation().getX()) * (endX - robotPose.getTranslation().getX())
                    + (endY - robotPose.getTranslation().getY())
                            * (endY - robotPose.getTranslation().getY())) <= lookaheadMeters) {
                return new Translation2d(endX, endY);
            }
        }

        return output;
    }

    public Movement calculate(Pose2d robotPose, double lookaheadMeters) {

        // Find our next goal
        Translation2d goal = getGoalPose(robotPose, lookaheadMeters);

        // Determine our real error
        Translation2d error = new Translation2d(goal.getX() - robotPose.getTranslation().getX(),
                goal.getY() - robotPose.getTranslation().getY());

        // Determine our error as robot-friendly arguments
        // This will flip Y, and offset the heading to match out field coordinate
        // system.
        double heading = Math.atan2(error.getX(), error.getY() * -1) ;
        double distance = Math.hypot(error.getX(), error.getY() * -1);

        // Construct a movement object
        return new Movement(distance, heading);

    }
}