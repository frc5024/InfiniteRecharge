package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.utils.RobotLogger;
import frc.robot.subsystems.CellSuperstructure;

/** Command to intake an amount of cells */
public class IntakeCells extends CommandBase {

    /** Instance of the cell superstructure */
    private CellSuperstructure m_cellSuperstructure = CellSuperstructure.getInstance();

    /** Amount of cells to try to intake */
    private int m_intakeAmount;

    public IntakeCells() {
        this(5);
    }

    public IntakeCells(int cellCount) {
        m_intakeAmount = cellCount;
    }

    @Override
    public void initialize() {
        RobotLogger.getInstance().log("IntakeCells", String.format("Intaking %d cells", m_intakeAmount));
        m_cellSuperstructure.intakeCells(m_intakeAmount);
    }

    @Override
    public void execute() {

    }

    @Override
    public void end(boolean interrupted) {
        RobotLogger.getInstance().log("IntakeCells",
                String.format("Intake action %s", (interrupted) ? "interrupted" : "ended"));
        m_cellSuperstructure.stop();
    }

    @Override
    public boolean isFinished() {
        return m_cellSuperstructure.isDone() && !RobotBase.isSimulation();
    }
}