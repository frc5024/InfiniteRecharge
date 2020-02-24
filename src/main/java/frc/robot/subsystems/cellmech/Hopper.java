package frc.robot.subsystems.cellmech;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.lib5k.components.motors.TalonHelper;
import frc.lib5k.components.motors.motorsensors.TalonEncoder;
import frc.lib5k.components.sensors.EncoderBase;
import frc.lib5k.components.sensors.LineBreak;
import frc.lib5k.simulation.wrappers.SimTalon;
import frc.lib5k.utils.Mathutils;
import frc.lib5k.utils.RobotLogger;
import frc.robot.OI;
import frc.robot.RobotConstants;

/**
 * Robot hopper subsystem
 */
public class Hopper extends SubsystemBase {
    public static Hopper s_instance = null;
    private RobotLogger logger = RobotLogger.getInstance();

    private OI m_OI = OI.getInstance();

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
    /** counter to delay intaking */
    private int m_intakeDelayCounter = 0;

    /** Top line break */
    private LineBreak m_lineTop;
    /** previous value of top line break */
    private boolean m_lineTopLastValue;

    /** Middle line break */
    private LineBreak m_lineMiddle;
    /** previous value of Middle line break */
    private boolean m_lineMiddleLastValue;

    /** counter to manage rumbling */
    private int m_rumbleCounter;
    /** rumble sequence */
    private int[] m_rumbleSequence;

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

    // Timer for reset action
    private Timer m_resetTimer;

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
        m_lineMiddleLastValue = false;
        m_lineTopLastValue = false;

        // cache array so I don't have to type a bunch of stuff
        int[][] rumbles = RobotConstants.Hopper.HOPPER_DONE_RUMBLE_SEQUENCE;

        // find length of array and create array
        int arrayLength = 0;
        for (int i = 0; i < rumbles.length; i++) {
            arrayLength += rumbles[i][1];
        }
        m_rumbleSequence = new int[arrayLength];

        // set rumbling to be done
        m_rumbleCounter = arrayLength;

        // parse 2d array of value,duration pairs into a 1d array of values
        int i = 0;
        for (int y = 0; y < rumbles.length; y++) {
            for (int x = 0; x < rumbles[y][1]; x++) {
                m_rumbleSequence[i] = rumbles[y][0];
                i++;
            }
        }

        // Set up the reset timer
        m_resetTimer = new Timer();
        m_resetTimer.reset();

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
        if (m_rumbleCounter < m_rumbleSequence.length) {
            m_OI.rumbleOperator((double) m_rumbleSequence[m_rumbleCounter]);
            System.out.println(m_rumbleSequence[m_rumbleCounter]);
            m_rumbleCounter++;
        }

        if (m_systemState == SystemState.INTAKING || m_systemState == SystemState.INTAKEREADY
                || m_systemState == SystemState.UNJAM || m_systemState == SystemState.SHOOTING
                || m_systemState == SystemState.MOVETOTOP) {
            // Count cells

            // cache values of line break sensors
            boolean middleValue = m_lineMiddle.get();
            // If belt is moving up
            if (m_hopperBelt.get() > 0.0) {

                // add when cell enters bottom
                if (middleValue == true && m_lineMiddleLastValue == false) {
                    modifyCellCount(1);
                }

                // If belt is moving down
            } else if (m_hopperBelt.get() < 0.0) {

                // subtract when cell exits bottom
                if (middleValue == false && m_lineMiddleLastValue == true) {
                    modifyCellCount(-1);
                }

            }

        }

        // subtract when cell exits top when hopper is moving up and in shoot or idle
        // mode
        if ((m_systemState == SystemState.SHOOTING || m_systemState == SystemState.IDLE
                || m_systemState == SystemState.MOVETOTOP)) {
            if (m_lineTop.get() == false && m_lineTopLastValue == true) {
                modifyCellCount(-1);
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
        // increase counter when cell in range, reset to 0 when out of range
        if (m_lineBottom.get()) {
            m_intakeDelayCounter++;

            // if count reaches the desired amount of delay, starting intaking the cell
            if (m_intakeDelayCounter >= RobotConstants.Hopper.CYCLES_BEFORE_INTAKE) {
                m_systemState = SystemState.INTAKING;
                m_intakeDelayCounter = 0;
            }

        } else {
            m_intakeDelayCounter = 0;
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

        // if no cells in hopper, only rely on sensors
        if (m_cellCount > 0) {
            // if belt has gone 12 inches, stop tying and set state to ready to intake
            if (m_ticksAtStartOfIntake - m_hopperEncoder.getTicks() >= 41583) {
                m_systemState = SystemState.INTAKEREADY;
            }
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

            // Reset the timer
            m_resetTimer.reset();
            m_resetTimer.start();

        }

        // when a cell reaches the bottom, stop
        if (m_lineBottom.get()) {
            m_systemState = SystemState.MOVEUPONEPLACE;
        }

        // If our timer runs out, reset the counters, and go idle
        if (m_resetTimer.hasPeriodPassed(RobotConstants.Hopper.RESET_TIMEOUT_SECONDS)) {
            forceCellCount(0);
            m_systemState = SystemState.IDLE;
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

        // stop if middle or top sensor is tripped
        if (m_lineMiddle.get() || m_lineTop.get()) {
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
     * @return state of the top line break sensor
     */
    public boolean getTopLineBreak() {
        return m_lineTop.get();
    }

    /**
     * @return current amount of cells in the hopper
     */
    public int getCellCount() {
        return m_cellCount;
    }

    /**
     * @param changeAmount amount to increase or decrease the cell count by
     */
    public void modifyCellCount(int changeAmount) {
        m_cellCount += changeAmount;
        m_cellCount = (int) Mathutils.clamp(m_cellCount, 0, 5);
    }

    public void startRumble() {
        m_rumbleCounter = 0;
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
        if (m_cellCount > 0) {
            m_systemState = SystemState.MOVETOBOTTOM;
        }
    }

    /**
     * Supply cells to shooter until there are none left
     * 
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
    public void startIntake() {
        logger.log("Hopper", "Intake requested");
        m_systemState = SystemState.INTAKEREADY;
    }

    /**
     * Stop the hopper
     */
    public void stop() {
        m_systemState = SystemState.IDLE;
    }

    /**
     * moves cells to bottom
     */
    public void moveCellsToBottom() {
        m_systemState = SystemState.MOVETOBOTTOM;
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