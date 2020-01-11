// package frc.robot.autonomous.actions;

// import edu.wpi.first.wpilibj.controller.PIDController;
// import edu.wpi.first.wpilibj.geometry.Pose2d;
// import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
// import frc.robot.RobotConstants;

// public class MotionSolveCommand extends SequentialCommandGroup {

//     private Pose2d m_goal;

//     private PIDController m_speedController;
//     private PIDController m_turnController;

//     private double m_epsilon;

//     public MotionSolveCommand(Pose2d start, Pose2d end, double turnRate, double epsilon) {

//         // Configure PID controllers
//         this.m_speedController = new PIDController(RobotConstants.ControlGains.kPDriveVel,
//                 RobotConstants.ControlGains.kIDriveVel, RobotConstants.ControlGains.kDDriveVel);
//         this.m_turnController = new PIDController(RobotConstants.ControlGains.kPTurnVel,
//                 RobotConstants.ControlGains.kITurnVel, RobotConstants.ControlGains.kDTurnVel);

//         // Set epsilon
//         this.m_speedController.setTolerance(epsilon);
//         this.m_turnController.setTolerance(epsilon);

//         // Set locals
//         this.m_goal = start;
//         this.m_epsilon = epsilon;

//     }

//     @Override
//     public void initialize() {
//         // Reset PID controllers
//         m_speedController.reset();
//         m_turnController.reset();
//     }

//     @Override
//     public void execute() {
        
//     }

//     @Override
//     public boolean isFinished() {
//         return m_speedController.atSetpoint() || 
//     }
// }