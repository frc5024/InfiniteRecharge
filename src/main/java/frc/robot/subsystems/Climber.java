package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Robot subsystem in charge of endgame climbing
 */
public class Climber extends SubsystemBase {
    public static Climber s_instance = null;

    private Climber() {

    }

    /**
     * Get the instance of Climber
     * 
     * @return Climber Instance
     */
    public static Climber getInstance() {
        if (s_instance == null) {
            s_instance = new Climber();
        }

        return s_instance;

    }

}