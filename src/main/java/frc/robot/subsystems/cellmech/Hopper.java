package frc.robot.subsystems.cellmech;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.TalonHelper;
import frc.lib5k.components.motors.motorsensors.TalonEncoder;
import frc.lib5k.components.sensors.EncoderBase;
import frc.robot.RobotConstants;

/**
 * The cell hopper subsystem
 */
public class Hopper extends SubsystemBase {
    public static Hopper s_instance = null;

    /**
     * Motor that moves hopper belt up and down
     */
    private WPI_TalonSRX m_hopperBelt;

    /**
     * Hopper belt encoder
     */
    private EncoderBase m_hopperEncoder;
    private int m_ticksAtStartOfIntake;
    private double m_revolutionsPerInch;

    /**
     * Bottom line break
     */
    private DigitalInput m_lineBottom;
    private boolean m_lineBottomLastValue;

    /**
     * Top line break
     */
    private DigitalInput m_lineTop;
    private boolean m_lineTopLastValue;

    /**
     * System states
     */
    private enum SystemState {
        IDLE, // system Idle
        INTAKEREADY, // ready for a cell to enter
        INTAKING, // moving cell up 1 space
        UNJAM, // spit cells out
        MOVETOTOP, // move top cell in hopper to the top
        MOVETOBOTTOM, // move bottom cell in hopper to the bottom
        MOVEUPONEPLACE, // move the hopper up 8 inches
        SHOOTING // supply cells to shooter
    }

    /**
     * Tracker for hopper system state.
     */
    private SystemState m_systemState = SystemState.IDLE;
    private SystemState m_lastState = null;

    private int m_cellCount = 3;

    private int m_desiredAmountToIntake = 0;
    private int m_desiredAmountToHaveAfterShooting = 0;

    private Hopper() {
        // Construct motor controller
        m_hopperBelt = new WPI_TalonSRX(RobotConstants.Hopper.HOPPER_BELT_MOTOR);

        // Set voltage limiting
        TalonHelper.configCurrentLimit(m_hopperBelt, 34, 32, 30, 0);

        // Construct encoder
        m_hopperEncoder = new TalonEncoder(m_hopperBelt);
        m_hopperBelt.setSensorPhase(false);

        // Construct line break
        m_lineBottom = new DigitalInput(RobotConstants.Hopper.HOPPER_LINEBREAK_BOTTOM);
        m_lineTop = new DigitalInput(RobotConstants.Hopper.HOPPER_LINEBREAK_TOP);

        // Set revolutions per inch
        m_revolutionsPerInch = RobotConstants.Hopper.REVOLUTIONS_PER_INCH;

        m_lineBottomLastValue = false;
        m_lineTopLastValue = false;
    }

    /**
     * Get the instance of Hopper
     * 
     * @return Hopper Instance
     */
    public static Hopper getInstance() {
        if (s_instance == null) {
            s_instance = new Hopper();
        }

        return s_instance;

    }

    @Override
    public void periodic() {

        // Count cells

        // cache values of line break sensors
        boolean bottomValue = m_lineBottom.get();
        boolean topValue = m_lineTop.get();

        // If belt is moving up
        if (m_hopperBelt.get() > 0.0) {

            // add when cell enters bottom
            if (bottomValue == true && m_lineBottomLastValue == false) {
                m_cellCount += 1;
            }

            // subtract when cell exits top
            if (topValue == false && m_lineTopLastValue == true) {
                m_cellCount -= 1;
            }

            // If belt is moving down
        } else {

            // subtract when cell exits bottom
            if (bottomValue == false && m_lineBottomLastValue == true) {
                m_cellCount -= 1;
            }

            // add when cell enters top
            if (topValue == true && m_lineTopLastValue == false) {
                m_cellCount += 1;
            }

        }

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
        case INTAKEREADY:
            handleIntakeReady(isNewState);
            break;
        case INTAKING:
            handleIntaking(isNewState);
            break;
        case UNJAM:
            handleUnjam(isNewState);
            break;
        case MOVETOTOP:
            handleMoveToTop(isNewState);
            break;
        case MOVETOBOTTOM:
            handleMoveToBottom(isNewState);
            break;
        case MOVEUPONEPLACE:
            handleMoveUpOnePlace(isNewState);
            break;
        case SHOOTING:
            handleShooting(isNewState);
            break;
        default:
            m_systemState = SystemState.IDLE;
        }

