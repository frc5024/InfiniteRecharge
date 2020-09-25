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
    private double totalCurrent;

    public CurrentSpiking(int channel, double targetSpike){
        this.channel = channel;
        this.targetSpike = targetSpike;
    }

    public double getChannelCurrent(int channel){ 
        return powerDistributionPanel.getCurrent(channel);
    }

    public void setTargetSpike(double targetSpike){
        this.targetSpike = targetSpike;
    }

    public double getAverage(int... args){
        totalCurrent = 0;
        for(int arg : args){
            totalCurrent += getChannelCurrent(arg);
        }

        return totalCurrent / args.length;
    }
    

    public boolean hitTargetSpike(double seconds){
        if(getChannelCurrent(channel) >= targetSpike){
            if(isNew){
                timer.reset();
                timer.start();
                isNew = false;
            }

            if(timer.hasElapsed(seconds)){
                return true;
            }
        }else{
            isNew = true;
        }

        return false;
    }

    


}