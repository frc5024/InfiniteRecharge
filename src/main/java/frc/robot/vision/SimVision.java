package frc.robot.vision;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import frc.lib5k.utils.Mathutils;
import frc.robot.subsystems.DriveTrain;

public class SimVision {

    private static final double LIMELIGHT_FOV = 29.8;

    private static final Translation2d[] visionTargets = new Translation2d[] { new Translation2d(0, 0) };

    public static boolean shouldSimulate() {
        return RobotBase.isSimulation();
    }

    public static LimelightTarget getSimulatedTarget() {

        // Get the robot's position as seperate components
        Pose2d robotPose = DriveTrain.getInstance().getPosition();
        double rX = robotPose.getTranslation().getX();
        double rY = robotPose.getTranslation().getY();
        double rTheta = robotPose.getRotation().getRadians();

        // Find all targets within camera FOV, and save the closest
        Translation2d closestTranslation = null;
        LimelightTarget closestTarget = null;
        for (Translation2d target : visionTargets) {

            // Determine if target is within camera bounds
            double dTheta = Math.atan2(target.getY() - rY, target.getX() - rX);

            // Shift dTheta by the chassis angle
            dTheta -= rTheta;

            // If this angle is outside the FOV, skip the target
            if (!Mathutils.epsilonEquals(Math.toDegrees(dTheta), 0.0, LIMELIGHT_FOV)) {
                continue;
            }

            // If another "close" target exists, only override if this one is closer
            if (closestTranslation != null) {

                // Find distances
                double tDist = Math.abs(target.getDistance(robotPose.getTranslation()));
                double oDist = Math.abs(closestTranslation.getDistance(robotPose.getTranslation()));

                // If this distance is greater, skip the target
                if (tDist >= oDist) {
                    continue;
                }
            }

            // Set the closest data
            closestTranslation = target;
            closestTarget = new LimelightTarget(Math.toDegrees(dTheta), 0, 0, 0);
        }

        return closestTarget;
    }
}