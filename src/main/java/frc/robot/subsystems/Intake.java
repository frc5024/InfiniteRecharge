package frc.robot.subsystems;

public class Intake{
    public static Intake m_instance = null;

    /**
     * Get the instance of Intake
     * 
     * @return Intake Instance
     */
    public static Intake getInstance(){
        if(m_instance == null){
            m_instance = new Intake();
        }

        return m_instance;

    }
    
    








}