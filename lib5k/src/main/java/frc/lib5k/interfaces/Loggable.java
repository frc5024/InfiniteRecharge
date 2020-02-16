package frc.lib5k.interfaces;

/**
 * Common interface for components that can be logged
 */
public interface Loggable {
    
    /**
     * Log component status 
     */
    public void logStatus();


    /**
     * Push telemetry data to NetworkTables
     */
    public void updateTelemetry();

}