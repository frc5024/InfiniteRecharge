package frc.lib5k.framework;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.lib5k.framework.EventHandler;
import frc.lib5k.framework.events.RobotState;

public abstract class RobotFramework extends TimedRobot {

    @Override
    public void robotPeriodic() {
        periodic();
    }

    @Override
    public void autonomousInit() {
        EventHandler.getInstance().observeRobotState(RobotState.AUTONOMOUS);
        inAutonomous(true);
    }

    @Override
    public void autonomousPeriodic() {
        inAutonomous(false);
    }

    @Override
    public void teleopInit() {
        EventHandler.getInstance().observeRobotState(RobotState.TELEOP);
        inOperatorControl(true);
    }

    @Override
    public void teleopPeriodic() {
        inOperatorControl(false);
    }

    @Override
    public void disabledInit() {
        EventHandler.getInstance().observeRobotState(RobotState.DISABLED);
        inDisabled(true);
    }

    @Override
    public void disabledPeriodic() {
        inDisabled(false);
    }

    @Override
    public void testInit() {
        EventHandler.getInstance().observeRobotState(RobotState.UNIT_TEST);
        inTest(true);
    }

    @Override
    public void testPeriodic() {
        inTest(false);
    }

    /**
     * Periodic loop for mode-independent robot code
     */
    public abstract void periodic();

    /**
     * Periodic runner for robot autonomous mode
     * 
     * @param isNew Did the robot just enter autonomous
     */
    public abstract void inAutonomous(boolean isNew);

    /**
     * Periodic runner for robot "teleop" mode
     * 
     * @param isNew Did the robot just enter teleop
     */
    public abstract void inOperatorControl(boolean isNew);

    /**
     * Periodic runner while the robot is disabled
     * 
     * @param isNew Did the robot just become disabled
     */
    public abstract void inDisabled(boolean isNew);

    /**
     * Periodic runner for robot test mode
     * 
     * @param isNew Did the robot just enter test mode
     */
    public abstract void inTest(boolean isNew);
}