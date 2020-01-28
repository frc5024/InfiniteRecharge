package frc.robot.autonomous.actions.subsystemActions;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;


public class Shoot extends WaitCommand{

    public Shoot() {
        this(5);
    }
    
    public Shoot(int cellCount) {
        super(cellCount);
    }

}