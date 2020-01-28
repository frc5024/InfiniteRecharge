package frc.robot.subsystems.cellmech;

import com.ctre.phoenix.Logger;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.control.JRADController;
import frc.lib5k.simulation.wrappers.SimTalon;
import frc.lib5k.utils.Mathutils;
import frc.lib5k.utils.RobotLogger;
import frc.robot.RobotConstants;

/**
 * Robot Shooter subsystem
 */
public class Shooter extends SubsystemBase {
    public static Shooter s_instance = null;

    //Wind-up time
    private long windUpStartTime, windUpEndTime, windUpTotalTime;

    //Logger
    RobotLogger logger = RobotLogger.getInstance();

    /**
     * Shooter motor controller
     */
    private SimTalon m_motorController = new SimTalon(RobotConstants.Shooter.MOTOR_ID);

    /**
     * System states
     */
    private enum SystemState {
        IDLE, // System Idle
        SPIN_UP, // Flywheel spinning up
        SPIN_DOWN, // Flywheel spinning down
        HOLD, // Flywheel holding a speed
        MANUAL, // Manual voltage control
        UNJAM, // Flywheel unjamming

    }

    /**
     * Tracker for shooter system state.
     */
    private SystemState m_systemState = SystemState.IDLE;
    private SystemState m_lastState = null;

    // Output value. Depending on mode, this will become different things
    private double output = 0.0;

    /**
     * Spin-up PID controller
     */
    private PIDController m_spinupController;

    /**
     * Flywheel hold controller
     */
    private JRADController m_holdController;

    private Shooter() {

        // Disable motor brakes
        m_motorController.setNeutralMode(NeutralMode.Coast);

        // Configure the rpm controllers
        m_spinupController = new PIDController(RobotConstants.Shooter.kPVel, RobotConstants.Shooter.kIVel,
                RobotConstants.Shooter.kDVel);
        m_holdController = new JRADController(RobotConstants.Shooter.kJ, RobotConstants.Shooter.kF,
                RobotConstants.Shooter.kLoadRatio);
    }

    /**
     * Get the instance of Shooter
     * 
     * @return Shooter Instance
     */
    public static Shooter getInstance() {
        if (s_instance == null) {
            s_instance = new Shooter();
        }

        return s_instance;

    }

    @Override
    public void periodic() {
        // Publish controller voltage
        SmartDashboard.putNumber("Shooter Voltage", m_motorController.getMotorOutputVoltage());

        // Determine if this state is new
        boolean isNewState = false;
        if (m_systemState != m_lastState) {
            isNewState = true;
        }

        /* Handle states */
        switch (m_systemState) {
        case IDLE:

            // Handle the idle state
            handleIdle(isNewState);
            break;

        case SPIN_UP:

            // Handle the spinup state
            handleSpinUp(isNewState);
            break;

        case SPIN_DOWN:

            // Handle the spindown state
            handleSpinDown(isNewState);
            break;

        case HOLD:

            // Handle holding motor at velocity
            handleHold(isNewState);
            break;

        case UNJAM:

            // TODO: Unjam code
            break;

        default:

            // Set the system to spin down in case it is spinning and loses track of state.
            m_systemState = SystemState.SPIN_DOWN;

        }

        // Set the last state
        m_lastState = m_systemState;
    }

    /**
     * Handle system idle state
     * 
     * @param newState Is this state new?
     */
    private void handleIdle(boolean newState) {

        if (newState) {

            // Enable voltage compensation for use during "test mode"
            m_motorController.enableVoltageCompensation(false);

            // Force-set the motor to 0.0V
            m_motorController.set(0.0);

            // Force-set output
            output = 0.0;
        }

    }

    /**
     * Handle flywheel spinup using PID control to get it near speed
     * 
     * @param newState Is this state new?
     */
    private void handleSpinUp(boolean newState) {

        if (newState) {

            // Reset wind-up
            windUpStartTime = System.currentTimeMillis();

            // Disable motor brakes
            m_motorController.setNeutralMode(NeutralMode.Coast);

            // Disable voltage compensation to allow velocity controllers to take control
            m_motorController.enableVoltageCompensation(false);

            m_motorController.configOpenloopRamp(0.3);

            // Configure the spinup controller
            m_spinupController.reset();
        }

        // Get the current motor output voltage
        double voltage = m_motorController.getMotorOutputVoltage();

        // Calculate the motor output
        double motorOutput = m_spinupController.calculate(voltage, this.output);

        // Accumulate the output
        motorOutput += voltage;

        // Disallow reverse motor output
        motorOutput = Mathutils.clamp(motorOutput, 0, 12);

        // Set the motor output
        m_motorController.setVoltage(motorOutput);

        // TODO: Remove this
        this.m_systemState = SystemState.HOLD;

        // Switch to HOLD state if spinup complete
        if (Mathutils.epsilonEquals(voltage, this.output, RobotConstants.Shooter.VOLTAGE_EPSILON)) {

            // Move to next state
            this.m_systemState = SystemState.HOLD;
        }
    }

    /**
     * Handle flywheel spindown to 0
     * 
     * @param newState Is this state new?
     */
    private void handleSpinDown(boolean newState) {

        if (newState) {

            // Set a large ramp rate to the motor, and "stop" it. This should slow down
            // gradually
            m_motorController.configOpenloopRamp(1.5);
            m_motorController.setNeutralMode(NeutralMode.Brake);
            m_motorController.set(0);
        }

        m_systemState = SystemState.IDLE;

    }

    /**
     * Handle holding the flywheel velocity
     * 
     * @param newState Is this state new?
     */
    private void handleHold(boolean newState) {

        if (newState) {
            windUpEndTime = System.currentTimeMillis();
            windUpTotalTime = windUpEndTime-windUpStartTime;
            // Set the JRAD setpoint
            // m_holdController.setSetpoint(this.output);
            logger.log("[Shooter] Holding. Spin-Up took " + (windUpTotalTime/1000.0) + " seconds.");

            m_motorController.enableVoltageCompensation(true);
        }

        // // Get the current motor output voltage
        // double voltage = m_motorController.getMotorOutputVoltage();

        // // Calculate the motor output
        // double motorOutput = m_holdController.calculate(voltage);

        // motorOutput += voltage;

        // // Disallow reverse motor output
        // // motorOutput = Mathutils.clamp(motorOutput, 0, 12);

        // System.out.println(motorOutput);

        // Set the motor output
        m_motorController.setVoltage(this.output);

    }

    public void setOutputPercent(double val) {

        // Switch to "Spin Up" mode. If we are already at, or above the setpoint, it
        // will automatically move to the next state.
        m_systemState = SystemState.SPIN_UP;

        // Set the desired voltage
        output = val * RobotConstants.Shooter.MAX_VOLTAGE;

    }

    public void stop() {

        // Set the mode to spin down
        m_systemState = SystemState.SPIN_DOWN;

    }

}