package frc.lib5k.ui.intpus.actions;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.ui.intpus.sources.ButtonSource;

public class CommandToggle implements InputAction {

    // Button input
    private ButtonSource m_soucre;

    // Command to toggle
    private CommandBase m_command;

    /**
     * Create a CommandToggle that starts/stops a command when a button is pressed
     * 
     * @param source  Button
     * @param command Command
     */
    public CommandToggle(ButtonSource source, CommandBase command) {
        this.m_soucre = source;
        this.m_command = command;

    }

    @Override
    public void update() {

        // Check for a button press
        if (m_soucre.getPressed()) {

            // If the command is running, stop it
            if (m_command.isScheduled()) {
                m_command.cancel();
            } else {
                // Start the command otherwise
                m_command.schedule();
            }
        }

    }
}