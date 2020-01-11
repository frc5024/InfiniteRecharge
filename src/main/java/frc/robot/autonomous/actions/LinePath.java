package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.autonomous.helpers.EasyTrajectory;
import frc.robot.autonomous.helpers.PathGenerator;
import frc.robot.autonomous.helpers.SpeedConstraint;

/**
 * A simple point-to-point movement command.
 * 
 * This works by motion-profiling "desired" motor velocity control frames, then
 * sampling them, and comparing against the real motor data.
 * 
 * The LinePath will automatically turn to face the "end" pose, then move
 * towards it.
 */
public class LinePath extends SequentialCommandGroup {

    /**      
     * Generate a Line  from a start to end pose
     * 
     * @param start       Starting pose (field-relative)
     * @param end         Desired ending pose ( field-relative)
     */
    public LinePath(Pose2d start, Pose2d end){
        this(start, end, false);
    }
    
    /**      
     * Generate a Line  from a start to end pose
     * 
     * @param start       Starting pose (field-relative)
     * @param end         Desired ending pose ( field-relative)
     * @param reversed    Should the path be driven backwards?
     */
    public LinePath(Pose2d start, Pose2d end, boolean reversed) {
        this(start, end, new SpeedConstraint(1,1), reversed);
    }

    /**
     * Generate a LinePath from a start pose to end translation with various
     * settings
     * 
     * @param start       Starting pose (field-relative)
     * @param end         Desired ending translation vector (start-relative)
     * @param constraints Constraints on profile
     * @param reversed    Should the path be driven backwards?
     */
    public LinePath(Pose2d start, Translation2d end, SpeedConstraint constraints, boolean reversed) {
        this(start, new Pose2d(end, start.getRotation()), constraints, reversed);
    }

    /**
     * Generate a LinePath from a start to end pose with various settings
     * 
     * @param start       Starting pose (field-relative)
     * @param end         Desired ending pose (field-relative)
     * @param constraints Constraints on profile
     * @param reversed    Should the path be driven backwards?
     */
    public LinePath(Pose2d start, Pose2d end, SpeedConstraint constraints, boolean reversed) {

        // Determine angle offset based on path reverse setting. This will effectively
        // set the back of the bot to the "front" if reversed
        double angleFlipOffset = (reversed) ? Math.PI : 0;

        // Determine angle from start to end
        Rotation2d theta = new Rotation2d(Math.atan2(end.getTranslation().getY() - start.getTranslation().getY(),
                end.getTranslation().getX() - start.getTranslation().getX()) - angleFlipOffset);

        // Turn to face the end pose
        addCommands(new TurnToCommand(theta, 2.0));

        // Get from start to end
        addCommands(PathGenerator.generate(new EasyTrajectory(new Pose2d(start.getTranslation(), theta), end),
                constraints));

    }

}