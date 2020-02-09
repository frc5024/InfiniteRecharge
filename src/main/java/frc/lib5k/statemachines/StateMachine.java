package frc.lib5k.statemachines;

import frc.lib5k.interfaces.PeriodicComponent;
import frc.lib5k.roborio.FPGAClock;

/**
 * State Machine handler. Handles calling state runners and state changes
 * 
 * @param <T> IStateMapping object
 */
public class StateMachine<T extends IStateMapping> implements PeriodicComponent {

    // Default state
    private T m_defaultState;

    // Current state
    private T m_currentState;

    // Is new tracker
    private boolean m_isNew = true;
    private double m_timestampOffset = 0;

    /**
     * Create a State Machine
     * 
     * @param defaultState Default state
     */
    public StateMachine(T defaultState) {
        // Set the state
        m_defaultState = m_currentState = defaultState;

    }

    /**
     * Update the statemachine and handle calls to state handlers
     */
    @Override
    public void update() {

        // dt tracker
        double dt = FPGAClock.getFPGAMilliseconds();

        // If this is a new state, set the dt offset
        if (m_isNew) {
            m_timestampOffset = dt;
        }

        // Offset the clock
        dt -= m_timestampOffset;

        // Make a call to the state handler
        m_currentState.execute(dt, m_isNew);

    }

    /**
     * Set the statemachine state
     * 
     * @param state State to set
     */
    public void setState(T state) {
        m_currentState = state;

    }

    public T getCurrentState() {
        return m_currentState;
    }

    /**
     * Reset the statemachine to default
     */
    public void reset() {
        m_currentState = m_defaultState;

    }

}