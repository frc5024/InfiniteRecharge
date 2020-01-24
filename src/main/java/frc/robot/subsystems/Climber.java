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
        m_climbServo = new Servo(RobotConstants.Climber.CLIMBER_SERVO);
    
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

    /**
     * Ejects the climber 
     */
    public void ejectClimber(double speed) {
        m_climbServo.set(speed); //TEST VALUE, THE LOWER THE NUMBER, THE HIGHER THE HEIGHT OF THE CLIMBER 
    }

    /**
     * Retracts the climber
     */
    public double retractClimber(double speed) {
        return speed;
    }
}