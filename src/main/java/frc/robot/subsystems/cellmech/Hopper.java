package frc.robot.subsystems.cellmech;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.motorsensors.TalonEncoder;
import frc.lib5k.components.sensors.EncoderBase;
import frc.robot.RobotConstants;

/**
 * The ball hopper subsystem
 */
public class Hopper extends SubsystemBase {
    public static Hopper s_instance = null;

    /**
     * Motor that moves hopper belt up and down
     */
    private WPI_TalonSRX m_hopperBelt;

    /**
     * Hopper belt encoder
     */
    private EncoderBase m_hopperEncoder;

    /**
     * Bottom line break 
     */
    private DigitalInput m_lineBottom;

    /**
     * Top line break 
     */
    private DigitalInput m_lineTop;

    private Hopper() {
        // Construct motor controller
        m_hopperBelt = new WPI_TalonSRX(RobotConstants.Hopper.HOPPER_BELT_MOTOR);

        // Construct encoder
        m_hopperEncoder = new TalonEncoder(m_hopperBelt);
        m_hopperBelt.setSensorPhase(false);

        // Construct line break
        m_lineBottom = new DigitalInput(RobotConstants.Hopper.HOPPER_LINEBREAK_BOTTOM);
        m_lineTop = new DigitalInput(RobotConstants.Hopper.HOPPER_LINEBREAK_TOP);
    }

    /**
     * Get the instance of Hopper
     * 
     * @return Hopper Instance
     */
    public static Hopper getInstance() {
        if (s_instance == null) {
            s_instance = new Hopper();
        }

        return s_instance;

    }

}