package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color8Bit;


public class GameData {
    private static GameData s_instance = null;

    /**
     * GameData constructor
     *
     */
    private GameData() {

    }

    /**
     * Get the GameData instance
     *
     * @return GameData instance
     */
    public static GameData getInstance(){
        if (s_instance == null) {
            s_instance = new GameData();
        }

        return s_instance;
    }


    /**
     *
     * @return one of the three enums in Stages to know which stage we are in.
     */
    public Stage getGameStage() {

        if(DriverStation.getInstance().isAutonomous()) {
            return Stage.STAGE1;
        }

        if( DriverStation.getInstance().isOperatorControl() && getControlColor() == null) {
            return Stage.STAGE2;
        }

        if( DriverStation.getInstance().isOperatorControl() && getControlColor() != null) {
            return Stage.STAGE3;
        }

        return null;
    }


    /**
     *
     * @return a Color object
     */
    public Color8Bit getControlColor() {

        switch (DriverStation.getInstance().getGameSpecificMessage()) {
            case "R":
                return new Color8Bit(255, 0, 0);
            case "G":
                return new Color8Bit(0, 255, 0);
            case "B":
                return new Color8Bit(0, 255, 255);
            case "Y":
                return new Color8Bit(255, 255, 0);
            default:
                return null;
        }
    }

    private enum Stage {
        STAGE1,
        STAGE2,
        STAGE3
    }
}
