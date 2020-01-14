package frc.lib5k.networking.firewall;

import frc.lib5k.networking.Port;

public interface IFirewall {
    
    public boolean isValidPort(Port port);

    public String getName();
}