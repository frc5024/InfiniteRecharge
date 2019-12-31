package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.lib5k.roborio.FaultReporter;
import frc.lib5k.utils.RobotLogger;
import frc.robot.subsystems.DriveTrain;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

	/* Robot I/O helpers */
	RobotLogger logger = RobotLogger.getInstance();
	FaultReporter m_faultReporter = FaultReporter.getInstance();

	/* Robot Subsystems */
	private DriveTrain m_drivetrain = DriveTrain.getInstance();

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {

		// Register all subsystems
		m_drivetrain.register(); // TODO: Replace with default command
	}

	@Override
	public void autonomousInit() {
	}

	@Override
	public void autonomousPeriodic() {
	}

	@Override
	public void teleopInit() {
	}

	@Override
	public void teleopPeriodic() {
	}

}
