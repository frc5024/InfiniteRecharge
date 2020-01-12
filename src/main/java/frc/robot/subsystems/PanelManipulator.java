package frc.robot.subsystems;

import java.util.Map;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.util.Color8Bit;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.ColorSensor5k;
import frc.lib5k.utils.ColorUtils;
import frc.lib5k.utils.RobotLogger;
import frc.robot.GameData;
import frc.robot.RobotConstants;

/**
 * Robot subsystem in charge of manipulating the field's Control Panel
 */
public class PanelManipulator extends SubsystemBase {
    RobotLogger logger = RobotLogger.getInstance();
    private static PanelManipulator s_instance = null;

    private ColorSensor5k m_colorSensor = new ColorSensor5k(I2C.Port.kOnboard);

    /* Color thresholding */
    private NetworkTableEntry m_threshold;
    private Color8Bit red = new Color8Bit(255, 0, 0);
    private Color8Bit green = new Color8Bit(0, 255, 0);
    private Color8Bit blue = new Color8Bit(0, 255, 255);
    private Color8Bit yellow = new Color8Bit(255, 255, 0);
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
     * 
     * @return a boolean that if the control color is the same as the sensed color,
     *         it will return true. else false.
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

    public Color getSensedColor() {
        return m_colorSensor.getColor();
    }

    public void outputTelemetry() {

        String color = "";

        // Read sensor info
        Color detectedColor = getSensedColor();

        // Publish sensor info*
        SmartDashboard.putNumber("Red", detectedColor.red);
        SmartDashboard.putNumber("Green", detectedColor.green);
        SmartDashboard.putNumber("Blue", detectedColor.blue);

        // Log which color is being sensed.
        if (ColorUtils.epsilonEquals(getSensedColor(), new Color(red), m_threshold.getValue().getDouble())) {

            color = "RED";

        }

        if (ColorUtils.epsilonEquals(getSensedColor(), new Color(blue), m_threshold.getValue().getDouble())) {

            color = "BLUE";

        }

        if (ColorUtils.epsilonEquals(getSensedColor(), new Color(green), m_threshold.getValue().getDouble())) {

            color = "GREEN";
        }

        if (ColorUtils.epsilonEquals(getSensedColor(), new Color(yellow), m_threshold.getValue().getDouble())) {

            color = "YELLOW";

        }

        SmartDashboard.putString("Color:", color);
        SmartDashboard.putString("Offset Color", offsetColorString(color));

    }

    /**
     * Offsets the color by the color that is at a 90 degree angle.
     * 
     * @param sensedColor
     * @return
     */
    public Color offsetSensedColor(Color8Bit sensedColor) {
        Color c = new Color(sensedColor);

        if (c == new Color(red)) {
            return new Color(blue);
        }

        if (c == new Color(blue)) {
            return new Color(red);
        }

        if (c == new Color(yellow)) {
            return new Color(green);
        }

        if (c == new Color(green)) {
            return new Color(yellow);
        }

        return null;
    }

    public String offsetColorString(String colorString) {
        if (colorString == "RED") {
            return "BLUE";
        }
        if (colorString == "BLUE") {
            return "RED";
        }
        if (colorString == "YELLOW") {
            return "GREEN";
        }
        if (colorString == "GREEN") {
            return "YELLOW";
        }

        return "NOTHING";
    }

    public double spinWheelTurns(int turns) {

        return 0.0;

    }

    public double spinWheelColors(int numberOfColorChanges) {

        return 0.0;

    }
}