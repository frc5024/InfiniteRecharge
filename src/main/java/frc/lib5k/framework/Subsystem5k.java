package frc.lib5k.framework;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.logging.INetworkLoggable;
import frc.lib5k.logging.NamedSendable;

/**
 * Robot Subsystem framework component
 */
public abstract class Subsystem5k extends SubsystemBase {

    public abstract void periodic();

    /**
     * Register multiple child components
     * 
     * @param children Children
     */
    public void addChildren(Sendable... children) {
        for (Sendable child : children) {
            addChild(SendableRegistry.getName(child), child);
        }
    }

    /**
     * Register multiple child components
     * 
     * @param children Children
     */
    public void addChildren(NamedSendable... children) {
        for (NamedSendable child : children) {
            addChild(child.getName(), child.getComponent());
        }
    }

    /**
     * Register multiple child components
     * 
     * @param children Children
     */
    public void addChildren(INetworkLoggable... children) {
        addChildren(children);
    }
}