        m_lineBottomLastValue = m_lineBottom.get();
        m_lineTopLastValue = m_lineTop.get();
    }

    /**
     * Set belt off
     * 
     * @param newState Is this state new?
     */
    private void handleIdle(boolean newState) {
        if (newState) {

            // Stop belt
            setBeltSpeed(0.0);

        }
    }

    /**
     * wait to detect cell, when detected, start intaking
     * 
     * @param newState Is this state new?
     */
    private void handleIntakeReady(boolean newState) {
        if (newState) {

            // Stop belt
            setBeltSpeed(0.0);

        }

        // cache values of line break sensors
        boolean bottomValue = m_lineBottom.get();

        if (bottomValue == true && m_lineBottomLastValue == false) {
            if (m_cellCount < m_desiredAmountToIntake) {
                m_systemState = SystemState.INTAKING;
            }
        }

        if (m_cellCount >= 5 || m_cellCount == m_desiredAmountToIntake) {
            m_systemState = SystemState.IDLE;
        }
    }

    /**
     * take in 1 cell
     * 
     * @param newState Is this state new?
     */
    private void handleIntaking(boolean newState) {
        if (newState) {

            // get number of ticks at start of intake
            m_ticksAtStartOfIntake = m_hopperEncoder.getTicks();

            // Start belt
            setBeltSpeed(0.5);

        }
        // if belt has gone 8 inches, set state to ready to intake
        if (m_hopperEncoder.getTicks() - m_ticksAtStartOfIntake >= (4096 * m_revolutionsPerInch) * 8) {
            m_systemState = SystemState.INTAKEREADY;
        }
    }

    /**
     * attempt to spit out all cells
     * 
     * @param newState Is this state new?
     */
    private void handleUnjam(boolean newState) {
        if (newState) {

            // Reverse belt
            setBeltSpeed(-0.8);

        }
    }

    /**
     * move top cell to top of hopper
     * 
     * @param newState Is this state new?
     */
    private void handleMoveToTop(boolean newState) {
        if (newState) {

            // Start belt
            setBeltSpeed(0.5);

        }

        if (m_lineTop.get()) {
            m_systemState = SystemState.IDLE;
        }
    }

    /**
     * move bottom cell to bottom of hopper
     * 
     * @param newState Is this state new?
     */
    private void handleMoveToBottom(boolean newState) {
        if (newState) {

            // Start belt
            setBeltSpeed(-0.5);

        }

        if (m_lineBottom.get()) {
            m_systemState = SystemState.MOVEUPONEPLACE;
        }
    }

    /**
     * move cells up 8 inches
     * 
     * @param newState Is this state new?
     */
    private void handleMoveUpOnePlace(boolean newState) {
        if (newState) {

            // get number of ticks at start of intake
            m_ticksAtStartOfIntake = m_hopperEncoder.getTicks();

            // Start belt
            setBeltSpeed(0.5);

        }
        // if belt has gone 8 inches, set state to ready to intake
        if (m_hopperEncoder.getTicks() - m_ticksAtStartOfIntake >= (4096 * m_revolutionsPerInch) * 8) {
            m_systemState = SystemState.IDLE;
        }
    }

    /**
     * provide cells for shooting
     * 
     * @param newState Is this state new?
     */
    private void handleShooting(boolean newState) {
        if (newState) {

            // Start belt
            setBeltSpeed(RobotConstants.Hopper.SHOOTER_FEED_SPEED);

        }

        if (m_cellCount == 0 || m_cellCount == m_desiredAmountToHaveAfterShooting) {
            m_systemState = SystemState.IDLE;
        }
    }

    /**
     * Sets the speed of the hopper belt
     * 
     * @param speed desired speed of the belt -1.0 to 1.0
     */
    private void setBeltSpeed(double speed) {
        m_hopperBelt.set(speed);
    }

    /**
     * @return current amount of cells in the hopper
     */
    public int getCellCount() {
        return m_cellCount;
    }

    /**
     * @return wether or not the hopper has completed it's actions (if it is idle or not)
     */
    public boolean isDone() {
        return m_systemState == SystemState.IDLE;
    }

    /**
     * Stop shooting and get remaining cells back to the bottom
     */
    public void interruptShooting() {
        m_systemState = SystemState.MOVETOBOTTOM;
    }

    /**
     * Supply cells to shooter until there are none left
     */
    public void supplyCellsToShooter(int amountToSupply) {
        // limit amount to supply to what we have
        amountToSupply = amountToSupply > m_cellCount ? m_cellCount : amountToSupply;
        // make sure amount isn't negative
        amountToSupply = amountToSupply < 0 ? 0 : amountToSupply;
        // set amount to end up with after shooting
        m_desiredAmountToHaveAfterShooting = m_cellCount - amountToSupply;
        // start feeding cells
        m_systemState = SystemState.SHOOTING;
    }

    /**
     * Set the hopper to unjam
     */
    public void unjam() {
        m_systemState = SystemState.UNJAM;
    }

    /**
     * Set the hopper to intake
     */
    public void startIntake(int amountToTakeIn) {
        // make sure amount isn't negative
        amountToTakeIn = amountToTakeIn < 0 ? 0 : amountToTakeIn;
        // set desired amount
        m_desiredAmountToIntake = amountToTakeIn + m_cellCount;
        // limit desired amount to 5 or below
        m_desiredAmountToIntake = m_desiredAmountToIntake > 5 ? 5 : m_desiredAmountToIntake;
        // start intaking
        m_systemState = SystemState.INTAKEREADY;
    }

    /**
     * Stop the hopper
     */
    public void stop() {
        m_systemState = SystemState.IDLE;
    }

}