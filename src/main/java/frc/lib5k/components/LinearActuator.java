package frc.lib5k.components;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

/**
 * PCM-Powered Linear actuator
 */
public class LinearActuator implements Sendable {

    private Solenoid m_trigger;

    public enum ActuatorState {
        kDEPLOYED, // Actuator deployed
        kINACTIVE, // Actuator un-powered
    }

    /**
     * Create a Linear Actuator that is powered via a Pneumatic Control Module
     * 
     * @param pcmID      PCM CAN device ID
     * @param pcmChannel PCM device channel
     */
    public LinearActuator(int pcmID, int pcmChannel) {

        // Configure the trigger
        m_trigger = new Solenoid(pcmID, pcmChannel);

        // Replace solenoid sendable with actuator
        // SendableRegistry.remove(m_trigger);
        SendableRegistry.add(this, "LinearActuator", pcmChannel);

    }

    /**
     * Set the actuator state
     * 
     * @param state State
     */
    public void set(ActuatorState state) {
        // PCM is not designed to do this
        // clearAllFaults();

        // Send data
        switch (state) {
        case kDEPLOYED:
            m_trigger.set(true);
            break;
        default:
            m_trigger.set(false);
        }
    }

    /**
     * Get if the actuator is deployed
     * 
     * @return Is deployed?
     */
    public boolean isDeployed() {
        return m_trigger.get();
    }

    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("Solenoid");
        builder.setActuator(true);
        builder.setSafeState(() -> set(ActuatorState.kINACTIVE));
        builder.addBooleanProperty("Value", this::isDeployed, (t) -> {
            set((t) ? ActuatorState.kDEPLOYED : ActuatorState.kINACTIVE);
        });

    }

    public void clearAllFaults() {
        m_trigger.clearAllPCMStickyFaults();
    }
}