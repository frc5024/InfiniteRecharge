package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Robot Shooter subsystem
 */
public class Shooter extends SubsystemBase {
    public static Shooter s_instance = null;

    private static final double DEFAULT_RPM_EPSILON = 50;

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

    }
    
    public void setVoltage(double voltage) {
        
    }

    public void setRPM(double rmp) {
        setRPM(rmp, DEFAULT_RPM_EPSILON);
    }

    public void setRPM(double rpm, double epsilon) {
        
    }

}