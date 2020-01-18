package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.components.limelight.Limelight;
import frc.lib5k.components.limelight.LimelightTarget;
import frc.lib5k.kinematics.DriveSignal;
import frc.lib5k.utils.Mathutils;
import frc.robot.RobotConstants;
import frc.robot.subsystems.DriveTrain;

public class VisionAlign extends CommandBase{

    // Epsilon
    private static final double EPSILON = 2.0;

    // Hard cap percentage on turn speed
    private static final double TURN_SPEED_HARD_CAP = 1.0;

    // Number of cycles to wait before declaring this command "done"
    private static final int MIN_CYCLES = 5;

    //Limelight for targeting
    private Limelight m_limelight;

    // PID controller for angle solves
    private PIDController m_controller;

    // Angle setpoint
    private Rotation2d setpoint;

    // Counter for controller rest cycles (Thanks 1114 for this idea)
    private int cycles = 0;

    /**
     * Turn to align with Limelight target if it exists.
    */
    public VisionAlign(){

        if(m_limelight.isTargetVisible()){

            m_limelight = new Limelight();
            double targetRotation = getTargetRotation();
            align(Rotation2d.fromDegrees(targetRotation));
        }
    }

    /**
     * Turn to align with Limelight target if it exists.
     * If none exists, turn to a default rotation.
     * 
     * @param defaultRotation Default rotation, in degrees.
    */
    public VisionAlign(double defaultRotation){

        if(m_limelight.isTargetVisible()){

            m_limelight = new Limelight();
            double targetRotation = getTargetRotation();
            align(Rotation2d.fromDegrees(targetRotation));
        }else{

            //Default rotation
            align(Rotation2d.fromDegrees(defaultRotation));
        }
    }

    @Override
    public void initialize(){
        // Reset the controller
        m_controller.reset();

        // Reset the cycle count
        cycles = 0;
    }

    private void align(Rotation2d setpoint){

        this.setpoint = setpoint;

        // Set up the PID controller
        m_controller = new PIDController(RobotConstants.ControlGains.kPTurnVel, RobotConstants.ControlGains.kITurnVel,
        RobotConstants.ControlGains.kDTurnVel);

        // Set controller limits
        m_controller.setTolerance(EPSILON);

        // Set the setpoint to 0, so we can calculate by error
        m_controller.setSetpoint(0.0);
    }

    private double getTargetRotation(){
        LimelightTarget target = m_limelight.getTarget();

        double px = target.getX();
        double nx = (1/160) * (px - 159.5);
        double vpw = 2.0*Math.tan(59.6/2);
        double x = vpw/2 * nx;
        double ax = Math.atan2(1,x);
        return ax;
    }

    private double getError() {

        // Convert the WPILib angles to Lib5K-compatible angles
        double setpointAngle = Mathutils.wpiAngleTo5k(setpoint.getDegrees());
        double currentAngle = Mathutils.wpiAngleTo5k(DriveTrain.getInstance().getPosition().getRotation().getDegrees());

        // Find error
        double error = Mathutils.getWrappedError(currentAngle, setpointAngle);

        return error;
    }

    @Override
    public void execute(){
        // Determine system error
        double error = getError();

        // Get the system output
        double output = m_controller.calculate(error, 0.0);

        // Clamp the output
        output = Mathutils.clamp(output, -1.0, 1.0);

        // Hard cap the turn speed
        output *= TURN_SPEED_HARD_CAP;

        // Send output data to motors
        DriveTrain.getInstance().setOpenLoop(new DriveSignal(output, -output));

        // Increase cycle count
        if (m_controller.atSetpoint()) {
            System.out.println(cycles);
            cycles++;
        }
    }

    @Override
    public void end(boolean interrupted){
        // Stop the drivetrain
        DriveTrain.getInstance().stop();
    }

    @Override
    public boolean isFinished() {

        // If we reached the setpoint, and are at it for at least n cycles, we have
        // finished
        return cycles > MIN_CYCLES;
    }
}