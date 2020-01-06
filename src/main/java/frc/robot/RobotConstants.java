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

    /**
     * Constants regarding the DriveTrain
     */
    public static class DriveTrain {

        /**
         * Motor controller IDs
         */
        public static class MotorControllers {

            /* Left side Talons */
            public static final int LEFT_FRONT_TALON = 1;
            public static final int LEFT_REAR_TALON = 2;

            /* Right side Talons */
            public static final int RIGHT_FRONT_TALON = 1;
            public static final int RIGHT_REAR_TALON = 1;
        }

        /**
         * Current limiting
         */
        public static class CurrentLimits {
            public static final int PEAK_AMPS = 35;
            public static final int HOLD_AMPS = 33;
            public static final int TIMEOUT_MS = 30;
        }
    }

    public static class PanelManipulator {

        /**
         * Threshold for color comparison
         */
        public static final double DEFAULT_COLOR_THRESHOLD = 20;

    }

}