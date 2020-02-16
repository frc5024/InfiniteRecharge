package frc.lib5k.networking.exceptions;

public class PortException extends Exception {
    public PortException() {
        this("Unknown");
    }

    public PortException(String info) {
        super("Network port exception: " + info);
    }
}