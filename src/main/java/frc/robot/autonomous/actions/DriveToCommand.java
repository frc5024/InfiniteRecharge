package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.autonomous.helpers.SpeedConstraint;
import frc.robot.subsystems.DriveTrain;

/**
 * A command that will drive the robot from a dynamic pose to an absolute pose
 */
public class DriveToCommand extends CommandBase {

    // Internal LinePath command
    private LinePath m_internalCommand;

    // Goal pose
    private Pose2d m_goal;

    // Movement constraints
    private SpeedConstraint m_constraints;

    // Should drive reversed?
    private boolean m_reversed;

    /**
     * Create a DriveToCommand
     * 
     * @param goal Goals pose
     */
    public DriveToCommand(Pose2d goal) {
        this(goal, new SpeedConstraint(1));
    }

    /**
     * Create a DriveToCommand
     * 
     * @param goal        Goals pose
     * @param constraints Speed constraints
     */
    public DriveToCommand(Pose2d goal, SpeedConstraint constraints) {
        this(goal, constraints, false);
    }

    /**
     * Create a DriveToCommand
     * 
     * @param goal        Goals pose
     * @param constraints Speed constraints
     * @param reversed    Should drive backwards?
     */
    public DriveToCommand(Pose2d goal, SpeedConstraint constraints, boolean reversed) {
        this.m_goal = goal;
        this.m_constraints = constraints;
        this.m_reversed = reversed;

    }

    @Override
    public void initialize() {
        // Get current robot pose
        Pose2d current = DriveTrain.getInstance().getPosition();

        // Generate a LinePath to follow
        m_internalCommand = new LinePath(current, m_goal, m_constraints, m_reversed);

        // Init the LinePath
        m_internalCommand.initialize();

    }

    @Override
    public void execute() {

        // Execute the LinePath
        m_internalCommand.execute();
    }

    @Override
    public void end(boolean interrupted) {

        // End the LinePath
        m_internalCommand.end(interrupted);
    }

    @Override
    public boolean isFinished() {

        // Check if the LinePath is finished
        return m_internalCommand.isFinished();
    }
}