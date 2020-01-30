package frc.robot.subsystems;

import frc.lib5k.simulation.wrappers.SimTalon;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.ColorSensor5k;
import frc.lib5k.utils.ColorUtils;
import frc.lib5k.utils.RobotLogger;
import frc.robot.GameData;
import frc.robot.OI;
import frc.robot.RobotConstants;
import frc.robot.GameData.Stage;


public class PanelManipulator extends SubsystemBase {
    RobotLogger logger = RobotLogger.getInstance();
    private static PanelManipulator s_instance = null;

    /**
    * Physical Devices
    */
    private ColorSensor5k m_colorSensor = new ColorSensor5k(I2C.Port.kOnboard);
    private SimTalon m_spinnerMotor = new SimTalon(RobotConstants.PanelManipulator.SPINNER_MOTOR_ID);

    // Color matching class.
    private final ColorMatch m_colorMatcher = new ColorMatch();

    // Color Data
    private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);

    // States
    private ControlState currentState;
    private ControlState lastState;

    // SaveData vars;
    private Color currentColor;
    private Color lastColor;
    private boolean inRange;
    private boolean isUnlocked = OI.getInstance().unlockPanelManipulator();
    private boolean doneRotations;
    private boolean donePosition;
    private int colorCount;
    private int rotations;
    private double proximity;


    private PanelManipulator() {

        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kGreenTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
        m_colorMatcher.addColorMatch(kYellowTarget); 

        m_spinnerMotor.setNeutralMode(NeutralMode.Coast);
        m_spinnerMotor.enableVoltageCompensation(true);
        m_spinnerMotor.configOpenloopRamp(0.5);

    }

    /**
     * Get the PanelManipulator instance
     * 
     * @return Instance
     */
    public static PanelManipulator getInstance() {
        if (s_instance == null) {
            s_instance = new PanelManipulator();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

        proximity = m_colorSensor.getProximity();
        currentColor = m_colorMatcher.matchClosestColor(m_colorSensor.getColor()).color;
        inRange = (proximity > 200) ? true : false;

        if(isUnlocked) {
            setState(ControlState.INIT);
        } else {
            setState(ControlState.IDLE);
        }

        switch(currentState) {
            case INIT:
                
                checkRange();

            break;
            
            case ROTATING:

                checkRange();

                if(GameData.getInstance().getGameStage() == Stage.STAGE2) {

                    if(currentColor != lastColor) {
                        colorCount++;
                        currentColor = lastColor;
                    }
                    if(colorCount == 8) {
                        rotations++;
                        colorCount = 0;
                    }
                    if(rotations < 3) {
                        currentState = ControlState.IDLE;
                    }
                }
    
                // Stage 3 Positional Movement (to a specific color).
                if(GameData.getInstance().getGameStage() == Stage.STAGE3) {

                    m_spinnerMotor.set(0.5);
                    
                    if(ColorUtils.epsilonEquals(new Color(GameData.getInstance().getControlColor()), currentColor, 0.2)) {
                        currentState = ControlState.IDLE;
                    }
                    
                }
    
                break;

            case ERROR:

                m_spinnerMotor.stopMotor();
                break;

            case IDLE:

                m_spinnerMotor.stopMotor();
                break;
        }
        

    }

    public void checkRange() {
        if(inRange) {
            setState(ControlState.ROTATING);
            m_spinnerMotor.set(0.9);
        } else {
            setState(ControlState.IDLE);
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

        // if (match.color == offsetColor(wantColor)) {
        //     return true;
        // } else {
        //     return false;
        // }       

        return false;
    }


    public void updateTelemetry() {


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

    private void setState(ControlState state) {
        this.currentState = state;
    }

    private enum ControlState {

        INIT,
        ROTATING,
        ERROR,
        IDLE

    }
}