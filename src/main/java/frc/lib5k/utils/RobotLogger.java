package frc.lib5k.utils;

import java.io.File;
import java.util.ArrayList;
import edu.wpi.first.wpilibj.Notifier;

/**
 * A threaded logger for use by all robot functions
 */
public class RobotLogger {
    private static RobotLogger instance = null;
    private Notifier notifier;
    ArrayList<String> periodic_buffer = new ArrayList<String>();

    /**
     * Log level
     * 
     * The kRobot level will immediately push to the console, everything else is
     * queued until the next notifier cycle
     */
    public enum Level {
        kRobot, kInfo, kWarning, kLibrary
    }

    private RobotLogger() {
        this.notifier = new Notifier(this::pushLogs);

        // Build local log path
        File f = new File(FileUtils.getHome() + "rrlogs");
        if (!f.exists()) {
            f.mkdir();
        }
    }

    /**
     * Start the periodic logger
     * 
     * @param period The logging notifier period time in seconds
     */
    public void start(double period) {
        this.notifier.startPeriodic(period);
    }

    /**
     * Get a RobotLogger instance
     * 
     * @return The current RobotLogger
     */
    public static RobotLogger getInstance() {
        if (instance == null) {
            instance = new RobotLogger();
        }
        return instance;
    }

    /**
     * Log a message to netconsole with the INFO log level. This message will not be
     * logged immediately, but instead through the notifier.
     * 
     * @param msg The message to log
     */
    public void log(String msg) {
        this.log(msg, Level.kInfo);
    }

    public void log(String msg, Level log_level) {
        log("", msg, log_level);
    }

    public void log(String component, String msg) {
        log(component, msg, Level.kInfo);
    }

    /**
     * Logs a message to netconsole with a custom log level. The kRobot level will
     * immediately push to the console, everything else is queued until the next
     * notifier cycle
     * 
     * @param msg       The message to log
     * @param log_level the Level to log the message at
     */
    public void log(String component, String msg, Level log_level) {
        String display_string = toString(component, msg, log_level);

        // If the log level is kRobot, just print to netconsole, then return
        if (log_level == Level.kRobot) {
            System.out.println(display_string);
            return;
        }

        // Add log to the periodic_buffer
        this.periodic_buffer.add(display_string);

    }

    /**
     * Push all queued messages to netconsole, the clear the buffer
     */
    private void pushLogs() {

        try {
            for (String x : this.periodic_buffer) {
                System.out.println(x);
            }
            periodic_buffer.clear();
        } catch (Exception e) {
            System.out.println("Tried to push concurrently");
        }

    }

    /**
     * Convert a message and Level to a string
     * 
     * @param msg       The message
     * @param log_level The Level to log at
     * 
     * @return The formatted output string
     */
    private String toString(String component, String msg, Level log_level) {
        String level_str;

        // Turn enum level into string
        switch (log_level) {
        case kInfo:
            // level_str = "INFO: ";
            level_str = "";
            break;
        case kWarning:
            level_str = "WARNING: ";
            break;
        case kRobot:
            level_str = "ROBOT: ";
            break;
        case kLibrary:
            level_str = "LIBRARY: ";
            break;
        default:
            level_str = "";
            break;
        }

        return String.format("%s%s %s", level_str, (component.equals("")) ? component : "[" + component + "]", msg);
    }

}