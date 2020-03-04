package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.AnalyticsEngine;
import frc.robot.AnalyticsEngine.AnalyticEvent;
import frc.robot.subsystems.CellSuperstructure;

/** Command to unjam cells */
public class UnjamCells extends CommandBase {

    private CellSuperstructure m_cellSuperstructure = CellSuperstructure.getInstance();

    public UnjamCells() {

    }

    @Override
    public void initialize() {
        m_cellSuperstructure.unjam();
        AnalyticsEngine.trackEvent(AnalyticEvent.BALL_UNJAM);
    }

    @Override
    public void end(boolean interrupted) {
        m_cellSuperstructure.stop();
    }
}