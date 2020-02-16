package frc.lib5k.networking.firewall;

import frc.lib5k.networking.Port;

public class FMS2014Firewall implements IFirewall {

    // Firewall name
    private final String name = "FRC 2014+ Firewall";

    /* Port ranges */
    private final int[][] udp_ranges = { { 1180, 1190 }, { 1130, 1130 }, { 1140, 1140 }, { 554, 554 }, { 5800, 5810 } };
    private final int[][] tcp_ranges = { { 1180, 1190 }, { 1735, 1735 }, { 80, 80 }, { 443, 443 }, { 554, 554 },
            { 5800, 5801 } };

    
    
    @Override
    /**
     * Check if a port is allowed by the firewall
     * 
     * @param port Port to check
     * @return is the port allowed?
     */
    public boolean isValidPort(Port port) {
        boolean isValid = false;

        // Check protocol
        switch (port.getProtocol()) {
        case UDP:
            for (int[] is : udp_ranges) {
                for (int i : is) {
                    if (i == port.getNumber()) {
                        isValid = true;
                        break;
                    }
                }
            }
            break;
        case TCP:
            for (int[] is : tcp_ranges) {
                for (int i : is) {
                    if (i == port.getNumber()) {
                        isValid = true;
                        break;
                    }
                }
            }
            break;
        default:
            isValid = false;

        }

        return isValid;
    }

    @Override
    public String getName() {
        return name;
    }

}