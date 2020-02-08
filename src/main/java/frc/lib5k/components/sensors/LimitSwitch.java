package frc.lib5k.components.sensors;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import frc.lib5k.components.sensors.interfaces.IBinarySensor;

public class LimitSwitch extends DigitalInput implements IBinarySensor {

    /**
     * Create a digital Limit Switch sensor object
     * 
     * @param channel DigitalIO channel
     */
    public LimitSwitch(int channel) {
        super(channel);

        // Set the Sendable name
        SendableRegistry.setName(this, "LimitSwitch", channel);
    }

    @Override
    public boolean get() {
        return !super.get();
    }

}