package frc.robot;

import edu.wpi.first.wpilibj.util.Units;

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
            public static final int RIGHT_FRONT_TALON = 3;
            public static final int RIGHT_REAR_TALON = 4;
        }

        /**
         * Current limiting
         */
        public static class CurrentLimits {
            public static final int PEAK_AMPS = 35;
            public static final int HOLD_AMPS = 33;
            public static final int TIMEOUT_MS = 30;
        }

        /**
         * Encoder constants
         */
        public static class Encoders {

            /* Encoder slots */
            public static final int LEFT_ENCODER_SLOT = 0;
            public static final int RIGHT_ENCODER_SLOT = 0;

            /* Ticks per revolution of the encoder */
            public static final int TICKS_PER_REVOLUTION = 4096;
        }

        /**
         * Component measurements
         */
        public static class Measurements {
            public static final double WHEEL_DIAMETER = Units.inchesToMeters(6.0);
            public static final double WHEEL_CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;

            public static final double DRIVEBASE_WIDTH = Units.inchesToMeters(28.0);
            public static final double DRIVEBASE_LENGTH = Units.inchesToMeters(28.0);

            public static final double GEAR_RATIO = 8.45;

        }
    }

}