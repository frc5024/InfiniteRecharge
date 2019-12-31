package frc.robot;

/**
 * All constants and configuration for the robot should be stored here
 */
public class RobotConstants {

    /**
     * Constants regarding human input
     */
    public static class HumanInputs {

        /**
         * Deadband values for various inputs
         */
        public static class Deadbands {

            /**
             * Deadband for rotation control on the driver's Xbox controller
             */
            public static final double ROTATION_INPUT_DEADBAND = 0.2;
            
        }

        /**
         * HID device id of the driver's Xbox controller
         */
        public static final int DRIVER_CONTROLLER_ID = 0;

        
    }
    
}