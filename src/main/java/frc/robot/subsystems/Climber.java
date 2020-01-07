package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Robot climber subsystem
 */
public class Climber extends SubsystemBase {
    public static Climber m_instance = null;

    private Climber() {

    }

    /**
     * Get the instance of Climber
     * 
     * @return Climber Instance
     */
    public static Climber getInstance() {
        if (m_instance == null) {
            m_instance = new Climber();
        }

        return m_instance;

    }

}