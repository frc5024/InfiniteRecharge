package frc.robot;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.wpilibj.util.Units;
import frc.lib5k.kinematics.PIDProfile;
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
    public static final boolean ENABLE_PID_TUNING_OUTPUTS = false;

    public static class Autonomous {

        /**
         * Number of seconds to wait before robot is allowed to score
         */
        public static final double SCORE_LATE_DELAY = 5.0;

        // Vision-based distance P Gain
        public static final double VISION_DISTANCE_KP = -0.1;

        public static final double AUTO_TARGET_DISTANCE_EPSILON = 5.0;

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
         * HID device id of the driver ans operator Xbox controllers
         */
        public static final int DRIVER_CONTROLLER_ID = 0;
        public static final int OPERATOR_CONTROLLER_ID = 1;

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
            public static final int PEAK_AMPS = 33;
            public static final int HOLD_AMPS = 30;
            public static final int TIMEOUT_MS = 15;
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
            public static final boolean RIGHT_SENSOR_PHASE = false;

            /* Ticks per revolution of the encoder */

            public static final int PULSES_PER_REVOLUTION = 5760; //4096;// 1024 // 2880;//1440; // (isCompBot())? 4096
                                                                 // : 1440;

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

        public static int ALIGNMENT_EPSILON = 3;

    }

    /**
     * Control Gains Measurements
     */
    public static class ControlGains {

        // Feedforward Gains
        public static final double ksVolts = 1.02; // Practice Base 0.837; // MiniBot 2.37
        public static final double kvVoltsSecondsPerMeter = 7.01; // Practice Base 2.46; // 1.8 MiniBot 1.73
        public static final double kaVoltsSecondsSquaredPerMeter = 2.64; // Practice Base 0.0455; // 0.0231 MiniBot
                                                                         // .0304

        // Optimal Control Gain for driving
        public static final double kPDriveVel = 0.478;// 0.68; //0.478;
        public static final double kIDriveVel = 0.0;
        public static final double kDDriveVel = 0.008;

        // Optimal Control Gain for turning
        public static final double kPTurnVel = 0.0085;// 0.030;
        public static final double kITurnVel = 0.01; // 0.12;
        public static final double kDTurnVel = 0.0106; // 0.0066

        // Basic P control for encoder-only distance driving
        public static final double kRP = 0.05;

        // P = 0.027 I = 0.1 D = 0.006

        // Closest: 3.34m

        // PID Controller
        public static PIDController turningPIDController = new PIDController(kPTurnVel, kITurnVel, kDTurnVel);

        public static PIDController drivePidController = new PIDController(kPTurnVel, kITurnVel, kDTurnVel);

        // DifferentialDriveKinematics allows for the use of the track length
        public static final double kTrackWidthMeters = 0.1524;
        public static final DifferentialDriveKinematics kDriveKinematics = new DifferentialDriveKinematics(
                kTrackWidthMeters);

        // Max Trajectory of Velocity and Acceleration
        public static final double kMaxSpeedMetersPerSecond = 3; // This value will most likely need to be changed
        public static final double kMaxAccelerationMetersPerSecondSquared = 1.5; // This value will most likely need to
                                                                                 // be
                                                                                 // changed

        // Ramsete Parameters (Not sure if this is nessacary for trajectory and may need
        // changes)
        public static final double kRamseteB = 2; // in meters
        public static final double kRamseteZeta = .7; // in Seconds

    }

    public static class PanelManipulator {

        public static final int MOTOR_ID = 22;

        public static final int SENSOR_BUFFER_SIZE = 5;

        public static final int DISTANCE_THRESHOLD = 200;

        public static final double SPINNER_SPEED = 1.0;

        /**
         * Threshold for color comparison (taken from REV ColorMatch)
         */
        public static final double DEFAULT_COLOR_THRESHOLD = 0.91;

    }

    /**
     * Constants regarding the intake
     */
    public static class Intake {

        // Motor controller IDs
        public static final int INTAKE_ACTUATOR_TALON = 13;
        public static final int INTAKE_ROLLER_TALON = 14;

        public static final boolean INTAKE_ACTUATOR_TALON_INVERTED = false;
        public static final boolean INTAKE_ROLLER_TALON_INVERTED = true;

        // Sensors DIO ports
        public static final int INTAKE_LIMIT_BOTTOM = 0;
        public static final int INTAKE_LIMIT_TOP = 1;

        // PID values
        public static final double kPArm = 0.011111111111;
        public static final double kIArm = 0.0;
        public static final double kDArm = 0.0;

        public static final double ARM_TICKS_PER_DEGREE = 1000;

        public static final double ARM_UP_SPEED = -0.9;
        public static final double ARM_DOWN_SPEED = 0.45;

        public static final double ROLLER_SPEED = 0.4;
    }

    /**
     * Constants regarding the hopper
     */
    public static class Hopper {

        // Reset Timer
        public static final double RESET_TIMEOUT_SECONDS = 4.0;

        // Motor
        public static final int HOPPER_BELT_MOTOR = 12;

        public static final boolean HOPPER_BELT_MOTOR_INVERTED = true;

        // Sensors
        public static final int HOPPER_LINEBREAK_BOTTOM = 2;
        public static final int HOPPER_LINEBREAK_BOTTOM_POWER_CHANNEL = 1;

        public static final int HOPPER_LINEBREAK_MIDDLE = 4;
        public static final int HOPPER_LINEBREAK_MIDDLE_POWER_CHANNEL = 2;

        public static final int HOPPER_LINEBREAK_TOP = 3;
        public static final int HOPPER_LINEBREAK_TOP_POWER_CHANNEL = 3;

        // Belt speed during shooting
        public static final double SHOOTER_FEED_SPEED = 0.5;
        // how many times the belt gearbox output rotates to move 1 inch
        public static final double REVOLUTIONS_PER_INCH = 2;

        // cycles the robot goes through with the bottom ensor tripped before moving the
        // hopper
        public static final int CYCLES_BEFORE_INTAKE = 10;

        // array of value, cycle duration pairs
        public static final int[][] HOPPER_DONE_RUMBLE_SEQUENCE = { { 1, 5 }, { 0, 3 }, { 1, 5 }, { 0, 1 } };
    }

    public static class Shooter {

        /**
         * Shooter motorm_releasePin.clearAllFaults();
         */
        public static final int MOTOR_ID = 16;

        public static final double MOTOR_MAX_RPM = 4450; // 5700;

        public static final double MOTOR_KV = 371;
        public static final double VOLTAGE_EPSILON = 0.4;
        public static final double RPM_EPSILON = VOLTAGE_EPSILON * MOTOR_KV;

        /* Shooter PID */
        public static final double kPVel = 0.00045; // ;
        public static final double kIVel = 3e-7;// ;
        public static final double kDVel = 0.3;
        public static final double kIz = 0.0;
        public static final double kFF = 0.0;

        /* Limelight */
        public static final double TARGET_HEIGHT = 2.49;
        public static final double LIMELIGHT_HEIGHT = 1;
        public static final double LIMELIGHT_MOUNT_ANGLE = 13;

        /* Launch Angle */
        public static final double LAUNCH_ANGLE = 45;

        /* RPM to m/s */
        public static final double WHEEL_DIAMETER = Units.inchesToMeters(4);
        public static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER * Math.PI;
        public static final double RPM_PER_METER = 1 / WHEEL_CIRCUMFERENCE;
        public static final double RPM_PER_MPS = RPM_PER_METER / 60;

        public static final double DEFAULT_VELOCITY = MOTOR_MAX_RPM * 0.85;

    }

    /**
     * Constants regarding the Climber
     */
    public static class Climber {
        // ALL OF THESE ARE PLACEHOLDERS
        public static final int PIN_RELEASE_SOLENOID = 4;
        public static final int MOTOR_CONTROLLER_ID = 21;
        public static final int HIGH_HALL_ID = 5;
        public static final int LOW_HALL_ID = 6;
    }

    public static class Pneumatics {

        // PCM device ID on CAN network
        public static final int PCM_CAN_ID = 8;
    }

}