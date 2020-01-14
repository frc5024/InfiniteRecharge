package frc.robot;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.lib5k.components.drive.IDifferentialDrivebase;
import frc.lib5k.components.gyroscopes.NavX;
import frc.lib5k.roborio.FaultReporter;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;
import frc.robot.autonomous.Chooser;
import frc.robot.commands.DriveControl;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.PanelManipulator;
import frc.robot.subsystems.Shooter;

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
	private Intake m_intake = Intake.getInstance();
	private Climber m_climber = Climber.getInstance();
	private PanelManipulator m_Manipulator = PanelManipulator.getInstance();
	private Shooter m_shooter = Shooter.getInstance();

	/* Robot Commands */
	private CommandBase m_autonomousCommand;
	private DriveControl m_driveControl;

	private Chooser m_autonChooser;

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
		m_intake.register();
		m_climber.register();
		m_shooter.register();

		m_Manipulator.register();
		// Start the logger
		logger.start(0.02);

		// Reset & calibrate the robot gyroscope
		NavX.getInstance().reset();
		NavX.getInstance().setInverted(false);

		// Reset the drivetrain pose
		// m_driveTrain.setPosition(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));
		m_driveTrain.setRampRate(0.12);

		// Create and publish an autonomous chooser
		m_autonChooser = new Chooser();
		m_autonChooser.publishOptions();

		// Simulate main gyroscope
		if (RobotBase.isSimulation()) {
			logger.log("Robot", "Starting NavX simulation");
			NavX.getInstance().initDrivebaseSimulation((IDifferentialDrivebase) m_driveTrain);
		}

		m_driveTrain.setPosition(m_autonChooser.getRobotAutoStartPosition());
	}

	@Override
	public void robotPeriodic() {

		// Publish telemetry data to smartdashboard if setting enabled
		if (RobotConstants.PUBLISH_SD_TELEMETRY) {
			m_driveTrain.updateTelemetry();
		}

	}

	@Override
	public void autonomousInit() {
		logger.log("Robot", "Autonomous started");

		// Determine correct autonomous command to run
		m_autonomousCommand = m_autonChooser.generateAutonomousCommand();

		// Try to start the command
		if (m_autonomousCommand != null) {
			m_autonomousCommand.schedule();
		} else {
			logger.log("Robot", "Failed to start autonomous command, was null!", Level.kWarning);
		}

		// Determine robot starting position
		// m_driveTrain.setPosition(new Pose2d(0.0, 0.0, Rotation2d.fromDegrees(0.0)));
		// NavX.getInstance().reset();
		m_driveTrain.setPosition(m_autonChooser.getRobotAutoStartPosition());

		// Enable brakes on the DriveTrain
		m_driveTrain.setBrakes(true);
	}

	@Override
	public void autonomousPeriodic() {

		// Run all scheduled WPILib commands
		CommandScheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		logger.log("Robot", "Teleop started");

		// Enable brakes on the DriveTrain
		m_driveTrain.setBrakes(true);

		// Disable the autonomous command
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}

		// Start the teleop commands
		if (m_driveControl != null) {
			m_driveControl.schedule();
		}

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
		m_driveTrain.stop();

	}

	@Override
	public void disabledPeriodic() {

		// Run all scheduled WPILib commands
		CommandScheduler.getInstance().run();
	}

}
