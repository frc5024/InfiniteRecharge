package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import frc.lib5k.control.Toggle;
import edu.wpi.first.wpilibj.GenericHID;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    private static OI s_instance = null;

    /**
     * Robot drivers Xbox controller interface object
     */
    private XboxController m_driverController = new XboxController(RobotConstants.HumanInputs.DRIVER_CONTROLLER_ID);
    private XboxController m_operatorController = new XboxController(RobotConstants.HumanInputs.OPERATOR_CONTROLLER_ID);
    private XboxController m_testController = new XboxController(2);

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
     * Toggle for keeping track of running Intake auton
     */
    private Toggle m_shouldRunAutoIntakeToggle = new Toggle();

    /**
     * Toggle for keeping track of running Shoot auton
     */
    private Toggle m_shouldRunAutoShootToggle = new Toggle();

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
        return m_shouldShootToggle.feed(m_operatorController.getAButtonPressed());
    }

    public double getHopperBeltSpeed() {
        double speed = 0.0;

        speed += m_testController.getTriggerAxis(GenericHID.Hand.kRight);
        speed -= m_testController.getTriggerAxis(GenericHID.Hand.kLeft);
        
        speed = (Math.abs(speed) < 0.2 ? 0.0 : speed);

        return speed;
    }

    public double getHarversterRollerSpeed() {
        double speed = 0.0;

        speed = -m_testController.getY(GenericHID.Hand.kRight);
        
        speed = (Math.abs(speed) < 0.2 ? 0.0 : speed);

        return speed;
    }

    public double getHarversterArmSpeed() {
        double speed = 0.0;

        speed = -m_testController.getY(GenericHID.Hand.kLeft);
        
        speed = (Math.abs(speed) < 0.2 ? 0.0 : speed);

        return speed;
    }

    public boolean shouldRunAutoIntake() {
        return m_shouldRunAutoIntakeToggle.feed(m_testController.getBButton());
    }

    public boolean shouldRunAutoShoot() {
        return m_shouldRunAutoShootToggle.feed(m_testController.getYButton());
    }

    public boolean shouldAutoAim() {
        return m_driverController.getYButton();
    }

}
