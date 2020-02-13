package frc.robot.subsystems.cellmech;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.TalonHelper;
import frc.lib5k.components.motors.motorsensors.TalonEncoder;
import frc.lib5k.components.sensors.EncoderBase;
import frc.lib5k.components.sensors.LineBreak;
import frc.lib5k.simulation.wrappers.SimTalon;
import frc.lib5k.utils.Mathutils;
import frc.lib5k.utils.RobotLogger;
import frc.robot.RobotConstants;

/**
 * Robot hopper subsystem
 */
public class Hopper extends SubsystemBase {
    public static Hopper s_instance = null;
    private RobotLogger logger = RobotLogger.getInstance();

    /** Motor that moves hopper belt up and down */
    private SimTalon m_hopperBelt;

    /** Hopper belt encoder */
    private EncoderBase m_hopperEncoder;
    /** ticks of encoders when intake starts */
    private int m_ticksAtStartOfIntake;
    /** how many times the motor turns per inch of belt movement */
    private double m_revolutionsPerInch;

    /** Bottom line break */
    private LineBreak m_lineBottom;
    /** previous value of bottom line break */
    private boolean m_lineBottomLastValue;

    /** Top line break */
    private LineBreak m_lineTop;
    /** previous value of top line break */
    private boolean m_lineTopLastValue;

    /** Middle line break */
    private LineBreak m_lineMiddle;
    /** previous value of Middle line break */
    private boolean m_lineMiddleLastValue;

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

    /** Tracker for hopper system state */
    private SystemState m_systemState = SystemState.IDLE;
    /** previous system state */
    private SystemState m_lastState = null;

    /** amount of cells currently in hopper */
    private int m_cellCount = 0;

    /** amount of cells to have after intaking */
    private int m_desiredAmountToIntake = 5;
    /** amount of cells to have after shooting */
    private int m_desiredAmountToHaveAfterShooting = 0;

    private Hopper() {
        // Construct motor controller
        m_hopperBelt = new SimTalon(RobotConstants.Hopper.HOPPER_BELT_MOTOR);

        // invert motor
        m_hopperBelt.setInverted(RobotConstants.Hopper.HOPPER_BELT_MOTOR_INVERTED);

        // Set voltage limiting
        TalonHelper.configCurrentLimit(m_hopperBelt, 34, 32, 30, 0);

        // Construct encoder
        m_hopperEncoder = new TalonEncoder(m_hopperBelt);
        m_hopperBelt.setSensorPhase(false);

        // Construct line break
        m_lineBottom = new LineBreak(RobotConstants.Hopper.HOPPER_LINEBREAK_BOTTOM,
                RobotConstants.Pneumatics.PCM_CAN_ID, RobotConstants.Hopper.HOPPER_LINEBREAK_BOTTOM_POWER_CHANNEL);
        m_lineTop = new LineBreak(RobotConstants.Hopper.HOPPER_LINEBREAK_TOP, RobotConstants.Pneumatics.PCM_CAN_ID,
                RobotConstants.Hopper.HOPPER_LINEBREAK_TOP_POWER_CHANNEL);
        m_lineMiddle = new LineBreak(RobotConstants.Hopper.HOPPER_LINEBREAK_MIDDLE,
                RobotConstants.Pneumatics.PCM_CAN_ID, RobotConstants.Hopper.HOPPER_LINEBREAK_MIDDLE_POWER_CHANNEL);

        // Set revolutions per inch
        m_revolutionsPerInch = RobotConstants.Hopper.REVOLUTIONS_PER_INCH;

        m_lineBottomLastValue = false;
        m_lineTopLastValue = false;

        // Add children
        addChild("Belt", m_hopperBelt);
        addChild("Bottom Limit", m_lineBottom);
        addChild("Middle Limit", m_lineMiddle);
        addChild("Top Limit", m_lineTop);
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
        } else if (m_hopperBelt.get() < 0.0) {

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

        m_lastState = m_systemState;

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
        m_lineMiddleLastValue = m_lineMiddle.get();
        m_lineTopLastValue = m_lineTop.get();

    }

