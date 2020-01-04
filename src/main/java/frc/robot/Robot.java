package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.lib5k.components.gyroscopes.NavX;
import frc.lib5k.roborio.FaultReporter;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.robot.commands.DriveControl;
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
	private DriveTrain m_driveTrain = DriveTrain.getInstance();

	/* Robot Commands */
	private DriveControl m_driveControl;

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {

		// Create control commands
		logger.log("Robot", "Constructing Commands", Level.kRobot);
		m_driveControl = new DriveControl();

		// Register all subsystems
		logger.log("Robot", "Registering Subsystems", Level.kRobot);

		m_driveTrain.setDefaultCommand(m_driveControl);

		// Start the logger
		logger.start(0.02);

		// Reset & calibrate the robot gyroscope
		NavX.getInstance().reset();
	}

	/**
	 * Code to be called on both autonomous and teleop init
	 */
	private void sharedInit() {

		// Enable brakes on the DriveTrain
		m_driveTrain.setBrakes(true);
	}

	@Override
	public void autonomousInit() {
		logger.log("Robot", "Autonomous started");

		// Run shared init code
		sharedInit();
	}

	@Override
	public void autonomousPeriodic() {

		// Run all scheduled WPILib commands
		CommandScheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		logger.log("Robot", "Teleop started");

		// Run shared init code
		sharedInit();
	}

	@Override
	public void teleopPeriodic() {

		// Run all scheduled WPILib commands
		CommandScheduler.getInstance().run();
	}

	@Override
	public void disabledInit() {
		logger.log("Robot", "Robot disabled");

		// Disable brakes on the DriveTrain
		m_driveTrain.setBrakes(false);

	}

	@Override
	public void disabledPeriodic() {

		// Run all scheduled WPILib commands
		CommandScheduler.getInstance().run();
	}
}
