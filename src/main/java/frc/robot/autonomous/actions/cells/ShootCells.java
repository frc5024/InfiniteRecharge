package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.CellSuperstructure;

/** Command to shoot an amount of cells */
public class ShootCells extends CommandBase {

    /** Instance of the cell superstructure */
    private CellSuperstructure m_cellSuperstructure = CellSuperstructure.getInstance();

    /** Amount of cells to try to shoot */
    private int m_shootAmount = 5;

    public ShootCells() {
        this(5);
    }
    public ShootCells(int cellCount) {
        m_shootAmount = cellCount;
    }

    @Override
    public void initialize() {
        m_cellSuperstructure.shootCells(m_shootAmount);
    }

    @Override
    public boolean isFinished() {
        return m_cellSuperstructure.isDone();
    }
}