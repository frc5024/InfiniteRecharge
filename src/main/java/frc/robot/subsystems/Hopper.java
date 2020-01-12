package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Robot subsystem in charge of ball serialization and storage.
 */
public class Hopper extends SubsystemBase {
    private static Hopper s_instance = null;

    private Hopper() {

    }

    /**
     * Get the Hopper system instance
     * 
     * @return Hopper instance
     */
    public static Hopper getInstance() {
        if (s_instance == null) {
            s_instance = new Hopper();
        }

        return s_instance;
    }

    @Override
    public void periodic() {
        
    }

}