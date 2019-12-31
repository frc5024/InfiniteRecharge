package frc.lib5k.loops.loopers;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.lib5k.loops.loopables.LoopableSubsystem;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;

/**
 * A looper for LoopableSubsystems. 
 */
public class SubsystemLooper extends Looper {
    private RobotLogger logger = RobotLogger.getInstance();

    ArrayList<LoopableSubsystem> subsystems = new ArrayList<LoopableSubsystem>();

    double dt;

    public SubsystemLooper() {
        logger.log("[Subsystem Looper] Constructing", Level.kRobot);
    }

    /**
     * Register a LoopableSubsystem with the looper
     * 
     * @param subsystem LoopableSubsystem to register with the looper
     */
    public void register(LoopableSubsystem subsystem) {
        subsystems.add(subsystem);
        logger.log("[Subsystem Looper] Registered " + subsystem.name, Level.kRobot);
    }

    /**
     * Intarface for registering multiple subsystems
     */
    public void registerAll(LoopableSubsystem ... subsystems ){
        for (LoopableSubsystem subsystem : subsystems) {
            register(subsystem);
        }
    }

    @Override
    /**
     * Execute the periodic functions for each subsystem
     *  
     */
    public void update() {
        double inputTime = 0;

        // Run and check total time for inputs
        for (LoopableSubsystem subsystem : subsystems) {

            double start = Timer.getFPGATimestamp();
            subsystem.last_timestamp = start;

            // Run the function
            try {
                subsystem.periodicInput();
            } catch (Exception e) {
                logger.log("[Subsystem Looper] A registered subsystem failed to execute");
                logger.log("" + e);
            }

            // Return execution time
            inputTime += Timer.getFPGATimestamp() - start;

        }

        if (inputTime > (period / 2)) {
            logger.log("[SubsystemLooper] Subsystem inputs are using more than half of the allotted looper time",
                    Level.kWarning);
        }

        double outputTime = 0;

        // Run and check total time for outputs
        for (LoopableSubsystem subsystem : subsystems) {

            double start = Timer.getFPGATimestamp();
            subsystem.last_timestamp = start;

            // Run the function
            try {
                subsystem.periodicOutput();
            } catch (Exception e) {
                logger.log("[Subsystem Looper] A registered subsystem failed to execute");
                logger.log("" + e);
            }

            // Return execution time
            outputTime += Timer.getFPGATimestamp() - start;

        }



        // Calculate dt
        this.dt = inputTime + outputTime;
    }

    /**
     * Outputs telemetry data from the Looper and all subsystems to SmartDashboard
     */
    public void outputTelemetry() {
        SmartDashboard.putNumber("SubsystemLooper DT", dt);

        for (LoopableSubsystem subsystem : subsystems) {
            subsystem.outputTelemetry();
        }
    }
}