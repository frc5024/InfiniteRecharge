package frc.lib5k.drivebase.modules;

import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.SimpleMotorFeedforward;
import edu.wpi.first.wpiutil.math.MathUtil;
import frc.lib5k.components.sensors.EncoderBase;
import frc.lib5k.interfaces.PeriodicComponent;
import frc.lib5k.kinematics.PIDProfile;
import frc.lib5k.roborio.FPGAClock;

/**
 * A TankTrack is the core component used to make up a DifferentialDrivebase
 */
public class TankTrack implements PeriodicComponent {

    // Motor control
    private SpeedController controller;
    private EncoderBase encoder;

    // Track sensor
    private PIDController pid;
    private SimpleMotorFeedforward feedforward = null;
    private double tpm;

    // Control modes
    private enum ControlMode {
        OPEN_LOOP, VOLTAGE, VELOCITY
    }

    private ControlMode mode = ControlMode.OPEN_LOOP;

    // Output setpoint
    private double setpoint = 0.0;

    // Velocity info
    private double lastTime = 0.0;
    private double lastMeters = 0.0;
    private double velocityMPS = 0.0;
    private double maxMPS;

    /**
     * Create a TankTrack
     * 
     * @param controller Track speed controller
     * @param encoder    Track sensor
     * @param tpm        Number of sensor "ticks" per meter traveled
     */
    public TankTrack(SpeedController controller, EncoderBase encoder, double tpm, double maxMPS) {
        this.controller = controller;
        this.encoder = encoder;
        this.tpm = tpm;
        this.maxMPS = maxMPS;
        this.pid = new PIDController(1.0, 0.0, 0.0);
    }

    @Override
    public void update() {

        // Update the encoder
        encoder.update();

        // Determine dt
        double time = FPGAClock.getFPGASeconds();
        double dt = time - lastTime;
        lastTime = time;

        // Determine track velocity
        double meters = encoder.getTicks() / tpm;
        velocityMPS = (meters - lastMeters) / dt;
        lastMeters = meters;

        // Handle each control mode
        switch (mode) {
            case OPEN_LOOP:
                handleOpenLoop();
                break;
            case VELOCITY:
                handleVelocity();
                break;
            case VOLTAGE:
                handleVoltage();
                break;
            default:
                setpoint = 0.0;
                mode = ControlMode.OPEN_LOOP;
                break;

        }

    }

    private void handleOpenLoop() {

        // Set the open-loop output
        controller.set(setpoint);

    }

    private void handleVoltage() {

        // Set the voltage output
        controller.setVoltage(setpoint);

    }

    private void handleVelocity() {

        // Calculate pid
        double pidOutput = pid.calculate(velocityMPS, setpoint);

        // Optionally calculate feedforward
        double ff = 0.0;
        if (feedforward != null) {
            ff = feedforward.calculate(setpoint);
        }

        // Set controller output
        controller.setVoltage(ff + pidOutput);

    }

    /**
     * Configure track velocity PID
     * 
     * @param profile PID profile
     */
    public void configurePID(PIDProfile profile) {
        configurePID(profile.kp, profile.ki, profile.kd);
    }

    /**
     * Configure track velocity PID
     * 
     * @param Kp P gain
     * @param Ki I gain
     * @param Kd D gain
     */
    public void configurePID(double Kp, double Ki, double Kd) {
        this.pid.setPID(Kp, Ki, Kd);
    }

    /**
     * Configure track feedforward
     * 
     * @param feedforward Motor feedforward
     */
    public void configureFeedforward(SimpleMotorFeedforward feedforward) {
        this.feedforward = feedforward;
    }

    /**
     * Set the track's percent output
     * 
     * @param output Percent output
     */
    public void setPercentOutput(double output) {
        mode = ControlMode.OPEN_LOOP;
        setpoint = MathUtil.clamp(output, -1, 1);
    }

    /**
     * Set the track's voltage output
     * 
     * @param output Voltage output
     */
    public void setVoltage(double output) {
        mode = ControlMode.VOLTAGE;
        setpoint = MathUtil.clamp(output, -12, 12);
    }

    /**
     * Set the track's velocity
     * 
     * @param mps Track velocity in MPS
     */
    public void setVelocity(double mps) {
        mode = ControlMode.VELOCITY;
        setpoint = mps;
    }

    /**
     * Get the track velocity
     * 
     * @return Track velocity in MPS
     */
    public double getVelocity() {
        return velocityMPS;
    }

    /**
     * Get track distance in meters
     * 
     * @return Track distance traveled
     */
    public double getMeters() {
        return encoder.getTicks() / tpm;
    }

    /**
     * Get max output in MPS
     * @return Max MPS
     */
    public double getMaxMPS(){
        return maxMPS;
    }

    /**
     * Zeros all sensors and controllers
     */
    public void zero() {
        controller.set(0.0);
        mode = ControlMode.OPEN_LOOP;
        setpoint = 0.0;
        encoder.zero();
        pid.reset();
        velocityMPS = 0.0;
        lastMeters = 0.0;
    }
}