package frc.lib5k.networking;

import java.util.ArrayList;

import frc.lib5k.networking.exceptions.PortException;
import frc.lib5k.networking.exceptions.PortInUseException;
import frc.lib5k.networking.exceptions.PortNotAllowedException;
import frc.lib5k.networking.firewall.FMS2014Firewall;
import frc.lib5k.networking.firewall.IFirewall;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;

public class PortManager {
    private static PortManager m_instance = null;

    // List of all allocated ports
    private ArrayList<Port> m_allocatedPorts = new ArrayList<>();

    // Firewall rules to obey
    // By default, use the FRC 2014 rules, but allow this to be changed
    private IFirewall m_firewall = new FMS2014Firewall();

    public static PortManager getInstance() {
        if (m_instance == null) {
            m_instance = new PortManager();
        }

        return m_instance;
    }

    /**
     * Set a new firewall to obey. This will not clear existing allocations
     * 
     * @param firewall New firewall rules
     */
    public void setFirewall(IFirewall firewall) {
        this.m_firewall = firewall;
    }

    /**
     * Allocate a network port for use, while obeying firewall rules
     * 
     * @param port Network port
     * @throws PortException
     */
    public void allocatePort(Port port) throws PortException {
        // Handle previous allocation
        if (isPortAllocated(port)) {
            throw new PortInUseException(port);
        }

        // Ensure port is allowed by the firewall
        if (!m_firewall.isValidPort(port)) {
            throw new PortNotAllowedException(port);
        }

        // Add port to allocations
        m_allocatedPorts.add(port);

    }

    /**
     * Remove a port from the allocation
     * 
     * @param port Port to remove
     */
    public void deallocatePort(Port port) {
        m_allocatedPorts.remove(port);
    }

    /**
     * Check if a port is already allocated
     * 
     * @param port Port to check
     * @return Has it been allocated?
     */
    public boolean isPortAllocated(Port port) {

        // iter each port and check if it is taken
        for (Port p : m_allocatedPorts) {
            if (port.equals(p)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if the specified network port can be allocated
     * 
     * @param port
     * @return
     */
    public boolean canAllocate(Port port) {
        return !isPortAllocated(port) && m_firewall.isValidPort(port);
    }

    /**
     * Allocate all FRC-related ports automatically
     */
    public void allocateFRCPorts() {

        // A list of these ports can be found in the FMS Whitepaper
        try {
            // SmartDashboard
            allocatePort(new Port("tcp/1735"));

            // DriverStation
            allocatePort(new Port("udp/1130"));
            allocatePort(new Port("udp/1140"));

        } catch (PortException e) {
            RobotLogger.getInstance().log("PortManager", "Failed to allocate FRC network ports", Level.kLibrary);
        }

    }
}