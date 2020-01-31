package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.CellSuperstructure;

public class UnjamCells extends CommandBase {

    private CellSuperstructure m_cellSuperstructure = CellSuperstructure.getInstance();

    public UnjamCells() {

    }

    @Override
    public void initialize() {
        m_cellSuperstructure.unjam();
    }
}