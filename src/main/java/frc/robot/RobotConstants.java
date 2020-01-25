package frc.robot;

import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.util.Color8Bit;
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

            public static final int PULSES_PER_REVOLUTION = 1024;// 2880;//1440; // (isCompBot())? 4096 : 1440;

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

    /**
     * Control Gains Measurements
     */
    public static class ControlGains {

        // Feedforward Gains
        public static final double ksVolts = 2.37;
        public static final double kvVoltsSecondsPerMeter = 1.8;
        public static final double kaVoltsSecondsSquaredPerMeter = 0.0231;

        // Optimal Control Gain for driving
        public static final double kPDriveVel = 0.478;// 0.68;//0.478;
        public static final double kIDriveVel = 0.0;
        public static final double kDDriveVel = 0.008;

        // Optimal Control Gain for turning
        public static final double kPTurnVel = 0.0268;
        public static final double kITurnVel = 0.1;
        public static final double kDTurnVel = 0.0066; // 6

        public static final double kRP = 0.05;

        // P = 0.027 I = 0.1 D = 0.006

        // Closest: 3.34m

        // DifferentialDriveKinematics allows for the use of the track length
        public static final double kTrackWidthMeters = 0.1524;
        public static final DifferentialDriveKinematics kDriveKinematics = new DifferentialDriveKinematics(
                // DriveTrain.Measurements.DRIVEBASE_WIDTH);
                kTrackWidthMeters);

        // Max Trajectory of Velocity and Acceleration
        public static final double kMaxSpeedMetersPerSecond = 3; // This value will most likely need to be changed
        public static final double kMaxAccelerationMetersPerSecondSquared = 3.5; // This value will most likely need to
                                                                                 // be
                                                                                 // changed

        // Ramsete Parameters (Not sure if this is nessacary for trajectory and may need
        // changes)
        public static final double kRamseteB = 2; // in meters
        public static final double kRamseteZeta = .7; // in Seconds

    }

    public static class PanelManipulator {

        public static final int SPINNER_MOTOR_ID = 5;

        
        /**
         * Game Colors
         */
        public static Color8Bit RED_COLOR    = new Color8Bit(255, 0, 0);
        public static Color8Bit GREEN_COLOR  = new Color8Bit(0, 255, 0);
        public static Color8Bit BLUE_COLOR   = new Color8Bit(0, 255, 255);
        public static Color8Bit YELLOW_COLOR = new Color8Bit(255, 255, 0);

    }

    public static class Shooter {

        /**
         * Shooter motor
         */
        public static final int MOTOR_ID = 6;

        public static final double VOLTAGE_EPSILON = 0.2;

        public static final double MAX_VOLTAGE = 12;

        /* Shooter spinup PID */
        public static final double kPVel = 0.38;
        public static final double kIVel = 0.0;
        public static final double kDVel = 0.0;

        /* Shooter hold Gains */
        public static final double kJ = 0.0;
        public static final double kF = 0.0;
        public static final double kLoadRatio = 0.1;
    }

}