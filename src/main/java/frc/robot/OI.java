package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import frc.lib5k.control.Toggle;
import frc.robot.subsystems.Climber.Position;
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

    public void rumbleDriver(double force) {
        m_driverController.setRumble((force > 1.0) ? RumbleType.kLeftRumble : RumbleType.kRightRumble,
                (force > 1.0) ? force - 1.0 : force);
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

    /**
     * Check if the climber should be ejected
     */
    public boolean shouldEjectClimber() {
        return m_operatorController.getBackButton() && m_operatorController.getStartButtonPressed();
    }

    public boolean shouldCancelClimb() {
        return m_operatorController.getStartButtonPressed() && !m_operatorController.getBackButton();
    }

    public Position getWantedClimbPosition() {
        if (m_operatorController.getPOV() == 0) {
            return Position.LEVEL;
        } else if (m_operatorController.getPOV() == 180) {
            return Position.RETRACTED;
        } else {
            return Position.CURRENT;
        }
    }

    public boolean shouldAutoAim() {
        return m_driverController.getYButton();
    }

}
