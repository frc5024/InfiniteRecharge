package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.kinematics.DriveSignal;

/**
 * The DriveTrain handles all robot movement.
 */
public class DriveTrain extends SubsystemBase {
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

    }

    /**
     * Open-loop control the drivebase with a desired speed and rotation factor.
     * 
     * @param speed    Desired speed percentage [-1.0-1.0]
     * @param rotation Desired rotation factor [-1.0-1.0]
     */
    public void drive(double speed, double rotation) {

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
     * This method should stop the drivetrain from moving
     */
    public void stop() {

        // Force-set the current DriveSignal to a speed of 0
        m_currentSignal = new DriveSignal(0, 0);

        // Force-set the mode to Open loop
        m_currentDriveMode = DriveMode.OPEN_LOOP;

    }
}