package frc.robot;

import edu.wpi.first.wpilibj.util.Units;
import frc.lib5k.roborio.RR_HAL;

/**
 * All constants and configuration for the robot should be stored here
 */
public class RobotConstants {

    /**
     * Method for checking if the current robot is not MiniBot
     * 
     * @return Is it a competition robot
     */
    public static boolean isCompBot() {
        return !RR_HAL.getRobotName().equals("MiniBot");
    }

    public static final boolean PUBLISH_SD_TELEMETRY = true;

    public static class Autonomous {

        /**
         * Number of seconds to wait before robot is allowed to score
         */
        public static final double SCORE_LATE_DELAY = 5.0;
    }

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

        public static class Simulation {
            public static final double ENCODER_RAMP_RATE = 0.12;
        }

        /**
         * Motor controller IDs
         */
        public static class MotorControllers {

            /* Motor inversions */
            public static final boolean LEFT_SIDE_INVERTED = false;
            public static final boolean RIGHT_SIDE_INVERTED = true;

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
            public static final int LEFT_ENCODER_SLOT = 1;
            public static final int RIGHT_ENCODER_SLOT = 1;

            /* Encoder phases */
            public static final boolean LEFT_SENSOR_PHASE = false;
            public static final boolean RIGHT_SENSOR_PHASE = true;

            /* Ticks per revolution of the encoder */

            public static final int PULSES_PER_REVOLUTION = 1440; // (isCompBot())? 4096 : 1440;

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

            public static final int MOTOR_MAX_RPM = 5330; // For cim motors

        }
    }

}