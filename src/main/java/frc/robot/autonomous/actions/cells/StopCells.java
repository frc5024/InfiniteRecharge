package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.CellSuperstructure;

/** Command to stop shooting or intaking */
public class StopCells extends CommandBase {

    private CellSuperstructure m_cellSuperstructure = CellSuperstructure.getInstance();

    public StopCells() {

    }

    @Override
    public void initialize() {
        m_cellSuperstructure.stop();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}