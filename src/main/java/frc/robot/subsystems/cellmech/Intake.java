package frc.robot.subsystems.cellmech;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.TalonHelper;
import frc.lib5k.components.motors.motorsensors.TalonEncoder;
import frc.lib5k.components.sensors.EncoderBase;
import frc.robot.RobotConstants;

/**
 * Robot Intake subsystem
 */
public class Intake extends SubsystemBase {
    public static Intake s_instance = null;

    /**
     * Motor that moves intake up and down
     */
    private WPI_TalonSRX m_intakeActuator;

    /**
     * Encoder for arm actuation movement
     */
    private EncoderBase m_intakeActuatorEncoder;

    /**
     * PID controller for intake arm
     */
    private PIDController m_armPIDController;

    /**
     * Motor that drives the roller
     */
    private WPI_TalonSRX m_intakeRoller;

    /**
     * Hall effects sensor at the bottom intake arm position
     */
    private DigitalInput m_bottomHall;

    /**
     * Hall effects sensor at the top intake arm position
     */
    private DigitalInput m_topHall;

    /**
     * System states
     */
    private enum SystemState {
        IDLE, // System Idle
        LOWER, // Arm moving to bottom hall
        INTAKE, // Arm moving to bottom hall and spinning roller inwards
        UNJAM, // Arm moving to bottom hall and spinning roller outwards
        RAISING // Arm moving to top hall
    }

    /**
     * Tracker for intake system state.
     */
    private SystemState m_systemState = SystemState.IDLE;
    private SystemState m_lastState = null;

    private Intake() {

        // Construct motor controllers
        m_intakeActuator = new WPI_TalonSRX(RobotConstants.Intake.INTAKE_ACTUATOR_TALON);
        m_intakeRoller = new WPI_TalonSRX(RobotConstants.Intake.INTAKE_ROLLER_TALON);

        // Set voltage limiting
        TalonHelper.configCurrentLimit(m_intakeActuator, 34, 32, 30, 0);
        TalonHelper.configCurrentLimit(m_intakeRoller, 34, 32, 30, 0);

        // Construct sensors
        m_bottomHall = new DigitalInput(RobotConstants.Intake.INTAKE_HALL_BOTTOM);
        m_topHall = new DigitalInput(RobotConstants.Intake.INTAKE_HALL_TOP);
        m_intakeActuatorEncoder = new TalonEncoder(m_intakeActuator);

        // Construct PID controller
        m_armPIDController = new PIDController(RobotConstants.Intake.kPArm, RobotConstants.Intake.kIArm, RobotConstants.Intake.kDArm);
    }

    /**
     * Get the instance of Intake
     * 
     * @return Intake Instance
     */
    public static Intake getInstance() {
        if (s_instance == null) {
            s_instance = new Intake();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

        // Determine if this state is new
        boolean isNewState = false;
        if (m_systemState != m_lastState) {
            isNewState = true;
        }

        // Handle states
        switch (m_systemState) {
            case IDLE:
                handleIdle(isNewState);
                break;
            case LOWER:
                handleLower(isNewState);
                break;
            case INTAKE:
                handleIntake(isNewState);
                break;
            case UNJAM:
                handleUnjam(isNewState);
                break;
            case RAISING:
                handleRaising(isNewState);
                break;
            default:
                m_systemState = SystemState.IDLE;
        }

        if(m_topHall.get()) {
            m_intakeActuatorEncoder.zero();
        }  
    }

    /**
     * Set arm and roller motors to stop
     * 
     * @param newState Is this state new?
     */
    private void handleIdle(boolean newState) {
        if (newState) {

            // Stop arm movement
            setArmSpeed(0.0);

            // Stop roller
            setRollerSpeed(0.0);

        }
    }

    /**
     * Set arm to move down and roller to stop
     * 
     * @param newState Is this state new?
     */
    private void handleLower(boolean newState) {
        if (newState) {

            // Stop roller
            setRollerSpeed(0.0);

        }

        moveArmTo(90);
    }

    /**
     * Set arm to move down and roller to roll cells in
     * 
     * @param newState Is this state new?
     */
    private void handleIntake(boolean newState) {
        if (newState) {

            // Set roller to take in cells
            setRollerSpeed(0.8);

            m_armPIDController.reset();

        }

        moveArmTo(90);
    }

    /**
     * Set arm to move down and roller to roll cells out
     * 
     * @param newState Is this state new?
     */
    private void handleUnjam(boolean newState) {
        if (newState) {

            // Set arm to move down
            setArmSpeed(0.5);

            // Set roller to take in cells
            setRollerSpeed(-0.8);

        }
    }

    /**
     * Set arm to move up and stop roller
     * 
     * @param newState Is this state new?
     */
    private void handleRaising(boolean newState) {
        if (newState) {

            setRollerSpeed(0.0);

            m_armPIDController.reset();

        }

        // Set arm to move up
        moveArmTo(0);
         
    }

    /**
     * Use PID to move arm
     * 
     * @param angle either 0 or 90
     */
    private void moveArmTo(double desiredAngle) {
        // calculate theoretical current angle
        double currentAngle = ((double)m_intakeActuatorEncoder.getTicks()) / RobotConstants.Intake.ARM_TICKS_PER_DEGREE;
        // set motor output to PID output
        setArmSpeed(m_armPIDController.calculate(currentAngle, desiredAngle));
    }

    /**
     * Sets the speed of the arm. All arm movement should be done with this method, so the arm doesn't try to go past it's limits
     * 
     * @param speed desired speed of the arm -1.0 to 1.0
     */
    private void setArmSpeed(double speed) {

        // if moving down, only move if bottom hall not active
        // if (speed > 0.0) {
        //     if (m_bottomHall.get()) {
        //         speed = 0.0;
        //     }
        // }

        // if moving up, only move if top hall not active
        if (speed < 0.0) {
            if (m_topHall.get()) {
                speed = 0.0;
            }
        }

        m_intakeActuator.set(speed);

    }

    /**
     * Sets the speed of the roller wheel
     * 
     * @param speed desired speed of the roller -1.0 to 1.0
     */
    private void setRollerSpeed(double speed) {
        m_intakeRoller.set(speed);
    }

    /**
     * Set the harvester to unjam
     */
    public void unjam() {
        m_systemState = SystemState.UNJAM;
    }

    /**
     * Set the harvester to intake
     */
    public void intakeCells() {
        m_systemState = SystemState.INTAKE;
    }

    /**
     * Raise the harvester
     */
    public void raise() {
        m_systemState = SystemState.RAISING;
    }

    /**
     * Stop the Harvester
     */
    public void stop() {
        m_systemState = SystemState.IDLE;
    }


}