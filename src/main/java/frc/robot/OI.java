package frc.robot;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
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

    /* Controllers */
    private XboxController m_driverController = new XboxController(RobotConstants.HumanInputs.DRIVER_CONTROLLER_ID);
    private XboxController m_operatorController = new XboxController(RobotConstants.HumanInputs.OPERATOR_CONTROLLER_ID);

    /* Toggles and modifiers */

    // Toggle for keeping track of drivetrain orientation
    private Toggle m_driveTrainInvertToggle = new Toggle();

    // Shooting action toggle
    private Toggle m_shouldShootToggle = new Toggle();

    // Intake action toggle
    private Toggle m_shouldIntakeToggle = new Toggle();

    // Panel Manipulator Toggle(s)
    private Toggle m_shouldPositionToggle = new Toggle();
    private Toggle m_shouldRotateToggle = new Toggle();
    // Unjam toggle
    private Toggle m_shouldUnjamToggle = new Toggle();

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
     * Send a rumble command to the driver controller
     * 
     * @param force Force from 0-2
     */
    public void rumbleDriver(double force) {
        m_driverController.setRumble((force > 1.0) ? RumbleType.kLeftRumble : RumbleType.kRightRumble,
                (force > 1.0) ? force - 1.0 : force);
    }

    /**
     * Send a rumble command to the operator controller
     * 
     * @param force Force from 0-2
     */
    public void rumbleOperator(double force) {
        m_operatorController.setRumble((force > 1.0) ? RumbleType.kLeftRumble : RumbleType.kRightRumble,
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

    /**
     * Get if the drivebase should switch to auto-aim mode
     * 
     * @return Should be auto-aiming?
     */
    public boolean shouldAutoAim() {
        return m_driverController.getYButton();
    }

    /**
     * Check if the robot should be shooting balls right now
     * 
     * @return Should be shooting?
     */
    public boolean shouldShoot() {
        return m_operatorController.getTriggerAxis(Hand.kRight) > 0.8;
    }

    public boolean shouldRotate() {
        return m_operatorController.getStickButton(Hand.kLeft);
    }


    public boolean shouldPosition() {
        return m_operatorController.getStickButton(Hand.kRight);
    }


    public boolean shouldKillPanel(){
        return m_operatorController.getBackButtonPressed();
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
        double direction = 0;

        direction = m_driverController.getTriggerAxis(Hand.kRight);

        // Dead zone for the climber
        if (Math.abs(direction) <= 0.1) direction = -1; // Up, Level
        if (Math.abs(direction) >= 0.1) direction = 1; // Down, Retracted

        if (direction == -1) {
            return Position.LEVEL;
        } else if (direction == 1 ){
            return Position.RETRACTED;
        } else {
            return Position.CURRENT;
        }
    }

    /**
     * Check if the robot should be intaking balls right now
     * 
     * @return Should intake
     */
    public boolean shouldIntake() {
        return m_shouldIntakeToggle.feed(m_operatorController.getBumperPressed(Hand.kRight));
    }

    /**
     * Reset the intake input toggle
     */
    public void resetIntakeInput() {
        m_shouldIntakeToggle.reset();
    }

    /**
     * Should the cell counter be reset?
     * 
     * @return Should reset
     */
    public boolean shouldResetCellCount() {
        return (m_operatorController.getPOV() == 90) && m_operatorController.getXButtonPressed();
    }

    public boolean shouldUnjam() {
        return m_shouldUnjamToggle.feed(m_operatorController.getBButtonPressed());
    }

    public void resetUnjamInput() {
        m_shouldUnjamToggle.reset();
    }

}
