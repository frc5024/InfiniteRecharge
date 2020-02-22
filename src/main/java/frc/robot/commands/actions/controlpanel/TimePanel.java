package frc.robot.commands.actions.controlpanel;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.utils.RobotLogger;
import frc.robot.subsystems.PanelManipulator;

/**
 * Move the control panel based on time
 */
public class TimePanel extends CommandBase {
    private RobotLogger logger = RobotLogger.getInstance();

    private double m_time = 0.0;

    /**
     * Spin the control panel for a set time
     * @param time Seconds to spin for
     * @param reversed Reverse rotation?
     */
    public TimePanel(double time, boolean reversed) {

        // Convert normal time to the subsystem time
        m_time = Math.abs(time) * ((reversed) ? -1 : 1);

        addRequirements(PanelManipulator.getInstance());

    }

    @Override
    public void initialize() {
        logger.log("TimePanel", String.format("Rotating for %.2f seconds", m_time));
        PanelManipulator.getInstance().rotateForTime(m_time);
    }

    @Override
    public void end(boolean interrupted) {
        logger.log("TimePanel", (interrupted) ? "Interrupted" : "Finished spinning");
        PanelManipulator.getInstance().stop();

    }

    @Override
    public boolean isFinished() {
        return PanelManipulator.getInstance().isIdle();

    }

}