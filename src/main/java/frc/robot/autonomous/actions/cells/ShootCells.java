package frc.robot.autonomous.actions.cells;

import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.utils.RobotLogger;
import frc.robot.subsystems.CellSuperstructure;
import frc.robot.subsystems.cellmech.Shooter;
import frc.robot.subsystems.cellmech.Hopper;

/** Command to shoot an amount of cells */
public class ShootCells extends CommandBase {

    /** Instance of the cell superstructure */
    private CellSuperstructure m_cellSuperstructure = CellSuperstructure.getInstance();

    /** Instance of Shooter */
    private Shooter m_shooter = Shooter.getInstance();

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
        // if(m_shooter.isInPosition()){
        RobotLogger.getInstance().log("ShootCells", String.format("Shooting %d cells", m_shootAmount));
        m_cellSuperstructure.shootCells(m_shootAmount);
        // }
    }

    @Override
    public void end(boolean interrupted) {
        RobotLogger.getInstance().log("ShootCells",
                String.format("Shoot action %s", (interrupted) ? "interrupted" : "ended"));
        m_cellSuperstructure.stop();
        Hopper.getInstance().stop();

        // Handle simulation
        if (interrupted && RobotBase.isSimulation()) {
            Hopper.getInstance().forceCellCount(Math.max(Hopper.getInstance().getCellCount() - m_shootAmount, 0));
        }
    }

    @Override
    public boolean isFinished() {
        return m_cellSuperstructure.isDone();
    }
}