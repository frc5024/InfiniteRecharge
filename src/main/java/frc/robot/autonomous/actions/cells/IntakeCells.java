package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.CellSuperstructure;

public class IntakeCells extends CommandBase {

    private CellSuperstructure m_cellSuperstructure = CellSuperstructure.getInstance();

    private int m_intakeAmount = 5;

    public IntakeCells() {
        this(5);
    }
    public IntakeCells(int cellCount) {
        m_intakeAmount = cellCount;
    }

    @Override
    public void initialize() {
        m_cellSuperstructure.intakeCells(m_intakeAmount);
    }

    @Override
    public void execute() {
        
    }

    @Override
    public void end(boolean interrupted) {
        m_cellSuperstructure.stop();
    }

    @Override
    public boolean isFinished() {
        return m_cellSuperstructure.isDone();
    }
}