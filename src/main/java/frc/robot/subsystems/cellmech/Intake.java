package frc.robot.subsystems.cellmech;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotConstants;

/**
 * Robot Intake subsystem
 */
public class Intake extends SubsystemBase {
    public static Intake s_instance = null;

    /**
     * Motor that moves intake up and down
     */
    private WPI_TalonSRX m_intakeActuator;

    /**
     * Motor that drives the roller
     */
    private WPI_TalonSRX m_intakeRoller;

    /**
     * Hall effects sensor at the bottom intake arm position
     */
    private DigitalInput m_bottomHall;

    /**
     * Hall effects sensor at the top intake arm position
     */
    private DigitalInput m_topHall;

    private Intake() {

        // Construct motor controllers
        m_intakeActuator = new WPI_TalonSRX(RobotConstants.Intake.MotorControllers.INTAKE_ACTUATOR_TALON);
        m_intakeRoller = new WPI_TalonSRX(RobotConstants.Intake.MotorControllers.INTAKE_ROLLER_TALON);

        // Construct sensors
        m_bottomHall = new DigitalInput(RobotConstants.Intake.Sensors.INTAKE_HALL_BOTTOM);
        m_topHall = new DigitalInput(RobotConstants.Intake.Sensors.INTAKE_HALL_TOP);
    }

    /**
     * Get the instance of Intake
     * 
     * @return Intake Instance
     */
    public static Intake getInstance() {
        if (s_instance == null) {
            s_instance = new Intake();
        }

        return s_instance;

    }

}