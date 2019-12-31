package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * The DriveTrain handles all robot movement.
 */
public class DriveTrain extends SubsystemBase {
    private static DriveTrain s_instance = null;

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
}