package frc.lib5k.kinematics;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.lib5k.control.PIDv2;
import frc.lib5k.spatial.LocalizationEngine;
import frc.lib5k.utils.Mathutils;

public class MovementPlanner {
    PIDv2 m_forwardController;
    PIDv2 m_turnController;

    /**
     * Create a MovementPlanner from two PIDProfiles
     * 
     * @param forwardCFG Front-Back movement PIDProfile
     * @param turnCFG    Turning PIDProfile
     */
    public MovementPlanner(PIDProfile forwardCFG, PIDProfile turnCFG) {
        // Create each PID controller from PIDProfiles
        m_forwardController = new PIDv2(forwardCFG);
        m_turnController = new PIDv2(turnCFG);
    }

    /**
     * Compute a MovementSegment from the current robot location, and some
     * parameters. To follow a path from the current robot position to the end
     * position, this should be called once every 20ms until the segment is
     * finished. REMEMBER: a LocalizationEngine instance must exist and be updated
     * for this to work!
     * 
     * @param end         Desired FieldPosition to move to
     * @param constraints System output constraints
     * @param turnRate    How quickly the robot should turn to it's goal. Play with
     *                    this number to generate arcs
     * @param epsilon     Acceptable error from the end position (in meters)
     * 
     * @return MovementSegment to be executed by the robot's DriveTrain
     */
    public MovementSegment compute(FieldPosition end, DriveConstraints constraints, double turnRate, double epsilon) {

        // Configure the output constraints of the PID controllers
        m_forwardController.setOutputConstraints(-constraints.getMaxVel(), constraints.getMaxVel());
        m_turnController.setOutputConstraints(-constraints.getMaxTurn(), constraints.getMaxTurn());

        // Determine 2D error from end point
        Error2D error = LocalizationEngine.getInstance().getRotatedError(end);

        // Get the robot's position
        FieldPosition robotPosition = LocalizationEngine.getInstance().getRobotPosition();

        // Flip X if driving backwards
        if (error.getY() < 0) {
            error.setX(-error.getX());
        }


        // Increase turning aggression based on path progress
        double turnModifier = (error.getX() * turnRate);
        // turnModifier = 1;

        // Bind the turnModifier to the max turn rate or 0
        double maxTurn = constraints.getMaxTurn();
        if (turnModifier > maxTurn) {
            turnModifier = maxTurn;
        } else if (turnModifier < -maxTurn) {
            turnModifier = -maxTurn;
        }

        // Restrict the end theta by the modifier. This makes the robot turn more based
        // on path completion
        double targetHeading = end.getTheta() - turnModifier;

        // Determine the error from the current angle to the end angle
        double headingErr = Mathutils.getWrappedError(robotPosition.getTheta(), targetHeading);

        // Determine the desired robot speed
        double speed = m_forwardController.calculate(error.getY());

        // Implement distance-based speed ramping
        double cappedHeadingErr = (headingErr > 90) ? 90 : headingErr;
        double speedModifier = (((-1 * cappedHeadingErr) / 90.0) + 1);

        // Modify speed with distance-based speed ramping
        speed *= speedModifier;

        // Determine turn rate from PID controller.
        // This must be inverted due to the fact that the heading error calculation
        // returns the proper error, not a motor speed
        double turn = -m_turnController.calculate(headingErr);

        // Determine if the robot has reached the end point yet
        boolean finished = false;
        if (constraints.getMinVel() <= 0.5) {
            // Check if the forward PID loop is finished
            if (m_forwardController.isFinished(epsilon)) {

                // Force-zero all outputs
                finished = true;
                speed = 0.0;
                turn = 0.0;
                reset();
            }
        } else if (Math.abs(error.getY()) < epsilon) {

            // Force-zero all outputs
            finished = true;
            speed = 0.0;
            turn = 0.0;
            reset();
        }

        MovementSegment segment = new MovementSegment(speed, turn, finished);
        System.out.println(error + "" + segment);
        // Return a movementSegment containing the system outputs
        return segment;
    }

    /**
     * Publish PIDController objects to Shuffleboard in the "MovementPlanner" tab
     */
    public void publishPIDControllers() {
        publishPIDControllers("MovementPlanner");
    }

    /**
     * Publish PIDController objects to Shuffleboard
     * 
     * @param tabName Shuffleboard tab name
     */
    public void publishPIDControllers(String tabName) {
        Shuffleboard.getTab(tabName).add("ForwardPID", m_forwardController);
        Shuffleboard.getTab(tabName).add("TurnPID", m_turnController);
    }

    /**
     * Reset the MovementPlanner. This should be called after each segment has been
     * completed
     */
    public void reset() {
        m_forwardController.reset();
        m_turnController.reset();
    }
}