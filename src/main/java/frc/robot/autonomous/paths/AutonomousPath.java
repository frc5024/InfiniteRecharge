package frc.robot.autonomous.paths;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public abstract class AutonomousPath {

    protected boolean score, getBalls = false;

    /**
     * Generate command group using params
     * 
     * @param score    Should score?
     * @param getBalls Should get balls?
     */
    public SequentialCommandGroup generate(boolean score, boolean getBalls) {
        // Set locals
        this.score = score;
        this.getBalls = getBalls;

        // Call and return the internal generator
        return getCommand();
    }

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

    /**
     * Get if the chooser has decided to score during this path
     * 
     * @return Should score?
     */
    public boolean shouldScore() {
        return score;
    }

    /**
     * Get if the chooser has decided to pick up balls during this path
     * 
     * @return Should pick up balls?
     */
    public boolean shouldGetBalls() {
        return getBalls;
    }

}