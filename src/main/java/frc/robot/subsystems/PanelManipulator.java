package frc.robot.subsystems;

import java.util.Map;

import com.revrobotics.ColorMatch;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.TalonHelper;
import frc.lib5k.components.sensors.ColorSensor5k;
import frc.lib5k.simulation.wrappers.SimTalon;
import frc.lib5k.utils.Mathutils;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.robot.RobotConstants;

/**
 * Subsystem in charge of interacting with the control panel
 */
public class PanelManipulator extends SubsystemBase {
    RobotLogger logger = RobotLogger.getInstance();
    private static PanelManipulator s_instance = null;

    // Color sensor
    private ColorSensor5k m_colorSensor;

    // Spinner motor
    private SimTalon m_spinner;

    // Threshold tracker
    private NetworkTableEntry m_threshold;

    // Color matching
    private ColorMatch m_matcher;

    // Possible field colors
    private enum FieldColors {

        BLUE(ColorMatch.makeColor(0.143, 0.427, 0.429)), GREEN(ColorMatch.makeColor(0.197, 0.561, 0.240)),
        RED(ColorMatch.makeColor(0.561, 0.232, 0.114)), YELLOW(ColorMatch.makeColor(0.361, 0.524, 0.113));

        public Color c;

        FieldColors(Color c) {
            this.c = c;
        }

        /**
         * Get a FieldColor from an FMS color string
         * 
         * @param color FMS color
         * @return FieldColor object
         */
        public static FieldColors fromFMSString(String color) {

            // Switch/case the FMS color string
            switch (color.toUpperCase()) {
            case "R":
                return RED;
            case "G":
                return GREEN;
            case "B":
                return BLUE;
            case "Y":
                return YELLOW;
            default:
                return null;
            }
        }
    }

    // Listing of possible field colors in correct order
    private FieldColors[] m_colors = new FieldColors[] { FieldColors.RED, FieldColors.GREEN, FieldColors.BLUE,
            FieldColors.GREEN };

    // Possible system states
    private enum SystemState {
        IDLE, // System idle
        AWAIT_ROTATIONAL, // Wait for the manipulator to be in place for rotation control
        ROTATIONAL, // System rotation control
        AWAIT_POSITONAL, // Wait for the manipulator to be in place for position control
        POSITIONAL, // System position control
    }

    // Tracker for last & current state
    private SystemState m_currentState = SystemState.IDLE;

    // Use something different to force an update message
    private SystemState m_lastState = SystemState.POSITIONAL;

    // Tracker for the number of colors to move
    private int m_desiredColorOffset = 0;

    // Tracker for the robot losing contact with the panel mid-action
    private boolean m_hasLostContact = false;

    private PanelManipulator() {

        // Find the stored color threshold value
        double storedThreshold = Preferences.getInstance().getDouble("Color threshold",
                RobotConstants.PanelManipulator.DEFAULT_COLOR_THRESHOLD);
        logger.log("PanelManipulator", String.format("Loaded color threshold: %.2f", storedThreshold));

        // Get the threshold value container
        m_threshold = Shuffleboard.getTab("Panel Manipulator").add("Color Threshold", storedThreshold)
                .withWidget(BuiltInWidgets.kNumberSlider).withProperties(Map.of("min", 0, "max", 255)).getEntry();

        // Connect to Color Sensor
        m_colorSensor = new ColorSensor5k(I2C.Port.kOnboard);

        // Build a color matcher
        m_matcher = new ColorMatch();

        // Create and configure the spinner motor
        m_spinner = new SimTalon(RobotConstants.PanelManipulator.MOTOR_ID);
        TalonHelper.configCurrentLimit(m_spinner, 34, 32, 30, 0);
        m_spinner.setInverted(false);

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
        double threshold = m_threshold.getDouble(RobotConstants.PanelManipulator.DEFAULT_COLOR_THRESHOLD);

        // Set the matcher threshold
        m_matcher.setConfidenceThreshold(threshold);

        // Check if new state
        boolean isNew = (m_currentState != m_lastState);
        m_lastState = m_currentState;

        // Handle system state
        switch (m_currentState) {
        case IDLE:
            handleIdle(isNew);
            break;
        case ROTATIONAL:
            handleRotation(isNew);
            break;
        case POSITIONAL:
            handlePosition(isNew);
            break;
        case AWAIT_POSITONAL:
        case AWAIT_ROTATIONAL:
            handleAwait(isNew);
            break;
        default:
            // Set state to idle
            m_currentState = SystemState.IDLE;
        }

    }

    /**
     * Handle system idle state
     * 
     * @param isNew Is this a new state?
     */
    private void handleIdle(boolean isNew) {
        if (isNew) {
            logger.log("PanelManipulator", "Became idle");

            // Stop the spinner motor
            m_spinner.set(0);
        }
    }

