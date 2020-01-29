package frc.lib5k.kinematics.purepursuit;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.lib5k.kinematics.DriveSignal;

public class Follower {

    // Path to follow
    private Path m_path;

    public Follower(Path path) {
        this.m_path = path;
    }

    private Translation2d getGoalPose(Pose2d robotPose, double lookaheadMeters) {
        return null;
    }

    public Movement calculate(Pose2d robotPose, double lookaheadMeters) {

        // Find our next goal
        Translation2d goal = getGoalPose(robotPose, lookaheadMeters);

        // Determine our real error
        Translation2d error = new Translation2d(robotPose.getTranslation().getX() - goal.getX(),
                robotPose.getTranslation().getY() - goal.getY());

        // Determine our error as robot-friendly arguments
        // This will flip Y, and offset the heading to match out field coordinate
        // system.
        double heading = Math.atan2(error.getX(), error.getY() * -1) - 90;
        double distance = Math.hypot(error.getX(), error.getY() * -1);

        // Construct a movement object
        return new Movement(distance, heading);

    }
}