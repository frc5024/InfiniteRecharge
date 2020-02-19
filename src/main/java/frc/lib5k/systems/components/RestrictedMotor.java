package frc.lib5k.systems.components;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SpeedController;
import frc.lib5k.components.sensors.interfaces.IBinarySensor;

/**
 * A controller for systems controlled by a motor with a range defined by two
 * sensors
 */
public class RestrictedMotor implements SpeedController {

    // Limits
    private Supplier<Boolean> m_lowLimit, m_highLimit;

    // Motor controller
    private SpeedController m_controller;

    /**
     * Create a RestrictedMotor
     * 
     * @param controller    Motor controller
     * @param negativeLimit Low limit sensor
     * @param positiveLimit High limit sensor
     */
    public RestrictedMotor(SpeedController controller, DigitalInput negativeLimit, DigitalInput positiveLimit) {
        m_controller = controller;
        m_lowLimit = negativeLimit::get;
        m_highLimit = positiveLimit::get;

    }

    /**
     * Create a RestrictedMotor
     * 
     * @param controller    Motor controller
     * @param negativeLimit Low limit sensor
     * @param positiveLimit High limit sensor
     */
    public RestrictedMotor(SpeedController controller, IBinarySensor negativeLimit, IBinarySensor positiveLimit) {
        m_controller = controller;
        m_lowLimit = negativeLimit::get;
        m_highLimit = positiveLimit::get;

    }

    /**
     * Create a RestrictedMotor
     * 
     * @param controller    Motor controller
     * @param negativeLimit Low limit sensor
     * @param positiveLimit High limit sensor
     */
    public RestrictedMotor(SpeedController controller, Supplier<Boolean> negativeLimit,
            Supplier<Boolean> positiveLimit) {
        m_controller = controller;
        m_lowLimit = negativeLimit;
        m_highLimit = positiveLimit;

    }

    @Override
    public void pidWrite(double output) {
        m_controller.pidWrite(output);

    }

    @Override
    public void set(double speed) {

        // Stop the motor if at limits
        speed = (speed < 0.0 && m_lowLimit.get()) ? 0 : speed;
        speed = (speed > 0.0 && m_highLimit.get()) ? 0 : speed;

        // Set the controller speed
        m_controller.set(speed);

    }

    @Override
    public double get() {
        return m_controller.get();
    }

    @Override
    public void setInverted(boolean isInverted) {
        m_controller.setInverted(isInverted);

    }

    @Override
    public boolean getInverted() {
        return m_controller.getInverted();
    }

    @Override
    public void disable() {
        m_controller.disable();

    }

    @Override
    public void stopMotor() {
        m_controller.stopMotor();

    }

}