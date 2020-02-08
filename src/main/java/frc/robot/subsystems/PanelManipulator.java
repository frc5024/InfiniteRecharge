package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.ColorSensor5k;
import frc.lib5k.simulation.wrappers.SimTalon;
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
    private double colorCount;

    private boolean inRange;
    private boolean isRUnlocked, isPUnlocked;
    private boolean isRotated, isPositioned;

    // Color Saves
    private Color detectedColor;
    private PanelColors currentColor, nextColor;


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
        if(OI.getInstance().unlockPanelManipulator()) {
            isPUnlocked = true;
        }

        // Proximity detection. 
        proximity = m_colorSensor.getProximity();

        // If proximity is greater than 200, than its false.
        inRange = (proximity > 200) ? true : false;

        logger.log(proximity + "", "proximity");

        // Whatever color is under the sensor at the current cycle.
        detectedColor = m_colorMatcher.matchClosestColor(m_colorSensor.getColor()).color;

        

        switch(state) {

            // If the Manipulator is not doing anything, it should be in this state.
            case IDLE:

                if(inRange && isPUnlocked) {

                    setState(ControlState.POSITIONAL);


                }

                if(inRange && isRUnlocked) {

                    setState(ControlState.ROTATING);

                }

                if(isPositioned && isRotated) {
                        
                    setState(ControlState.IDLE);
                    return;

                }

                break;

            
            // Rotates the panel 3 times. Detecting next colors instead of counting one when a color changes.
            case ROTATING:

                if(!inRange || !isRUnlocked) {
                    setState(ControlState.ERROR);
                }

                // Sets the motor speed. 
                m_spinnerMotor.set(OI.getInstance().getPanelThrottle());

                if(currentColor == nextColor) {
                    colorCount++;
                }

                // Changing the current color and the expected next color.
                if(detectedColor == kBlueTarget) {
                    currentColor = PanelColors.BLUE;
                    nextColor = PanelColors.GREEN;
                }

                if(detectedColor == kGreenTarget) {
                    currentColor = PanelColors.GREEN;
                    nextColor = PanelColors.RED;
                }

                if(detectedColor == kRedTarget) {
                    currentColor = PanelColors.RED;
                    nextColor = PanelColors.YELLOW;
                }

                if(detectedColor == kYellowTarget) {
                    currentColor = PanelColors.YELLOW;
                    nextColor = PanelColors.BLUE;
                }

                if(colorCount >= 28) {
                    OI.getInstance().rumbleOperator(0.4);
                    isRotated = true;
                }

                break;

                // Spins until the current color is under the sensor.
            case POSITIONAL:


                if(!isPUnlocked || !inRange) {
                    setState(ControlState.ERROR);
                }

                m_spinnerMotor.set(OI.getInstance().getPanelThrottle());

                if(isSensedColorCorrect()) {
                    m_spinnerMotor.stopMotor();
                    isPositioned = true;
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

        if(match.color == null) {
            return false;
        }
        if(match.color == controlColor) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * State setter
     * @param state
     */
    private void setState(ControlState state) {
        this.state = state;
    }

    /**
     * Control State Enum
     * Contains the 
     */
    private enum ControlState {

        IDLE,
        ROTATING,
        POSITIONAL,
        ERROR


    }

    private enum PanelColors {

        RED,
        BLUE,
        YELLOW,
        GREEN


    }
}