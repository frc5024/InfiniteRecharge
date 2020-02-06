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
import edu.wpi.first.wpiutil.CircularBuffer;
import frc.lib5k.components.ColorSensor5k;
import frc.lib5k.utils.RobotLogger;
import frc.robot.GameData;
import frc.robot.OI;
import frc.robot.RobotConstants;

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
    private ControlState state = ControlState.IDLE;

    // SaveData vars;
    private double proximity;
    private boolean inRange;
    private boolean isUnlocked;
    private double colorCount;
    private double rotations;
    private boolean doneRotations = false;


    // Color Saves
    private Color currentColor;
    private Color lastColor;
    private Color previousColor;

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

        // Unlocked the PanelManipulator.
        isUnlocked = OI.getInstance().unlockPanelManipulator();
        // Proximity detection. 
        proximity = m_colorSensor.getProximity();
        // If proximity is greater than 200, than its false.
        inRange = (proximity > 200) ? true : false;
        // Whatever color is under the sensor at the current cycle.
        currentColor = m_colorMatcher.matchClosestColor(m_colorSensor.getColor()).color;

        

        switch(state) {

            // If the Manipulator is not doing anything, it should be in this state.
            case IDLE:

                logger.log("[PanelManipulator]", "IDLE");

                if(doneRotations) {
                    setState(ControlState.POSITIONAL);
                }

                if(inRange && isUnlocked) {
                    setState(ControlState.ROTATING);
                }

                break;
            
            // Rotates the panel 3 times.
            case ROTATING:

                if(!inRange || !isUnlocked) {
                    setState(ControlState.ERROR);
                    break;
                }

                m_spinnerMotor.set(0.35);

                if(currentColor != lastColor) {

                }

                break;

                // Spins until the current color is under the sensor.
            case POSITIONAL:


                if(isUnlocked && inRange) {
                    setState(ControlState.ERROR);
                    break;
                }
                m_spinnerMotor.set(0.3);

                if(isSensedColorCorrect()) {
                    m_spinnerMotor.stopMotor();
                    setState(ControlState.IDLE);
                }
                
                break;
            
            // If something wrong happened, it should go tto this state. 
            case ERROR:
                
                m_spinnerMotor.set(0);
                logger.log("[PanelManipulator]", "Unlocked or Bumped During a Rotation!");
                setState(ControlState.IDLE);

                break;

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
        Color controlColor = new Color(GameData.getInstance().getControlColor());
        Color currentColor = m_colorSensor.getColor();

        ColorMatchResult match = m_colorMatcher.matchClosestColor(currentColor);

        if(match.color == controlColor) {

            return true;

        } else {

            return false;

        }
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
        SmartDashboard.putNumber("Color Count", colorCount);
        SmartDashboard.putNumber("Rotations", rotations);
                
    }

    /**
     * State setter
     * @param state
     */
    private void setState(ControlState state) {
        this.state = state;
    }

    /**
     * State getter
     * @return ControlState current state.
     */
    private ControlState getState() {
        return state;
    }

    private enum ControlState {

        IDLE,
        ROTATING,
        POSITIONAL,
        ERROR


    }
}