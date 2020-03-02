package frc.lib5k.autonomous;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public abstract class AutonomousSequence {

    /**
     * Get the command to run
     * 
     * @return Command
     */
    protected abstract SequentialCommandGroup getCommand();

    /**
     * Get the path's starting pose
     * 
     * @return Starting pose
     */
    public abstract Pose2d getStartingPose();

   

}