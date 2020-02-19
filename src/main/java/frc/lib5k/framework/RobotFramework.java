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

    // List of events to be run
    private static ArrayList<FutureEvent> m_events = new ArrayList<>();

    // List of subsystems to notify on state change
    private static ArrayList<Subsystem5k> m_subsystems = new ArrayList<>();

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

    @Override
    public void teleopInit() {
        // Notify all systems of an enable action
        for (Subsystem5k s : m_subsystems) {
            s.onRobotEnable();
        }
    }

    @Override
    public void autonomousInit() {
        // Notify all systems of an enable action
        for (Subsystem5k s : m_subsystems) {
            s.onRobotEnable();
        }
    }

    @Override
    public void testInit() {
        // Notify all systems of an enable action
        for (Subsystem5k s : m_subsystems) {
            s.onRobotEnable();
        }
    }

    @Override
    public void disabledInit() {
        // Notify all systems of an enable action
        for (Subsystem5k s : m_subsystems) {
            s.onRobotDisable();
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

    /**
     * Ask the robot framework to notify a system with state change events
     * 
     * @param system Subsystem to notify
     */
    public static void notify(Subsystem5k system) {
        m_subsystems.add(system);
    }

}