    /**
     * Set belt off
     * 
     * @param newState Is this state new?
     */
    private void handleIdle(boolean newState) {
        if (newState) {
            logger.log("Hopper", "Became idle");

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
            logger.log("Hopper", "Preparing to intake balls");

            // Stop belt
            setBeltSpeed(0.0);
        }

        // cache values of line break sensors
        boolean bottomValue = m_lineBottom.get();

        // if the bottom line break is tripped off for the first time
        if (bottomValue == true && m_lineBottomLastValue == false) {
            // only intake if the hopper doesn't have the desired amount of cells
            if (m_cellCount < m_desiredAmountToIntake) {
                m_systemState = SystemState.INTAKING;
            }

        }

        // if the hopper has the desired amount of cells, stop intaking
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
            logger.log("Hopper", "Intaking balls");

            // get number of ticks at start of intake
            m_ticksAtStartOfIntake = m_hopperEncoder.getTicks();

            // Start belt
            setBeltSpeed(0.5);

        }

        // stop if middle sensor is tripped and not bottom tripped
        if (m_lineMiddle.get() && !m_lineBottom.get()) {
            m_systemState = SystemState.INTAKEREADY;
        }

        // if belt has gone 12 inches, stop tying and set state to ready to intake
        if (m_ticksAtStartOfIntake - m_hopperEncoder.getTicks() >= 41583) {
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
            logger.log("Hopper", "Unjamming");

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
            logger.log("Hopper", "Pulling balls to top");

            // Start belt
            setBeltSpeed(0.5);

        }

        // when a cell reaches the top, stop
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
            logger.log("Hopper", "Pushing balls to bottom");

            // Start belt
            setBeltSpeed(-0.5);

        }

        // when a cell reaches the bottom, stop
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
            logger.log("Hopper", "Incrementing ball position");

            // get number of ticks at start of intake
            m_ticksAtStartOfIntake = m_hopperEncoder.getTicks();

            // Start belt
            setBeltSpeed(0.5);

        }

        // stop if middle sensor is tripped
        if (m_lineMiddle.get()) {
            m_systemState = SystemState.IDLE;
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
            logger.log("Hopper", "Feeding balls to flywheel");

            // Start belt
            setBeltSpeed(RobotConstants.Hopper.SHOOTER_FEED_SPEED);

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
     * @return wether or not the hopper has completed it's actions (if it is idle or
     *         not)
     */
    public boolean isDone() {
        return m_systemState == SystemState.IDLE;
    }

    /**
     * @return state of the top line break sensor
     */
    public boolean topLineBreakState() {
        return m_lineTop.get();
    }

    /**
     * Stop shooting and get remaining cells back to the bottom
     */
    public void interruptShooting() {
        logger.log("Hopper", "Shooting interrupt requested");
        m_systemState = SystemState.MOVETOBOTTOM;
    }

    /**
     * Supply cells to shooter until there are none left
     * 
     * @param amountToEndUpWith amount of cells to have in the hopper after shooting
     */
    public void supplyCellsToShooter() {
        // Do not log here, because this method gets spammed by the superstructure
        m_systemState = SystemState.SHOOTING;
    }

    /**
     * Set the hopper to unjam
     */
    public void unjam() {
        logger.log("Hopper", "Unjam requested");
        m_systemState = SystemState.UNJAM;
    }

    /**
     * Set the hopper to intake
     */
    public void startIntake(int amountToEndUpWith) {
        logger.log("Hopper", String.format("Intake action requested with a final cell count of %d", amountToEndUpWith));

        // set desired amount
        m_desiredAmountToIntake = amountToEndUpWith;
        // start intaking
        m_systemState = SystemState.INTAKEREADY;
    }

    /**
     * Stop the hopper
     */
    public void stop() {
        logger.log("Hopper", "Stopping hopper");
        m_systemState = SystemState.IDLE;
    }

    /**
     * manually control the belt
     * 
     * @param speed speed to set the belt to
     */
    public void manuallyControlBelt(double speed) {
        m_hopperBelt.set(speed);
    }

    /**
     * Force-override the internal power cell counter
     * 
     * @param count New cell count [0-5]
     */
    public void forceCellCount(int count) {
        logger.log("Hopper", String.format("Cell count force-set to: %d", count));
        m_cellCount = (int) Mathutils.clamp(count, 0, 5);
    }

}