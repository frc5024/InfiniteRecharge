package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import frc.lib5k.control.Toggle;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    private static OI s_instance = null;

    /**
     * Robot driver Xbox controller interface object
     */
    private XboxController m_driverController = new XboxController(RobotConstants.HumanInputs.DRIVER_CONTROLLER_ID);
    private XboxController m_operatorController = new XboxController(RobotConstants.HumanInputs.OPERATOR_CONTROLLER_ID);

    /* Toggles and modifiers */

    /**
     * Toggle for keeping track of drivetrain orientation
     */
    private Toggle m_driveTrainInvertToggle = new Toggle();

    /**
     * Toggle for keeping track of shooting
     */
    private Toggle m_shouldShootToggle = new Toggle();

    /**
     * Force the use of getInstance() by setting this class private
     */
    private OI() {
    }

    /**
     * Get the OI instance
     * 
     * @return OI instance
     */
    public static OI getInstance() {
        if (s_instance == null) {
            s_instance = new OI();
        }

        return s_instance;
    }

    /**
     * Get the robot "throttle" input
     * 
     * @return Throttle [-1.0-1.0]
     */
    public double getThrottle() {
        double speed = 0.0;

        // Determine the speed by subtracting the left trigger from the right. This will
        // effectively turn the left into "backwards speed" and the right into "forwards
        // speed"
        speed += m_driverController.getTriggerAxis(GenericHID.Hand.kRight);
        speed -= m_driverController.getTriggerAxis(GenericHID.Hand.kLeft);

        return speed;
    }

    /**
     * Get the robot "turn" input
     * 
     * @return Turn [-1.0-1.0]
     */
    public double getTurn() {
        return m_driverController.getX(GenericHID.Hand.kLeft);
    }

    /**
     * Check if the driver has enabled drivetrain inversion. This is a toggle
     * 
     * @return Should the drivetrain be inverted?
     */
    public boolean isDriveInverted() {

        // Feed X button press into a toggle. This will flip the toggle value every time
        // the button is pressed
        return m_driveTrainInvertToggle.feed(m_driverController.getXButtonPressed());
    }

    public boolean shouldShoot() {
        // TODO: replace this with an operator control
        return m_shouldShootToggle.feed(m_driverController.getAButtonPressed());
    }

    public boolean ejectClimber() {
        if (m_operatorController.getAButtonPressed()) {
            return true;
        } else {
            return false;
        }
    }

    public double retractClimber() {
        double speed = 0.0;
        
        // Use the left trigger to retract the climber
        speed -= m_operatorController.getTriggerAxis(GenericHID.Hand.kLeft);

        return speed;
    }
}
