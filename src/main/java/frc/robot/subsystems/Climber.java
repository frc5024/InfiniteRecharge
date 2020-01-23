package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Servo;

import frc.robot.RobotConstants;

/**
 * Robot climber subsystem
 */
public class Climber extends SubsystemBase {
    public static Climber s_instance = null;

    /**
     * Servo motor on the robot
     */
    private Servo m_climbServo;

    private Climber() {
        m_climbServo = new Servo(RobotConstants.Climber.CLIMBER_TALON);
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