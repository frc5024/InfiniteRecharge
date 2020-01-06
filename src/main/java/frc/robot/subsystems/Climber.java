package frc.robot.subsystems;

public class Climber{
    public static Climber m_instance = null;

    /**
     * Get the instance of Climber
     * 
     * @return Climber Instance
     */
    public static Climber getInstance(){
        if(m_instance == null){
            m_instance = new Climber();
        }

        return m_instance;

    }


}