package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.utils.RobotLogger;

/**
 * The DriveTrain handles all robot movement.
 */
public class DriveTrain extends SubsystemBase {
    private RobotLogger logger = RobotLogger.getInstance();
    private static DriveTrain s_instance = null;

    /**
     * Various drive modes
     */
    public enum DriveMode {
        OPEN_LOOP, // Open loop control (percent output control)
        VOLTAGE // Voltage control

    }

    // Keep track of the current DriveMode
    private DriveMode m_currentDriveMode = DriveMode.OPEN_LOOP;

    // Keep track of current DriveSignal
    // A DriveSignal keeps track of motor outputs for a drivebase
    private DriveSignal m_currentSignal = new DriveSignal(0, 0);

    /**
     * DriveTrain constructor.
     * 
     * All subsystem components should be created and configured here.
     */
    private DriveTrain() {

    }

    /**
     * Get the DriveTrain instance.
     * 
     * @return DriveTrain instance
     */
    public static DriveTrain getInstance() {
        if (s_instance == null) {
            s_instance = new DriveTrain();
        }

        return s_instance;
    }

    /**
     * Subsystem-specific tasks that must be run once per 20ms must be placed in
     * this method.
     */
    @Override
    public void periodic() {

        // Handle motor outputs for each mode
        switch (m_currentDriveMode) {
        case OPEN_LOOP:
            // Set Open loop outputs for motors
            // TODO: Set outputs here (reading from m_currentSignal)
            break;
        case VOLTAGE:
            // Set Voltage outputs for motors
            // TODO: Set outputs here (reading from m_currentSignal)
            break;
        default:
            // This code should never run, but if it does, we set the mode to OPEN_LOOP, and
            // the outputs to 0
            setOpenLoop(new DriveSignal(0, 0));
        }

    }

    /**
     * Open-loop control the drivebase with a desired speed and rotation factor.
     * 
     * @param speed    Desired speed percentage [-1.0-1.0]
     * @param rotation Desired rotation factor [-1.0-1.0]
     */
    public void drive(double speed, double rotation) {
        // TODO: Method stub
    }

    /**
     * Set the Open loop control signal. The values of this signal should be in the
     * rage of [-1.0-1.0]
     * 
     * @param signal Open loop signal
     */
    public void setOpenLoop(DriveSignal signal) {

        // Force-set the mode if not already set
        if (m_currentDriveMode != DriveMode.OPEN_LOOP) {

            // Enable motor brakes
            setBrakes(true);

            // Log the state change
            logger.log("DriveTrain", String.format("Set control mode to OPEN_LOOP with signal: %s", signal.toString()));

            // Set the new state
            m_currentDriveMode = DriveMode.OPEN_LOOP;
        }

        // Set the current DriveTrain signal
        m_currentSignal = signal;
    }

    /**
     * Set the Voltage control signal. The values of this signal should be in the
     * rage of [-12.0-12.0]
     * 
     * @param signal Voltage signal
     */
    public void setVoltage(DriveSignal signal) {

        // Force-set the mode if not already set
        if (m_currentDriveMode != DriveMode.VOLTAGE) {

            // Enable motor brakes
            setBrakes(false);

            // Log the state change
            logger.log("DriveTrain", String.format("Set control mode to VOLTAGE with signal: %s", signal.toString()));

            // Set the new state
            m_currentDriveMode = DriveMode.VOLTAGE;
        }

        // Set the current DriveTrain signal
        m_currentSignal = signal;
    }

    /**
     * Set the motor brakes. When enabled, the robot will automatically try to stay
     * in place (resisting pushing)
     * 
     * @param brakesApplied Should the brakes be applied?
     */
    public void setBrakes(boolean brakesApplied) {
        // TODO: Method stub
    }

    /**
     * Set the motor ramp rate. (Time from 0-100% output in seconds)
     * 
     * @param rate Motor ramp rate
     */
    public void setRampRate(double rate) {
        // TODO: Method stub
    }

    /**
     * This method should stop the drivetrain from moving
     */
    public void stop() {

        // Force-set the current DriveSignal to a speed of 0
        m_currentSignal = new DriveSignal(0, 0);

        // Force-set the mode to Open loop
        m_currentDriveMode = DriveMode.OPEN_LOOP;

    }
}