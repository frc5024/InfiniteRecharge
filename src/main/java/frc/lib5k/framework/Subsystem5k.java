package frc.lib5k.framework;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class Subsystem5k extends SubsystemBase {

    @Override
    public abstract void periodic();

    public abstract void onRobotEnable();

    public abstract void onRobotDisable();

}