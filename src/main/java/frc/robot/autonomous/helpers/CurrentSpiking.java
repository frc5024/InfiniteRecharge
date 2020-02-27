package frc.robot.autonomous.helpers;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.RobotConstants;

public class CurrentSpiking{

    PowerDistributionPanel powerDistributionPanel = new PowerDistributionPanel(RobotConstants.Power.PDP_CAN_ID);
    private int channel;

    public CurrentSpiking(int channel){
        this.channel = channel;
    }

    public double getChannelCurrent(int channel){ 
        return powerDistributionPanel.getCurrent(channel);
    }

    public void setSpike(){}




}