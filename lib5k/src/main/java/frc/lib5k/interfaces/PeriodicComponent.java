package frc.lib5k.interfaces;

/**
 * Common interface for components that require periodic updates
 */
public interface PeriodicComponent {
    
    /**
     * Method to be called periodically. Usually in the main robot loop (once per 20ms)
     */
    public void update();
}