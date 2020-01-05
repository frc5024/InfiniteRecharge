package frc.robot.autonomous;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;

/**
 * Class for handling autonomous command generation
 */
public class Chooser {

    /**
     * Publish all chooser options to Shuffleboard
     */
    public void publishOptions() {

        // TODO: Publish choosers and selectors here

    }

    /**
     * Generate an Autonomous command to be run based on chooser inputs
     * 
     * @return Generated command
     */
    public CommandBase generateAutonomousCommand() {

        // TODO: Generate a command / commandgroup to be run in autonomous here.

        return null;
    }

    /**
     * Get a Pose2d representing the robot's exact starting location for the
     * selected autonomous mode
     * 
     * @return Robot position
     */
    public Pose2d getRobotAutoStartPosition() {

        // TODO: Write code to return a Pose2d based on autonomous mode here
        return new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0));
    }
}