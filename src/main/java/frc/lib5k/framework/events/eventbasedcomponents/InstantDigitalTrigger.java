package frc.lib5k.framework.events.eventbasedcomponents;

import java.util.function.Consumer;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;
import frc.lib5k.framework.EventHandler;

public class InstantDigitalTrigger implements AutoCloseable {

    // Interface to HAL counter
    private Counter m_counter;

    // Callback
    private Consumer<Integer> m_callback;

    public InstantDigitalTrigger(Runnable callback, int digitalChannel) {
        this((x) -> {
            callback.run();
        }, digitalChannel);
    }

    public InstantDigitalTrigger(Consumer<Integer> callback, int digitalChannel) {
        this.m_callback = callback;

        // Define a digital input
        DigitalInput input = new DigitalInput(digitalChannel);

        // Configure a hardware counter
        m_counter = new Counter(input);

        // Make the EventHandler watch this trigger
        EventHandler.getInstance().addDigitalTrigger(this);

    }

    @Override
    public void close() {
        m_counter.close();

    }

    /**
     * Handle counter callbacks
     */
    public void handleCallback() {

        // Check for a detected count
        int count = m_counter.get();

        if (count > 0) {
            m_callback.accept(count);
        }

        // Reset the counter count
        m_counter.reset();

    }

}