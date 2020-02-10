package frc.lib5k.control;

public class BoostBackController {

    private SlewLimiter m_slew;
    private double Kp, Kb, Te;

    public BoostBackController(double slewRate, double Te, double Kp, double Kb) {
        
    }

    public double feed() {
        return 0.0;
    }
    
}