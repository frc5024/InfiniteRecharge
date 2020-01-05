package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.TalonSRXCollection;
import frc.robot.RobotConstants;

/**
 * The DriveTrain handles all robot movement.
 */
public class DriveTrain extends SubsystemBase {
    private static DriveTrain s_instance = null;

    /**
     * Left side gearbox.
     */
    private TalonSRXCollection m_leftGearbox;

    /**
     * Right side gearbox.
     */
    private TalonSRXCollection m_rightGearbox;

    /**
     * DriveTrain constructor.
     * 
     * All subsystem components should be created and configured here.
     */
    private DriveTrain() {

        // Construct both gearboxes
        m_leftGearbox = new TalonSRXCollection(
                new WPI_TalonSRX(RobotConstants.DriveTrain.MotorControllers.LEFT_FRONT_TALON),
                new WPI_TalonSRX(RobotConstants.DriveTrain.MotorControllers.LEFT_REAR_TALON));
        m_rightGearbox = new TalonSRXCollection(
                new WPI_TalonSRX(RobotConstants.DriveTrain.MotorControllers.RIGHT_FRONT_TALON),
                new WPI_TalonSRX(RobotConstants.DriveTrain.MotorControllers.RIGHT_REAR_TALON));

        // Configure the gearboxes
        m_leftGearbox.setCurrentLimit(RobotConstants.DriveTrain.CurrentLimits.PEAK_AMPS,
                RobotConstants.DriveTrain.CurrentLimits.TIMEOUT_MS, RobotConstants.DriveTrain.CurrentLimits.HOLD_AMPS,
                0);
        m_rightGearbox.setCurrentLimit(RobotConstants.DriveTrain.CurrentLimits.PEAK_AMPS,
                RobotConstants.DriveTrain.CurrentLimits.TIMEOUT_MS, RobotConstants.DriveTrain.CurrentLimits.HOLD_AMPS,
                0);
        
        // Disable motor safety
        m_leftGearbox.setMasterMotorSafety(false);
        m_rightGearbox.setMasterMotorSafety(false);


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