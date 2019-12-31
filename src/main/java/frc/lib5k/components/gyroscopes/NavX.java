package frc.lib5k.components.gyroscopes;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import frc.lib5k.components.gyroscopes.interfaces.IGyroscope;

/**
 * A wrapper for the AHRS / NavX gyroscope
 */
public class NavX extends AHRS implements IGyroscope {

    private static NavX m_instance = null;

    public NavX(){
        this(Port.kMXP);
    }

    public NavX(SPI.Port port) {
        super(port);

    }
    
    public static NavX getInstance(){
        if (m_instance == null){
            m_instance = new NavX();
        }

        return m_instance;
    }

    @Override
    public double getWrappedAngle() {
        return getAngle() % 360;
    }
    
}