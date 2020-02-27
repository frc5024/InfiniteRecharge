package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.control.CubicDeadband;
import frc.robot.OI;
import frc.robot.RobotConstants;
import frc.robot.commands.actions.FreeSpaceAutoAim;
import frc.robot.commands.actions.PivotAutoAim;
import frc.robot.subsystems.DriveTrain;

/**
 * Default command for controlling the robot drivebase with Xbox controller
 * inputs.
 */
public class DriveControl extends CommandBase {

    /** Operator interface object for reading driver inputs */
    private OI m_oi = OI.getInstance();

    /**
     * Deadband object for the "rotation" input. More info about deadbands can be
     * found at: https://en.wikipedia.org/wiki/Deadband
     */
    private CubicDeadband m_rotationDeadband = new CubicDeadband(
            RobotConstants.HumanInputs.Deadbands.ROTATION_INPUT_DEADBAND, 0.0);

    /** Alignment commands */
    private FreeSpaceAutoAim m_freeSpaceAim = new FreeSpaceAutoAim();
    private PivotAutoAim m_pivotAim = new PivotAutoAim();

    /**
     * DriveControl constructor
     */
    public DriveControl() {

        // Add the DriveTrain as a subsystem requirement
        addRequirements(DriveTrain.getInstance());
    }

    @Override
    public void execute() {

        // Handle auto-aim commands
        if (m_oi.shouldAutoAim()) {
            m_freeSpaceAim.schedule(true);
        } else if (m_oi.shouldPivotAim()) {
            m_pivotAim.schedule(true);
        }

        // Read driver inputs
        double speed = m_oi.getThrottle();
        double rotation = m_oi.getTurn();
        boolean invert = m_oi.isDriveInverted();

        // If the "invert" toggle is set, flip the speed value
        speed = (invert) ? speed * -1 : speed;

        // Deadband the rotation input to deal with low-quality Xbox joysticks
        rotation = m_rotationDeadband.feed(rotation);

        // Send control data to the DriveTrain
        DriveTrain.getInstance().drive(speed, rotation);
    }

    @Override
    public boolean isFinished() {
        // This is designed to be a "default command", so it must always return false
        return false;
    }

}