package frc.lib5k.simulation.wrappers;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.RobotBase;
import frc.lib5k.roborio.RR_HAL;

/**
 * A simulation wrapper for the WPI_TalonSRX
 */
public class SimTalon extends WPI_TalonSRX {

    // private SimDevice m_simDevice;
    // private SimDouble m_simSpeed;

    public SimTalon(int deviceNumber) {
        super(deviceNumber);

        // handle simulation device settings
        // m_simDevice = SimDevice.create("SimTalon", getDeviceID());
        // if (m_simDevice != null) {
        // m_simSpeed = m_simDevice.createDouble("Speed", true, 0.0);
        // }
    }

    // @Override
    // public void set(double speed) {

    // // Set sim speed
    // if (m_simDevice != null) {
    // m_simSpeed.set(speed);
    // }

    // super.set(speed);
    // }

    // @Override
    // public double get() {
    // if (m_simDevice != null) {
    // return m_simSpeed.get();
    // }

    // return super.get();
    // }

    @Override
    public double getMotorOutputVoltage() {
        if (RobotBase.isSimulation()) {
            return get() * RR_HAL.getSimSafeVoltage();
        }

        return super.getMotorOutputVoltage();
    }

    @Override
    public void setVoltage(double outputVolts) {
        if (RobotBase.isSimulation()) {
            set(outputVolts / RR_HAL.getSimSafeVoltage());
        } else {
            super.setVoltage(outputVolts);
        }
    }
}