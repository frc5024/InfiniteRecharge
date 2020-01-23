package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.DigitalInput;

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

    /**
     * Hall effect sensors on the climbers
     */
    private DigitalInput m_lowHalleffect;
    private DigitalInput m_highHalleffect;

    private Climber() {
    
        /*Climb Servo */
        m_climbServo = new Servo(RobotConstants.Climber.CLIMBER_TALON);
    
        /*Low and High Halleffect sensors */
        m_lowHalleffect = new DigitalInput(RobotConstants.Climber.LOW_HALLEFFECT);
        m_highHalleffect = new DigitalInput(RobotConstants.Climber.HIGH_HALLEFFECT);
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

    public void lowClimb() {
        /**
         * If the lower hall effect sensor gets triggered
         */
        if (m_lowHalleffect.get()) {

        }
    }

    public void highClimb() {
        /**
         * If the higher hall effect sensor gets triggered
         */
        if (m_highHalleffect.get()) {
            
        }
    }
}