package frc.lib5k.loops.loopables;

import frc.lib5k.utils.RobotLogger;

/**
 * A replacement for WPIlib's Subsystem. 
 * Based off 254's io loops
 */
public abstract class LoopableSubsystem {
    protected RobotLogger logger = RobotLogger.getInstance();

    public String name = "Unnamed Subsystem";
    public double last_timestamp;

    public void periodicInput() {
    }

    public void periodicOutput() {

    }
    
    public abstract void outputTelemetry();
    
    public abstract void stop();

    public abstract void reset();
}