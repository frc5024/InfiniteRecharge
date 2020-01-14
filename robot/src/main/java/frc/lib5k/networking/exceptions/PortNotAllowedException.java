package frc.lib5k.networking.exceptions;

import frc.lib5k.networking.Port;

public class PortNotAllowedException extends PortException {
    public PortNotAllowedException(Port port) {
        super("Port " + port.toString() + " is not allowed by the firewall");
    }
}