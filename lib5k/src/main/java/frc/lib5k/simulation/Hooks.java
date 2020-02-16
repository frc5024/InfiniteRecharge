package frc.lib5k.simulation;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;

/**
 * A collection of tools for hooking into robot simulation endpoints
 */
public class Hooks {

    /**
     * All possible robot states
     */
    public enum RobotState {
        TELEOP, DISABLED, AUTONOMOUS, TEST;
    }

    /**
     * Set robot mode only if the robot code is running in simulation
     * 
     * @param state Robot program state
     */
    public static void setStateIfSimulated(RobotState state) {
        if (RobotBase.isSimulation()) {
            setRobotState(state);
        }
    }

    // public static void publishSimulationModeChooser() {
    //     SendableBuilder modeSendable = SendableBuilder

    //     Shuffleboard.getTab("Simulation").add(new Chooser());
        
    // }

    /**
     * Set the robot program state
     * 
     * @param state Robot program state
     */
    public static void setRobotState(RobotState state) {

        // Reset all HAL program states
        DriverStation.getInstance().InOperatorControl(false);
        DriverStation.getInstance().InAutonomous(false);
        DriverStation.getInstance().InDisabled(false);
        DriverStation.getInstance().InTest(false);

        // Enable correct mode based on state
        switch (state) {

        // Handle teleop
        case TELEOP:
            DriverStation.getInstance().InOperatorControl(true);
            break;

        // Handle autonomous
        case AUTONOMOUS:
            DriverStation.getInstance().InAutonomous(true);
            break;

        // Handle Test mode
        case TEST:
            DriverStation.getInstance().InTest(true);
            break;

        // Handle disabled
        case DISABLED:
            DriverStation.getInstance().InDisabled(true);
            break;
        default:
            DriverStation.getInstance().InDisabled(true);

        }
    }


}