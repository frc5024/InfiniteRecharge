package frc.robot.subsystems;

import java.util.Map;

import com.revrobotics.ColorSensorV3;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.utils.ColorUtils;
import frc.robot.GameData;

public class PanelManipulator extends SubsystemBase {

    private static PanelManipulator s_instance = null;
    private boolean redSame, blueSame, greenSame, alphaSame;
    private boolean colorSame;

    private I2C.Port i2cPort = I2C.Port.kOnboard;

    private ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);
    private GameData gameData;
    private ShuffleboardTab panelTab;
    private NetworkTableEntry threashold;

    private PanelManipulator() {

        gameData = gameData.getInstance();
        panelTab = Shuffleboard.getTab("Panel Manipulator");
        threashold = panelTab.add("Color Threashold", 0).getEntry();
    }

    public static PanelManipulator getInstance() {
        if (s_instance == null) {
            s_instance = new PanelManipulator();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

               
        
        Color8Bit detectedColor = new Color8Bit(colorSensor.getColor());  
        

        SmartDashboard.putNumber("Red", detectedColor.red);
        SmartDashboard.putNumber("Green", detectedColor.green);
        SmartDashboard.putNumber("Blue", detectedColor.blue);

        SmartDashboard.putBoolean("Color Matches?", colorSame);
    }

}
