package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;

/**
 * Test code for the intake/shooter superstructure. This will be replaced soon
 */
public class OperatorControl extends CommandBase {

    private IntakeCells m_intakeCellsCommand = new IntakeCells(5);
    private ShootCells m_shootCellsCommand = new ShootCells(5);

    private OI m_oi = OI.getInstance();

    

    @Override
    public void execute() {

        if(m_oi.shouldRunAutoShoot()) {
            m_shootCellsCommand.schedule();
        } else {
            m_shootCellsCommand.cancel();
        }

        if(m_oi.shouldRunAutoIntake()) {
            m_intakeCellsCommand.schedule();
        } else {
            m_intakeCellsCommand.cancel();
        }
        
    }

}