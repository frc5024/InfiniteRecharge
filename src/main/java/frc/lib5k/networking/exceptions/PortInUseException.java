package frc.lib5k.networking.exceptions;

import frc.lib5k.networking.Port;

public class PortInUseException extends PortException {
    public PortInUseException(Port port) {
        super("Port " + port.toString() + " already allocated");
    }
}