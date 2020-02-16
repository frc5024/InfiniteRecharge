package frc.lib5k.framework.events;

import java.util.function.Consumer;

import frc.lib5k.roborio.FPGAClock;

/**
 * Event for robot state changes
 */
public class RobotStateChangeEvent {

    // Action to be run
    private Consumer<Double> m_action;

    // States to watch for
    private RobotState[] m_states;

    /**
     * Create a RobotStateChangeEvent
     * @param action Action to be run
     * @param states States to watch for
     */
    public RobotStateChangeEvent(Consumer<Double> action, RobotState... states) {
        this.m_action = action;
        this.m_states = states;

    }

    /**
     * Execute the state action
     */
    public void execute() {
        // Get robot timestamp
        double timestamp = FPGAClock.getFPGAMilliseconds();

        // Call the action
        m_action.accept(timestamp);
    }

    /**
     * Check if this state change handler watches for a specific robotstate
     * 
     * @param state Robot state to query
     * @return Does the handler watch for this state?
     */
    public boolean watchesFor(RobotState state) {
        for (RobotState s : m_states) {
            if (s == state) {
                return true;
            }
        }

        return false;

    }
}