package frc.lib5k.ui.intpus.controllers;

import java.util.HashMap;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import frc.lib5k.ui.intpus.sources.XboxButton;

public class XboxControllerFactory {

    // Mapping of currently allocated HID devices
    private static HashMap<Integer, XboxController> m_controllers = new HashMap<>();

    /**
     * Get a button object for a controller
     * 
     * @param hidID  Controller ID
     * @param button Button type
     * @return Button
     */
    public static XboxButton getButton(int hidID, Button button) {

        // Check if this is the first time seeing this controller
        if (!m_controllers.containsKey(hidID)) {
            m_controllers.put(hidID, new XboxController(hidID));
        }

        // Return the button mapping
        return new XboxButton(m_controllers.get(hidID), button);
    }
}