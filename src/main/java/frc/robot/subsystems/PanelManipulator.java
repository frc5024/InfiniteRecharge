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

public class PanelManipulator extends SubsystemBase {
    RobotLogger logger = RobotLogger.getInstance();
    private static PanelManipulator s_instance = null;

    /**
     * Color sensor interface
     */
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

        /* Update the threshold from Shuffleboard */

        // Check the threshold value, and compare to the last
        double thresh = m_threshold.getDouble(RobotConstants.PanelManipulator.DEFAULT_COLOR_THRESHOLD);

        if (thresh != m_lastThreshold) {

            // Write to preferences
            Preferences.getInstance().putDouble("Color threshold", thresh);

            // Set the last threshold
            m_lastThreshold = thresh;
        }

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
        return getColor().equals(wantedColor);
    }

    /**
     * Get the field color seen by the sensor (black if none)
     * 
     * @return Sensed color (corrected to match field colors)
     */
    public Color getColor() {

        double threshold = m_threshold.getDouble(RobotConstants.PanelManipulator.DEFAULT_COLOR_THRESHOLD);

        // Red
        if (m_colorSensor.isReadingEqual(this.red, threshold)) {
            return new Color(this.red);
        }

        // Yellow
        if (m_colorSensor.isReadingEqual(this.yellow, threshold)) {
            return new Color(this.yellow);
        }

        // Green
        if (m_colorSensor.isReadingEqual(this.green, threshold)) {
            return new Color(this.green);
        }

        // Blue
        if (m_colorSensor.isReadingEqual(this.blue, threshold)) {
            return new Color(this.blue);
        }

        return Color.kBlack;

    }

    /**
     * Get the exact sensed color from the sensor
     * 
     * @return Sensed raw color
     */
    public Color getSensedColor() {
        return m_colorSensor.getColor();
    }

    /**
     * Update the telemetry data
     */
    public void updateTelemetry() {

        // Read sensor info
        Color rawColor = getSensedColor();

        // Publish sensor info*
        SmartDashboard.putNumber("Raw Red", rawColor.red);
        SmartDashboard.putNumber("Raw Green", rawColor.green);
        SmartDashboard.putNumber("Raw Blue", rawColor.blue);

        // Log which color is being sensed.
        Color reading = getColor();

        SmartDashboard.putString("Closest Color", getColorString(reading));
        SmartDashboard.putString("Offset Color", getColorString(offsetSensedColor(reading)));

    }

    /**
     * Offsets the color by the color that is at a 90 degree angle.
     * 
     * @param c Sensed color
     * @return Offset color
     */
    public Color offsetSensedColor(Color c) {

        if (c.equals(new Color(red))) {
            return new Color(blue);
        }

        if (c.equals(new Color(blue))) {
            return new Color(red);
        }

        if (c.equals(new Color(yellow))) {
            return new Color(green);
        }

        if (c.equals(new Color(green))) {
            return new Color(yellow);
        }

        return null;
    }

    /**
     * Convert a color to it's string
     * 
     * @param c Color
     * @return String of color name
     */
    public String getColorString(Color c) {

        if (c.equals(red)) {
            return "RED";
        }

        if (c.equals(yellow)) {
            return "YELLOW";
        }

        if (c.equals(green)) {
            return "GREEN";
        }

        if (c.equals(blue)) {
            return "BLUE";
        }

        return "NONE";
    }

    public double spinWheelTurns(int turns) {

        return 0.0;

    }

    public double spinWheelColors(int numberOfColorChanges) {

        return 0.0;

    }
}