    /**
     * Handle system waiting for contact with panel
     * 
     * @param isNew Is this a new state?
     */
    private void handleAwait(boolean isNew) {
        if (isNew) {
            logger.log("PanelManipulator", "Waiting for contact with control panel");
        }

        // Skip if not in contact with panel
        if (!isTouchingPanel()) {
            return;
        }

        // Move to the correct state
        switch (m_currentState) {
        case AWAIT_POSITONAL:
            m_currentState = SystemState.POSITIONAL;
            break;
        case AWAIT_ROTATIONAL:
            m_currentState = SystemState.ROTATIONAL;
            break;
        default:
            m_currentState = SystemState.IDLE;
        }

    }

    /**
     * Handle system rotation control state
     * 
     * @param isNew Is this a new state?
     */
    private void handleRotation(boolean isNew) {
        if (isNew) {
            logger.log("PanelManipulator", "Starting rotational control");
        }

        // Check if we lost contact with the panel
        if (!isTouchingPanel()) {

            // Tell the system we lost contact with the panel
            m_hasLostContact = true;

            // Wait for the robot to come in contact with the panel again
            m_currentState = SystemState.AWAIT_ROTATIONAL;
        }

        // Handle movement to desired color offset
        handleMovementToColor();

        // Update the color counter
        updateColorCounter();

        // If we have reached the desired color, switch to idle
        if (m_desiredColorOffset == 0) {
            m_currentState = SystemState.IDLE;
        }
    }

    /**
     * Handle system position control state
     * 
     * @param isNew Is thie a new state?
     */
    private void handlePosition(boolean isNew) {
        if (isNew) {
            logger.log("PanelManipulator", "Starting positional control");

            // Read the current color index
            int currentIDX = getColorIdx();

            if (!m_hasLostContact) {
                // Determine real offset from estimated one
                int wrappedDistance = (m_desiredColorOffset - currentIDX) % 4;
                m_desiredColorOffset = (wrappedDistance < 3) ? wrappedDistance : -1;
            }

        }

        // Check if we lost contact with the panel
        if (!isTouchingPanel()) {

            // Tell the system we lost contact with the panel
            m_hasLostContact = true;

            // Wait for the robot to come in contact with the panel again
            m_currentState = SystemState.AWAIT_POSITONAL;
        }

        // Handle movement to desired color offset
        handleMovementToColor();

        // Update the color counter
        updateColorCounter();

        // If we have reached the desired color, switch to idle
        if (m_desiredColorOffset == 0) {
            m_currentState = SystemState.IDLE;
        }
    }

    /**
     * Handle spinner movement to the desired color offset
     */
    private void handleMovementToColor() {

        // All we have to do here is set the motor input to the clamped offset (yay for
        // math!)
        m_spinner.set(Mathutils.clamp(m_desiredColorOffset, RobotConstants.PanelManipulator.SPINNER_SPEED * -1,
                RobotConstants.PanelManipulator.SPINNER_SPEED));

    }

    private void updateColorCounter() {

    }

    /**
     * Get the currently sensed color as an index of m_colors (-1 for no color / out
     * of range)
     * 
     * @return Color array index
     */
    private int getColorIdx() {
        return 0;
    }

    /**
     * Get if the manipulator is currently touching the control panel
     * 
     * @return Is touching control panel?
     */
    public boolean isTouchingPanel() {
        return m_colorSensor.getProximity() > RobotConstants.PanelManipulator.DISTANCE_THRESHOLD;
    }

    /**
     * Check if the system is idle
     * 
     * @return Is system idle?
     */
    public boolean isIdle() {
        return m_currentState == SystemState.IDLE;
    }

    /**
     * Rotate panel by N rotations
     * 
     * @param relRotations Number of rotations for panel
     */
    public void rotateTo(double relRotations) {
        // Determine the number of color changes to watch for
        // Multiply rotations by 8 color changes
        m_desiredColorOffset = (int) (relRotations * 8);

        // Switch to rotation control
        logger.log("PanelManipulator",
                String.format("Requested %.1f rotations (%d colors)", relRotations, m_desiredColorOffset));
        m_currentState = SystemState.AWAIT_ROTATIONAL;

    }

    /**
     * Set the manipulator to move an FMS color under the field sensor
     * 
     * @param color FMS color string
     */
    public void goToColor(String color) {

        // Convert the FMS color string to an actual color
        FieldColors desiredColor = FieldColors.fromFMSString(color);

        // Ensure color is valid
        if (desiredColor == null) {
            logger.log("PanelManipulator", String.format("Requested color (%s) is invalid!", color), Level.kWarning);
            return;
        }

        // Get the Robot-oriented color array index (Field color should be under the FMS
        // sensor)
        // TODO: This may need to be addition not subtraction
        int robotIndx = (m_colors.length - 2) % 4;

        // Set the desired color count
        m_desiredColorOffset = robotIndx;

        // Set the system state
        m_currentState = SystemState.AWAIT_POSITONAL;
    }

}