package frc.lib5k.simulation.wrappers;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.RobotBase;
import frc.lib5k.roborio.RR_HAL;

public class SimSparkMax extends CANSparkMax {

    public SimSparkMax(int deviceID, MotorType type) {
        super(deviceID, type);

        // handle simulation device settings
        m_simDevice = SimDevice.create("SimSparkMax", getDeviceId());
        if (m_simDevice != null) {
            m_simSpeed = m_simDevice.createDouble("Speed", true, 0.0);
        }
    }

    private SimDevice m_simDevice;
    private SimDouble m_simSpeed;

    @Override
    public void set(double speed) {

        // Set sim speed
        if (m_simDevice != null) {
            m_simSpeed.set(speed);
        }

        super.set(speed);
    }

    @Override
    public double get() {
        if (m_simDevice != null) {
            return m_simSpeed.get();
        }

        return super.get();
    }

    @Override
    public void setVoltage(double outputVolts) {
        if(RobotBase.isSimulation()){
            set(outputVolts / RR_HAL.getSimSafeVoltage());
        }else{
            super.setVoltage(outputVolts);
        }
    }
}