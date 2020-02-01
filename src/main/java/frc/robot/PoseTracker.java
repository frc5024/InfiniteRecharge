package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotBase;
import frc.robot.vision.LimelightTarget;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import frc.lib5k.components.gyroscopes.NavX;
import frc.robot.subsystems.DriveTrain;
import frc.robot.vision.Limelight2;

public class PoseTracker{
    public static PoseTracker s_instance = null;
    
    // Gets Instances of LimeLight and the Drive Train
    private static Limelight2 limelight = Limelight2.getInstance();
    private DriveTrain driveTrain = DriveTrain.getInstance();

    // Crates the thread
    private Notifier m_thread;

    // Stores Robot Position
    private Pose2d m_robotPose;    

    // Stores the state of the LimeLight LED
    private int LEDState;

    // Stores the vertical offset
    private double verticalOffset;

    // Distance
    private double distance;

    // Stores the heading
    private double heading;

    // This variable stores wheither this is the first time the camera has been enabled
    private Boolean firstEnable = false;


    // Odometry object for tracking robot position   
    public DifferentialDriveOdometry m_odometry;

    // Creates instance
    public static PoseTracker getInstance(){
        if(s_instance == null){
            s_instance = new PoseTracker();

            return s_instance;
        }

        return s_instance;
    }
    
    
    // Starts thread 
    public PoseTracker(){
        m_thread = new Notifier(this::update);
        m_thread.startPeriodic(.01);
    }
    
    // Runs every .01 seconds
    public void update(){
        // Reads the heading
        heading = NavX.getInstance().getHeading();

        /* Handle odometry updates */
        m_odometry.update(Rotation2d.fromDegrees(heading), 
                        driveTrain.getLeftMeters(), driveTrain.getRightMeters());

        // stores the odometry
        m_robotPose = m_odometry.getPoseMeters();
        
        // Runs only during tele-op and checks if the limelight is on
        if(DriverStation.getInstance().isOperatorControl()){
            // Runs if the LED on the limelight is on and this is the first loop that it is enabled
            // calculates the distance from the target
            if(LEDState == 3 && firstEnable){
                // Gets the vertical offset of the camera
                verticalOffset = limelight.getYAngle();

                // Gets the distance TODO proper camera measurements
                // Calculates the distance from the target in a straight line
                distance = (FieldConstants.HEIGHT_OF_TARGET - RobotConstants.Camera.CAMERA_HEIGHT)
                                 / (Math.tan(verticalOffset - RobotConstants.Camera.CAMERA_ANGLE_1));

                // Resets the position to new position
                m_odometry.resetPosition(new Pose2d(Math.sin(heading) / distance, Math.sin(heading - 90), 
                                new Rotation2d(heading)), new Rotation2d(heading));
                

                firstEnable = false;
            }else if(LEDState == 1){
                firstEnable = true;
            }
        }
        // reads the limelights led state
        LEDState = limelight.getLED().getHandle();
    } 

    /**
     * 
     * @return returns the odometry
     */
    public DifferentialDriveOdometry getOdometry(){
        return m_odometry;
    }

    /**
     * 
     * @return the robot position
     */
    public Pose2d getRobotPose(){
        return m_robotPose;
    }

    




}