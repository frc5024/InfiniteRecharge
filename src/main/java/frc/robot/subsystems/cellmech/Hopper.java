package frc.robot.subsystems.cellmech;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * The ball hopper subsystem
 */
public class Hopper extends SubsystemBase {

    public static Hopper s_instance = null;

    private Hopper() {

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