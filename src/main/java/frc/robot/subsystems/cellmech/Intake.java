package frc.robot.subsystems.cellmech;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Robot Intake subsystem
 */
public class Intake extends SubsystemBase {
    public static Intake s_instance = null;

    private Intake() {

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