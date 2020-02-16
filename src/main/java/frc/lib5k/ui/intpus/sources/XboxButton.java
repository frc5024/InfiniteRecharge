package frc.lib5k.ui.intpus.sources;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.XboxController.Button;
import frc.lib5k.control.Toggle;

/**
 * A Wrapper for an X-box controller button
 */
public class XboxButton implements ButtonSource {

    // Button toggle
    private Toggle m_toggle;

    // Controller
    private XboxController m_controller;

    // Button type
    private Button m_type;

    public XboxButton(XboxController controller, Button button) {
        m_toggle = new Toggle();
        m_type = button;
    }

    @Override
    public boolean getCurrentState() {
        return m_controller.getRawButton(m_type.value);
    }

    @Override
    public boolean getToggle() {
        return m_toggle.feed(getPressed());
    }

    @Override
    public void resetToggle() {
        m_toggle.reset();

    }

    @Override
    public boolean getPressed() {
        return m_controller.getRawButtonPressed(m_type.value);
    }

    @Override
    public boolean getReleased() {
        return m_controller.getRawButtonReleased(m_type.value);
    }

}