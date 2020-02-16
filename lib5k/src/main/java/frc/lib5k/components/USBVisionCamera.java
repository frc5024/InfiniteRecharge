package frc.lib5k.components;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Solenoid;
import frc.lib5k.roborio.FPGAClock;

public class USBVisionCamera extends AutoCamera {

    public enum LEDMode {
        ON, OFF, BLINK;
    }

    private Solenoid m_relay;
    private LEDMode m_desiredMode;
    private Notifier m_thread;
    private final double blink_ms = 25;

    public USBVisionCamera(String name, int usb_slot, int pcm_port) {
        this(name, usb_slot, 0, pcm_port);
    }

    public USBVisionCamera(String name, int usb_slot, int pcm_id, int pcm_port) {
        super(name, usb_slot);

        // Init the relay
        m_relay = new Solenoid(pcm_id, pcm_port);

        // Set the desired mode
        m_desiredMode = LEDMode.OFF;

        // Set the thread
        m_thread = new Notifier(this::update);
        m_thread.startPeriodic(0.04);

    }

    /**
     * Enable or disable the LEDRing around the camera
     * 
     * @param enabled Should the LEDRing be enabled?
     */
    public void setLED(boolean enabled) {

        // Set the LEDMode
        setLED((enabled) ? LEDMode.ON : LEDMode.OFF);
    }

    /**
     * Set the LEDRing mode
     * 
     * @param mode LEDRing mode
     */
    public void setLED(LEDMode mode) {
        m_desiredMode = mode;
    }

    private void update() {

        // Handle the LEDMode
        switch (m_desiredMode) {
        case ON:
            m_relay.set(true);
            break;
        case OFF:
            m_relay.set(false);
            break;
        case BLINK:
            boolean should_enable = FPGAClock.getMillisecondCycle(blink_ms);
            m_relay.set(should_enable);
            break;
        default:
            m_relay.set(false);

        }
    }
}