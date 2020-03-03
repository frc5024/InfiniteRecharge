package frc.lib5k.utils;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.RobotBase;

public class WinUtils {
    
    /**
     * Win the game. (It can't fail)
     * @param <T> RobotBase
     * @param r Robot class builder
     */
    public static <T extends RobotBase> void winGame(Supplier<T> r) {
        RobotBase.startRobot(r);
    }
}