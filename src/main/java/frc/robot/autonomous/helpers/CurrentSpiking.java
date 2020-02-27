package frc.robot.autonomous.helpers;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.RobotConstants;

public class CurrentSpiking{

    PowerDistributionPanel powerDistributionPanel = new PowerDistributionPanel(RobotConstants.Power.PDP_CAN_ID);
    private int channel;
    private double targetSpike;

    public CurrentSpiking(int channel, double targetSpike){
        this.channel = channel;
        this.targetSpike = targetSpike;
    }

    public double getChannelCurrent(int channel){ 
        return powerDistributionPanel.getCurrent(channel);
    }

    public boolean hitTargetSpike(){
        if(targetSpike >= getChannelCurrent(channel)){
            return true;
        }

        return false;
    }




}