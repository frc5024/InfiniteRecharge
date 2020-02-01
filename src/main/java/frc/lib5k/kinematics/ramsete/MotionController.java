package frc.lib5k.kinematics.ramsete;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.geometry.Twist2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds;
import frc.lib5k.kinematics.systemdef.VelocityConstraint;
import frc.lib5k.kinematics.systemdef.VelocityDefinition;
import frc.lib5k.utils.Mathutils;

/**
 * A RAMSETE-like Differential drivebase movement controller based from
 * "Robotica articolata e mobile per i servizi e letecnologie", "Controls
 * Engineering in the FIRST Robotics Competition", and some code snippets
 * provided by team 1114.
 * 
 * <br>
 * <br>
 * This controller was designed by Evan Pratten (ewpratten) for use by FRC team
 * 5024.
 */
public class MotionController {

    // Chassis absolute maximum velocity
    private VelocityDefinition maxVelocity;

    // Turning constraints
    double turnRate, maxTurnPercent;

    // Conversion P gains
    double turnP, distP;

    /**
     * Create a MotionController
     * 
     * @param absMaxVelocity
     * @param turnRate
     * @param maxTurnPercent
     * @param rotationP
     * @param distanceP
     */
    public MotionController(VelocityDefinition absMaxVelocity, double turnRate, double maxTurnPercent, double rotationP,
            double distanceP) {
        this.maxVelocity = absMaxVelocity;
        this.turnRate = turnRate;
        this.maxTurnPercent = maxTurnPercent;
        this.turnP = rotationP;
        this.distP = distanceP;

    }

    public static Translation2d rotateBy(Pose2d pose, double angle) {

        // Convert to radians
        angle = Math.toRadians(angle);

        // Get new X and Y poses
        double x = pose.getTranslation().getX() * Math.cos(angle) - pose.getTranslation().getY() * Math.sin(angle);
        double y = pose.getTranslation().getX() * Math.sin(angle) + pose.getTranslation().getY() * Math.cos(angle);

        // Create the translation
        return new Translation2d(x, y);
    }

    public static Twist2d getRotatedError(Pose2d a, Pose2d b) {

        // Get the rotated poses
        Translation2d rotatedA = rotateBy(a, b.getRotation().getDegrees());
        Translation2d rotatedB = rotateBy(b, b.getRotation().getDegrees());

        // Determine X and Y errors
        double dx = rotatedB.getX() - rotatedA.getX();
        double dy = rotatedB.getY() - rotatedA.getY();

        // Construct a Twist
        return new Twist2d(dx, dy, b.getRotation().getDegrees() - a.getRotation().getDegrees());
    }

    public ChassisSpeeds calculate(Pose2d robotPose, Pose2d goalPose, DifferentialDriveWheelSpeeds speeds,
            VelocityConstraint constraints) {

        Twist2d error = getRotatedError(robotPose, goalPose);

        // Flip our X error if we are
        if (error.dx < 0) {
            error.dx *= -1;
        }

        // Increase turning aggression based on path progress
        double turnModifier = (error.dx * turnRate);

        // Bind the turnModifier to the max turn rate or 0
        turnModifier = Mathutils.clamp(turnModifier, -maxTurnPercent, maxTurnPercent);

        // Restrict the end theta by the modifier. This makes the robot turn more based
        // on path completion
        double targetHeading = goalPose.getRotation().getDegrees() - turnModifier;

        // Determine the error from the current angle to the end angle
        double headingErr = targetHeading - robotPose.getRotation().getDegrees();

        // Determine the desired robot speed
        double speed = error.dy * distP;

        // Implement distance-based speed ramping
        double cappedHeadingErr = (headingErr > 90) ? 90 : headingErr;
        double speedModifier = (((-1 * cappedHeadingErr) / 90.0) + 1);

        // Modify speed with distance-based speed ramping
        speed *= speedModifier;

        // Determine turn rate
        double turn = headingErr * turnP;

        // Convert inputs to left/right outputs
        double left = (speed + turn) * maxVelocity.maxVelocity;
        double right = (speed - turn) * maxVelocity.maxVelocity;

        // Clamp by the maximum velocity
        left = Mathutils.clamp(left, -maxVelocity.maxVelocity, maxVelocity.maxVelocity);
        right = Mathutils.clamp(right, -maxVelocity.maxVelocity, maxVelocity.maxVelocity);

        return null;
    }

}