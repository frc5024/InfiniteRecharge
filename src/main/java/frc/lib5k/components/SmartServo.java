package frc.lib5k.components;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.Servo;
import frc.lib5k.utils.Mathutils;

public class SmartServo extends Servo {

    /* Simulation */
    private SimDevice m_device;
    private SimDouble m_angle;
    private SimDouble m_output;

    public SmartServo(int channel) {
        super(channel);

        

        // Set up simulation
        m_device = SimDevice.create("SmartServo");
        if (m_device != null) {
            m_angle = m_device.createDouble("Degrees", true, 0.0);
            m_output = m_device.createDouble("Output", true, 0.0);
        }
    }

    @Override
    public void set(double value) {

        // Set simulation val
        if (m_device != null) {
            value = Mathutils.clamp(value, 0.0, 1.0);
            m_output.set(value);
            m_angle.set(360 * value);
        }

        // Set hardware output
        super.set(value);
    }

    @Override
    public void setAngle(double degrees) {

        // Set simulation val
        if (m_device != null) {
            degrees = Mathutils.clamp(degrees, 0.0, 360.0);
            m_output.set(degrees / 180.0);
            m_angle.set(degrees);
        }

        // Set hardware output
        super.setAngle(degrees);
    }

}