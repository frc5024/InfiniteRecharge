package frc.robot.subsystems;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color8Bit;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.ColorSensor5k;
import frc.lib5k.utils.ColorUtils;
import frc.lib5k.utils.RobotLogger;
import frc.robot.GameData;
import frc.robot.RobotConstants;

public class PanelManipulator extends SubsystemBase {
    RobotLogger logger = RobotLogger.getInstance();
    private static PanelManipulator s_instance = null;

    private ColorSensor5k m_colorSensor = new ColorSensor5k(I2C.Port.kOnboard);

    /* Color thresholding */
    private NetworkTableEntry m_threshold;
    
    private double m_lastThreshold = 0.0;

    private PanelManipulator() {


        // Find the stored color threshold value
        double storedThreshold = Preferences.getInstance().getDouble("Color threshold",
                RobotConstants.PanelManipulator.DEFAULT_COLOR_THRESHOLD);
        logger.log("PanelManipulator", String.format("Loaded color threshold: %.2f", storedThreshold));

        // Get the threshold value container
        m_threshold = Shuffleboard.getTab("Panel Manipulator").add("Color Threshold", storedThreshold)
                .withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 255)).getEntry();

    }

    public static PanelManipulator getInstance() {
        if (s_instance == null) {
            s_instance = new PanelManipulator();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

        // Check the threshold value, and compare to the last
        double thresh = m_threshold.getDouble(RobotConstants.PanelManipulator.DEFAULT_COLOR_THRESHOLD);

        if (thresh != m_lastThreshold) {

            // Write to preferences
            Preferences.getInstance().putDouble("Color threshold", thresh);

            // Set the last threshold
            m_lastThreshold = thresh;
        }

       

        // TODO: remove this after development
        outputTelemetry();

    }

    /**
     * z
     * @return a boolean that if the control color is the same as the sensed color, it will return true. else false.
     */
    public boolean isSensedColorCorrect() {

        // Read the color wanted by FMS
        Color8Bit wantedColor = GameData.getInstance().getControlColor();

        // Ensure a color has been requested
        if (wantedColor == null) {
            return false;
        }

        // Check equality
        return m_colorSensor.isReadingEqual(wantedColor,
                m_threshold.getDouble(RobotConstants.PanelManipulator.DEFAULT_COLOR_THRESHOLD));

    }

    public void outputTelemetry() {

        String color = "";

        // Read sensor info
        Color8Bit detectedColor = m_colorSensor.getSensedColor(); 

        // Publish sensor info*
        SmartDashboard.putNumber("Red", detectedColor.red);
        SmartDashboard.putNumber("Green", detectedColor.green);
        SmartDashboard.putNumber("Blue", detectedColor.blue);

        // Log which color is being sensed.
        if(ColorUtils.epsilonEquals(detectedColor, RobotConstants.PanelManipulator.redGameColor, m_threshold.getValue().getDouble()) ) {

            color = "RED";

        }

        if(ColorUtils.epsilonEquals(detectedColor, RobotConstants.PanelManipulator.blueGameColor, m_threshold.getValue().getDouble()) ) {


            color = "BLUE";

        }

        if(ColorUtils.epsilonEquals(detectedColor, RobotConstants.PanelManipulator.greenGameColor, m_threshold.getValue().getDouble()) ) {

            color = "GREEN";
        }

        if(ColorUtils.epsilonEquals(detectedColor, RobotConstants.PanelManipulator.yellowGameColor, m_threshold.getValue().getDouble()) ) {
            
            color = "YELLOW";
 
        }

        SmartDashboard.putString("Color:", color);
         
    }

    /**
     * Offsets the color by the color that is at a 90 degree angle.
     * @param sensedColor
     * @return 
     */
    public Color8Bit offsetSensedColor(Color8Bit sensedColor) {

        if(sensedColor == RobotConstants.PanelManipulator.redGameColor) {
            return RobotConstants.PanelManipulator.blueGameColor;
        }

        if(sensedColor == RobotConstants.PanelManipulator.blueGameColor) {
            return RobotConstants.PanelManipulator.redGameColor; 
        }

        if(sensedColor == RobotConstants.PanelManipulator.yellowGameColor) {
            return RobotConstants.PanelManipulator.greenGameColor;
        }

        if(sensedColor == RobotConstants.PanelManipulator.greenGameColor) {
            return RobotConstants.PanelManipulator.yellowGameColor;
        }

        return null;
    }

    /**
     * Stats for moving the Control Panel.
     * 
     */
    private enum SpinnerStates {
        IDLE,
        START,
        ROTATION,
        POSITION,
        STOP,
        ERROR
    }
}