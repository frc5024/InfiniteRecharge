package frc.lib5k.networking;

/**
 * Class for storing info about an internet port
 */
public class Port {

    /**
     * Networking protocol
     */
    public enum Protocol {
        UDP("udp"), TCP("tcp");

        private String name;

        Protocol(String name) {
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }

    // Locals
    int portNum;
    Protocol proto;

    public Port(String data) {
        String[] parts = data.split("/");

        // Handle invalid port data
        if (parts.length != 2) {
            return;
        }

        this.proto = (parts[0].equals("udp")) ? Protocol.UDP : Protocol.TCP;
        this.portNum = Integer.parseInt(parts[1]);
    }

    public Port(Protocol proto, int number) {
        this.proto = proto;
        this.portNum = number;
    }

    public int getNumber() {
        return portNum;
    }

    public Protocol getProtocol() {
        return proto;
    }

    public boolean equals(Port other) {
        return other.toString().equals(toString());
    }

    public String toString() {
        return String.format("%s/%d", proto.toString(), portNum);
    }
}