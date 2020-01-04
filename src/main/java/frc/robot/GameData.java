package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;

import java.awt.*;

public class GameData {

    DriverStation driverStation;

    public GameData() {
        driverStation = DriverStation.getInstance();

    }

    public Stage getGameStage() {

        if(driverStation.isAutonomous()) {
            return Stage.STAGE1;
        }

        if(!driverStation.isAutonomous() && getControlColor() == null) {
            return Stage.STAGE2;
        }

        if( !driverStation.isAutonomous() && getControlColor() != null) {
            return Stage.STAGE3;
        }

        return null;
    }

    public Color getControlColor() {

        switch (driverStation.getGameSpecificMessage()) {
            case "R":
                return Color.RED;
            case "G":
                return Color.GREEN;
            case "B":
                return Color.BLUE;
            case "Y":
                return Color.YELLOW;
            default:
                return null;
        }
    }

    // Stages within the game.
    private enum Stage {
        STAGE1,
        STAGE2,
        STAGE3
    }
}
