package frc.robot.autonomous.helpers;


import edu.wpi.first.wpilibj.PowerDistributionPanel;
import frc.robot.RobotConstants;
import edu.wpi.first.wpilibj.Timer;

public class CurrentSpiking{

    PowerDistributionPanel powerDistributionPanel = new PowerDistributionPanel(RobotConstants.Power.PDP_CAN_ID);
    private int channel;
    private double targetSpike;
    private Timer timer = new Timer();
    private boolean isNew = true;

    public CurrentSpiking(int channel, double targetSpike){
        this.channel = channel;
        this.targetSpike = targetSpike;
    }

    public double getChannelCurrent(int channel){ 
        return powerDistributionPanel.getCurrent(channel);
    }

    public boolean hitTargetSpike(double seconds){
        if(getChannelCurrent(channel) >= targetSpike){
            if(isNew){
                timer.reset();
                timer.start();
                isNew = false;
            }

            if(timer.hasElapsed(seconds)){
                System.out.println("Spike hit");
                return true;
            }
        }else{
            isNew = true;
        }

        return false;
    }




}