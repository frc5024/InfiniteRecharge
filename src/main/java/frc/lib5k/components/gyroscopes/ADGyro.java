package frc.lib5k.components.gyroscopes;

import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import frc.lib5k.components.drive.IDifferentialDrivebase;

public class ADGyro extends ADXRS450_Gyro {

    private static ADGyro m_instance = null;

    private boolean inverted = false;

    /* Simulation */
    private final double ROTATION_SPEED_GAIN = 40;
    private final double SIMULATION_PERIOD = 0.02;
    private IDifferentialDrivebase m_simDrivebase;
    private double[] m_simSensorReadings = new double[2];
    private Notifier m_simThread;
    private SimDevice m_simDevice;
    private SimDouble m_simAngle;
    private SimDouble m_simRate;

    public ADGyro() {
        super(SPI.Port.kOnboardCS0);
    }

    /**
     * Get the Default NavX instance
     * 
     * @return NavX instance
     */
    public static ADGyro getInstance() {
        if (m_instance == null) {
            m_instance = new ADGyro();
        }

        return m_instance;
    }

    public void initDrivebaseSimulation(IDifferentialDrivebase drivebase) {
        m_simDevice = SimDevice.create("ADGyro-Sim");

        if (m_simDevice != null) {
            m_simAngle = m_simDevice.createDouble("Angle", true, 0.0);
            m_simRate = m_simDevice.createDouble("Rate", true, 0.0);
            m_simDrivebase = drivebase;

            // Create and start a simulation thread
            m_simThread = new Notifier(this::updateSimData);
            m_simThread.startPeriodic(SIMULATION_PERIOD);

        }
    }

    private void updateSimData() {

        // Ensure sim is running
        if (m_simDevice != null) {

            // Get drivebase sensor readings
            double leftReading = m_simDrivebase.getLeftMeters();
            double rightReading = m_simDrivebase.getRightMeters();

            // Determine change from last reading
            double leftDiff = leftReading - m_simSensorReadings[0];
            double rightDiff = rightReading - m_simSensorReadings[1];

            // Calculate angle
            double omega = ((leftDiff - rightDiff) / m_simDrivebase.getWidthMeters() * ROTATION_SPEED_GAIN);

            // Set last readings
            m_simSensorReadings[0] = leftReading;
            m_simSensorReadings[1] = rightReading;

            // Publish readings
            m_simAngle.set(m_simAngle.get() + omega);
            m_simRate.set(omega);
        }

    }

    /**
     * Set if the NavX readings should be inverted
     * 
     * @param inverted Is NavX inverted?
     */
    public void setInverted(boolean inverted) {
        this.inverted = inverted;
    }

    @Override
    public double getAngle() {

        if (m_simDevice != null) {
            return m_simAngle.get();
        }

        return super.getAngle();
    }

    /**
     * Returns the heading of the robot.
     *
     * @return the robot's heading in degrees, from 180 to 180
     */
    public double getHeading() {
        return Math.IEEEremainder(getAngle(), 360) * (inverted ? -1.0 : 1.0);
    }

    @Override
    public double getRate() {

        if (m_simAngle != null) {
            return m_simRate.get() * (inverted ? -1.0 : 1.0);
        }

        return super.getRate() * (inverted ? -1.0 : 1.0);
    }

    /**
     * Get the gyro angle, wrapped by 360 degrees
     * 
     * @return Wrapped angle
     */
    public double getWrappedAngle() {
        return getAngle() % 360;
    }

    /**
     * Get the NavX heading as a Rotation2d object
     * 
     * @return Heading
     */
    public Rotation2d getRotation() {
        return Rotation2d.fromDegrees(getHeading());
    }
}