package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Robot subsystem in charge of shooting balls into the field goals
 */
public class Shooter extends SubsystemBase {
    public static Shooter s_instance = null;

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

}