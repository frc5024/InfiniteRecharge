package frc.robot;

import java.util.HashMap;

import frc.lib5k.utils.RobotLogger;

public class AnalyticsEngine {

    /**
     * Trackable robot events
     */
    public enum AnalyticEvent {
        BALL_SHOT("Balls shot: %d"), BALL_INTAKE("Balls loaded: %d"), BALL_UNJAM("Times unjamed: %d"),
        BALL_UNJAM_UP("Times unjammed up: %d"), BALL_OVERRIDE("Ball count override events: %d"),
        BALL_RESET("Ball count reset requests: %d"), AIM_FREESPACE("Autoaim actions (free space): %d"),
        AIM_PIVOT("Autoaim actions (pivot): %d"), CTRLPANEL_ROTATION("Control panel rotation actions: %d"),
        CTRLPANEL_COLOR("Control panel incr color actions: %d"), CTRLPANEL_TIME("Time-based control panel actions: %d"),
        CLIMB_REQUEST("Climb requests: %d");

        public String fString;

        private AnalyticEvent(String fString) {
            this.fString = fString;
        }

    }

    private static HashMap<AnalyticEvent, Integer> m_events = new HashMap<>();

    /**
     * Track an event
     * 
     * @param e Event to track
     */
    public static void trackEvent(AnalyticEvent e) {

        // Incr counter
        int count = m_events.getOrDefault(e, 0);
        m_events.put(e, count + 1);

    }

    public static void logStats() {

        // Log header
        RobotLogger.getInstance().log("AnalyticsEngine", "----- Begin Match Analytics -----");

        // Log every recorded event
        m_events.forEach((e, c) -> {

            // Log event
            RobotLogger.getInstance().log("AnalyticsEngine", String.format(e.fString, c));

        });

        // Log footer
        RobotLogger.getInstance().log("AnalyticsEngine", "------ End Match Analytics ------");
    }

    public static void resetStats() {
        RobotLogger.getInstance().log("AnalyticsEngine", "Cleared event map");
        m_events.clear();
    }
}