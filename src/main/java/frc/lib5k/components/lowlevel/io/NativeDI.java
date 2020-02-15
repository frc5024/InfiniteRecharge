package frc.lib5k.components.lowlevel.io;

import edu.wpi.first.hal.DIOJNI;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.InterruptJNI;
import edu.wpi.first.wpilibj.SensorUtil;

/**
 * An experimental class for interacting with instant FPGA sensor data
 */
public class NativeDI implements AutoCloseable {

    private int m_handle, m_interrupt;

    public NativeDI(int channel) {

        // Ensure we have a valid channel
        SensorUtil.checkDigitalChannel(channel);

        // Create an FPGA handle
        m_handle = DIOJNI.initializeDIOPort(HAL.getPort((byte) channel), true);

    }

    @Override
    public void close() {

        // Cancel all interrupts
        if (m_interrupt != 0) {
            InterruptJNI.cleanInterrupts(m_interrupt);
            m_interrupt = 0;
        }

        // Free the DIO port lock
        DIOJNI.freeDIOPort(m_handle);
        m_handle = 0;
    }

    public boolean getTriggered() {
        return DIOJNI.getDIO(m_handle);
    }
}