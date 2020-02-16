package frc.lib5k.framework;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.lib5k.roborio.FPGAClock;

/**
 * A framework for robot applications
 */
public abstract class RobotFramework extends TimedRobot {

    /**
     * An event for the future
     * 
     * @param action    Action to run
     * @param timestamp When to run it
     */
    private static class FutureEvent {
        Runnable action;
        double timestamp;

        FutureEvent(Runnable action, double timestamp) {
            this.action = action;
            this.timestamp = timestamp;
        }
    }

    private static ArrayList<FutureEvent> m_events = new ArrayList<>();

    @Override
    public void robotPeriodic() {

        // Get robot timestamp
        double timestamp = FPGAClock.getFPGAMilliseconds();

        // Find events that should be run
        for (FutureEvent e : m_events) {

            // Check if it should be run
            if (e.timestamp <= timestamp) {

                // Run event
                e.action.run();

                // Un-schedule event
                m_events.remove(e);
            }
        }

    }

    /**
     * Schedule something to be run N ms in the future
     * 
     * @param action     Action
     * @param msInFuture How much time to wait before running
     */
    public static void schedule(Runnable action, double msInFuture) {

        // Determine absolute timestamp
        double timestamp = FPGAClock.getFPGAMilliseconds() + msInFuture;

        // Add the action
        m_events.add(new FutureEvent(action, timestamp));
    }

}