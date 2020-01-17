package frc.robot.subsystems.cellmech;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Robot Shooter subsystem
 */
public class Shooter extends SubsystemBase {
    public static Shooter s_instance = null;

    private static final double DEFAULT_RPM_EPSILON = 50;

    private WPI_TalonSRX m_tempTalon = new WPI_TalonSRX(5);

    private Shooter() {
        // addChild(m_tempTalon);

        m_tempTalon.enableVoltageCompensation(true);
        m_tempTalon.setNeutralMode(NeutralMode.Coast);
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
        SmartDashboard.putNumber("Shooter Voltage", m_tempTalon.getMotorOutputVoltage());
    }
    
    public void setVoltage(double voltage) {
        
    }

    public void setRPM(double rmp) {
        setRPM(rmp, DEFAULT_RPM_EPSILON);
    }

    public void setRPM(double rpm, double epsilon) {
        
    }

}