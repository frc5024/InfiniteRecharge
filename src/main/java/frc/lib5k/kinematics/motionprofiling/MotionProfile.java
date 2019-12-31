package frc.lib5k.kinematics.motionprofiling;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Waypoint;

public class MotionProfile {

    Waypoint[] m_points;
    Trajectory.Config m_config;
    Trajectory m_trajectory;
    MotionConstraints m_constraints;
    boolean m_reverse;

    public MotionProfile(MotionConstraints constraints, boolean reverse, Waypoint... points) {
        m_constraints = constraints;
        m_points = points;
        m_reverse = reverse;
    }

    /**
     * Generate the MotionProfile from points
     * 
     * @param period Time step between sensor readings (usually 0.02)
     * @return This object
     */
    public MotionProfile generate(double period) {

        // Build the Trajectory config
        m_config = new Trajectory.Config(Trajectory.FitMethod.HERMITE_CUBIC, Trajectory.Config.SAMPLES_HIGH, period,
                m_constraints.getMaxVelocity(), m_constraints.getMaxAccel(), m_constraints.getMaxJerk());

        // Generate the trajectory
        m_trajectory = Pathfinder.generate(m_points, m_config);

        return this;
    }

    /**
     * Get the trajectory, or null if not generated
     * 
     * @return Trajectory
     */
    public Trajectory getTrajectory() {
        return m_trajectory;
    }

    /**
     * Get the trajectory, or generate first with a period of 0.02 seconds
     * 
     * @return Generated trajectory
     */
    public Trajectory getTrajectoryOrGenerate() {
        return getTrajectoryOrGenerate(0.02);
    }

    /**
     * Get the trajectory, or generate first
     * 
     * @param period Time step between sensor readings (usually 0.02)
     * @return Generated trajectory
     */
    public Trajectory getTrajectoryOrGenerate(double period) {
        // Check if the trajectory has been generated yet
        if (m_trajectory == null) {
            generate(period);
        }

        return getTrajectory();
    }

    /**
     * Get the MotionConstraints Object
     * 
     * @return MotionConstraints
     */
    public MotionConstraints getConstraints() {
        return m_constraints;
    }

    /**
     * Should the path be reversed
     * 
     * @return Reversed?
     */
    public boolean getReversed() {
        return m_reverse;
    }
}