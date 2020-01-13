package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.roborio.RR_HAL;

/**
 * Robot subsystem in charge of shooting balls into the field goals
 */
public class Shooter extends SubsystemBase {
    public static Shooter s_instance = null;

    private static final double DEFAULT_RPM_EPSILON = 50;

    public enum ControlType {
        OPEN_LOOP, // Open loop voltage control
        SPIN_UP, // Using PID to get flywheel up to speed
        HOLD, // Using JRAD to keep flywheel spinning at a setpoint speed
        UNJAM, // Unjam the shooter by rotating flywheel backwards at a slow speed to feed
               // balls back to hopper
    }

    /* Shooter state */
    private ControlType m_controlType = ControlType.OPEN_LOOP;
    private double m_output = 0.0;

    private Shooter() {

    }

    /**
     * Get the instance of Shooter
     * 
     * @return Shooter Instance
     */
    public static Shooter getInstance() {
        if (s_instance == null) {
            s_instance = new Shooter();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

        // TODO: handle states here
        switch (m_controlType) {
        case OPEN_LOOP:
            break;
        case SPIN_UP:
            break;
        case HOLD:
            break;
        case UNJAM:

            // Un-jamming is just rotating backwards slowly
            setOutput(-0.3);
            break;
        default:

            // Just safety-set the flywheel to "off"
            setOutput(0.0);
        }
    }

    /**
     * Set OPEN LOOP voltage. Note: this is not the same as using a PID + JRAD
     * controller fot voltage.
     * 
     * @param voltagePercent Open loop voltage percentage output
     */
    public void setOutput(double voltagePercent) {

        // TODO: fill this out
    }

    /**
     * Set desired shooter RPM
     * 
     * @param rmp Desired RPM
     */
    public void setRPM(double rmp) {
        setRPM(rmp, DEFAULT_RPM_EPSILON);
    }

    /**
     * Set the flywheel desired RPM with an acceptable error for spin-up
     * 
     * @param rpm     Desired RPM
     * @param epsilon Allowed error
     */
    public void setRPM(double rpm, double epsilon) {

        // TODO: fill this out
    }

    /**
     * Set the shooter to un-jam mode
     */
    public void unjam() {
        // Set the control type
        m_controlType = ControlType.UNJAM;
    }

    /**
     * Convert a flywheel RPM to a motor voltage
     * 
     * @param rmp RPM
     * @return Calculated voltage
     */
    public double rpmToVoltage(double rmp) {

        // Robot supply voltage
        double controllerVoltage = RR_HAL.getSimSafeVoltage();

        // TODO: Make this conversion
        return 0.0;
    }

}