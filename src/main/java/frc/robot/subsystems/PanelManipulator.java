package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.ColorSensor5k;
import frc.lib5k.utils.RobotLogger;
import frc.robot.GameData;
import frc.robot.RobotConstants;

public class PanelManipulator extends SubsystemBase {
    RobotLogger logger = RobotLogger.getInstance();
    private static PanelManipulator s_instance = null;

    // Physical devices
    private ColorSensor5k m_colorSensor = new ColorSensor5k(I2C.Port.kOnboard);
    private WPI_TalonSRX m_spinnerMotor = new WPI_TalonSRX(RobotConstants.PanelManipulator.SPINNER_MOTOR_ID);

    // Color matching class.
    private final ColorMatch m_colorMatcher = new ColorMatch();

    // Colors taken from the example code. 
    private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
  
    // Boolean for detecting correct colors.
    private boolean isCorrectColor;    

    private PanelManipulator() {

        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kGreenTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
        m_colorMatcher.addColorMatch(kYellowTarget); 

    }

    public static PanelManipulator getInstance() {
        if (s_instance == null) {
            s_instance = new PanelManipulator();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

        double prox = m_colorSensor.getProx();
        SmartDashboard.putNumber("Proximity:", prox);

        if(prox > 160) {
            isCorrectColor = isSensedColorCorrect();
            outputTelemetry();
        } else {
            isCorrectColor = false;
        }

    }

    /**
     * Gets the game color for that match.
     * Checks the detected color. If they match, they should return true.
     * If anything else, return false.
     * 
     * @return 
     */
    public boolean isSensedColorCorrect() {

        // Read the color wanted by FMS
        Color8Bit wantedColor = GameData.getInstance().getControlColor();
        Color wantColor;
        Color detectedColor = m_colorSensor.getColor();
        ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

        if(wantedColor == null) {
            return false;
        } else {
            wantColor = new Color(wantedColor);
        }

        if (match.color == offsetColor(wantColor)) {
            return true;
        } else {
            return false;
        }       

    }

    public void outputTelemetry() {


        Color detectedColor = m_colorSensor.getColor();
        String colorString;
        ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

        if (match.color == kBlueTarget) {
            colorString = "Blue";
          } else if (match.color == kRedTarget) {
            colorString = "Red";
          } else if (match.color == kGreenTarget) {
            colorString = "Green";
          } else if (match.color == kYellowTarget) {
            colorString = "Yellow";
          } else {
            colorString = "Unknown";
          }

        SmartDashboard.putNumber("Red", detectedColor.red);
        SmartDashboard.putNumber("Green", detectedColor.green);
        SmartDashboard.putNumber("Blue", detectedColor.blue);
        SmartDashboard.putNumber("Confidence", match.confidence);
        SmartDashboard.putString("Detected Color", colorString);
                
    }

    /**
     * Offsets the color by the color that is at a 90 degree angle.
     * @param sensedColor
     * @return 
     */
    public Color offsetColor(Color color) {

        if(color == kBlueTarget) {
            return kRedTarget;
        }
        if(color == kRedTarget) {
            return kBlueTarget;
        }
        if(color == kGreenTarget) {
            return kYellowTarget;
        }
        if(color == kYellowTarget) {
            return kGreenTarget;
        }

        return null;
    }

    /**
     * Stats for moving the Control Panel.
     * 
     */
    private enum SPINNER {
        IDLE,
        START,
        ROTATION,
        POSITION,
        STOP,
        ERROR
    }
}