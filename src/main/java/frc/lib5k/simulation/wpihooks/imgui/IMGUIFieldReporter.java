package frc.lib5k.simulation.wpihooks.imgui;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.geometry.Pose2d;

/**
 * A tool that hooks into HALSIM to report the robot's simulated position to
 * IMGUI
 */
public class IMGUIFieldReporter {

    private static IMGUIFieldReporter s_instance;

    /* Simulation devices to talk to HALSIM and IMGUI */
    private SimDevice m_device;
    private SimDouble m_x;
    private SimDouble m_y;
    private SimDouble m_theta;

    private IMGUIFieldReporter() {

        // Build a SimDevice
        m_device = SimDevice.create("Field2D");

        // Set up the simulation hooks
        if (m_device != null) {
            m_x = m_device.createDouble("x", false, 0.0);
            m_y = m_device.createDouble("y", false, 0.0);
            m_theta = m_device.createDouble("rot", false, 0.0);
        }

    }

    public static IMGUIFieldReporter getInstance() {
        if (s_instance == null) {
            s_instance = new IMGUIFieldReporter();
        }

        return s_instance;
    }

    /**
     * Check if this tool has successfully hooked into HALSIM
     * 
     * @return Has successfully hooked?
     */
    public boolean isHooked() {
        return m_device != null;
    }

    /**
     * Set the robot's position in IMGUI Simulation
     * 
     * @param p Robot position
     */
    public void reportRobotPosition(Pose2d p) {
        if (isHooked()) {

            // Translate the pose to adapt 5024's coordinate system to WPILib's
            double x = p.getTranslation().getX();
            double y = (p.getTranslation().getY() - 4.35) * -1;
            double theta = p.getRotation().getDegrees() * -1;

            // Publish values to IMGUI
            m_x.set(x);
            m_y.set(y);
            m_theta.set(theta);
        }

    }
}