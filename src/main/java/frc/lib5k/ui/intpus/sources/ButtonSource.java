package frc.lib5k.ui.intpus.sources;

/**
 * Interface for user input buttons
 */
public interface ButtonSource {

    /**
     * Get the button's current state
     * 
     * @return True if button pressed
     */
    public boolean getCurrentState();

    /**
     * Get the button toggle state
     * 
     * @return Toggles between true and false between each press
     */
    public boolean getToggle();

    /**
     * Reset the button toggle
     */
    public void resetToggle();

    /**
     * Get if the button has just been pressed
     * 
     * @return Has the button been pressed
     */
    public boolean getPressed();

    /**
     * Get if the button has just been released
     * 
     * @return Has the button been released
     */
    public boolean getReleased();

}