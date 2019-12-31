package frc.lib5k.components;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * A GearBox is a wrapper for any pair of WPI_TalonSRX motor controllers where
 * the first controller has an encoder attached.
 */
@Deprecated(since = "Kickoff 2020", forRemoval = true)
public class GearBox {
    public WPI_TalonSRX front, rear;

    private boolean is_inverse_motion, backEncoders = false;

    /**
     * GearBox Constructor
     * 
     * @param front The front or master talon in the gearbox
     * @param rear  The rear or slave talon in the gearbox
     */
    public GearBox(WPI_TalonSRX front, WPI_TalonSRX rear, boolean backEncoders) {
        /* Store both Talons */
        this.front = front;
        this.rear = rear;

        this.backEncoders = backEncoders;

        /* Configure the Talons */
        this.front.configFactoryDefault();
        this.rear.configFactoryDefault();
        this.rear.follow(this.front);

        this.front.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        

        if (backEncoders) {
            rear.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        } else {
            front.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        }

        // Set voltage comensation
        front.enableVoltageCompensation(true);
        rear.enableVoltageCompensation(true);
    }

    /**
     * Wrapper method around the WPI_TalonSRX current limiting functionality
     * 
     * @param peakCurrent  The current threshold that must be passed before the
     *                     limiter kicks in
     * @param holdCurrent  The current to hold the motors at once the threshold has
     *                     been passed
     * @param peakDuration The duration of the current limit
     */
    public void limitCurrent(int peakCurrent, int holdCurrent, int peakDuration) {
        int timeout = 0;
        this.front.configPeakCurrentLimit(peakCurrent, timeout);
        this.front.configPeakCurrentDuration(peakDuration, timeout);
        this.front.configContinuousCurrentLimit(holdCurrent, timeout);
        front.enableCurrentLimit(true);

        this.rear.configPeakCurrentLimit(peakCurrent, timeout);
        this.rear.configPeakCurrentDuration(peakDuration, timeout);
        this.rear.configContinuousCurrentLimit(holdCurrent, timeout);
        rear.enableCurrentLimit(true);
    }

    /**
     * Wrapper around the encoder for the front or master talon
     * 
     * @return Number of ticks reported by the front or master talon
     */
    public int getTicks() {
        return (backEncoders) ? this.rear.getSelectedSensorPosition() : this.front.getSelectedSensorPosition();
    }

    /**
     * Flips the sensor phase of the GearBox's encoder
     * 
     * This is mainly used for inverse motion profiling
     * 
     * @param is_inverted Should the phase be inverted?
     */
    public void setInverseMotion(boolean is_inverted) {
        if (backEncoders) {
            this.rear.setSensorPhase(is_inverted);
        } else {
            this.front.setSensorPhase(is_inverted);
        }
    }

    /**
     * Is the gearbox currently in inverse motion mode?
     * 
     * @return Is inverse motion?
     */
    public boolean getInverseMotion() {
        return this.is_inverse_motion;
    }

    /**
     * Get the master controller
     * 
     * @return the front talon
     */
    public WPI_TalonSRX getMaster() {
        return this.front;
    }

    /**
     * Set the GearBox speed
     * 
     * @param speed Percent output
     */
    public void set(double speed) {
        this.front.set(speed);
    }

    public GearBoxEncoder wrapEncoder() {
        return new GearBoxEncoder(this);
    }
}