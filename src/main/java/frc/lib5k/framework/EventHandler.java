package frc.lib5k.framework;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Notifier;
import frc.lib5k.framework.events.RobotState;
import frc.lib5k.framework.events.RobotStateChangeEvent;
import frc.lib5k.framework.events.eventbasedcomponents.InstantDigitalTrigger;

public class EventHandler {
    // EventHandler instance
    private static EventHandler s_instance = null;

    // Internal update loop
    private Notifier m_notifier;

    // Registered robotStateChange event callbacks
    private ArrayList<RobotStateChangeEvent> m_robotStateChangeCallbacks = new ArrayList<>();

    // Registered Digital triggers
    private ArrayList<InstantDigitalTrigger> m_digitalTriggerCallbacks = new ArrayList<>();

    private EventHandler() {

        // Create, and enable thread
        m_notifier = new Notifier(this::update);
        m_notifier.startPeriodic(0.01);

    }

    public static EventHandler getInstance() {
        if (s_instance == null) {
            s_instance = new EventHandler();
        }
        return s_instance;
    }

    /**
     * Set the robot's state, and call any registered callbacks for this state. This
     * should not be called by the user unless you really know what you are doing.
     * 
     * @param state Robot's state
     */
    protected void observeRobotState(RobotState state) {

        // Check all callbacks to see if anything listens for this state
        for (RobotStateChangeEvent event : m_robotStateChangeCallbacks) {
            if (event.watchesFor(state)) {
                event.execute();
            }
        }

    }

    /**
     * Add a digital trigger to watch
     * 
     * @param trigger Trigger
     */
    public void addDigitalTrigger(InstantDigitalTrigger trigger) {
        m_digitalTriggerCallbacks.add(trigger);
    }

    /**
     * Remove a digital trigger from the watchlist
     * 
     * @param trigger Trigger
     */
    public void removeDigitalTrigger(InstantDigitalTrigger trigger) {
        m_digitalTriggerCallbacks.remove(trigger);
    }

    private void update() {

        // Update all digital triggers
        for (InstantDigitalTrigger t : m_digitalTriggerCallbacks) {
            t.handleCallback();
        }
    }

}