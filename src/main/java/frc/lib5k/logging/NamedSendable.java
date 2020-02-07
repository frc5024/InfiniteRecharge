package frc.lib5k.logging;

import edu.wpi.first.wpilibj.Sendable;

/**
 * A container for a sendable and a component name
 */
public class NamedSendable {

    // Data
    private String name;
    private Sendable sendable;

    public NamedSendable(String name, Sendable component) {
        this.name = name;
        this.sendable = component;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sendable getComponent() {
        return sendable;
    }
}