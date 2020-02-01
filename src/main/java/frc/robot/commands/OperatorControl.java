package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;

/**
 * Test code for the intake/shooter superstructure. This will be replaced soon
 */
public class OperatorControl extends CommandBase {

    /** Instance of intake command */
    private IntakeCells m_intakeCellsCommand = new IntakeCells(5);

    /** Instance of shoot command */
    private ShootCells m_shootCellsCommand = new ShootCells(5);

    /** Instance of OI */
    private OI m_oi = OI.getInstance();

    @Override
    public void execute() {

        // Toggle for shooting
        if(m_oi.shouldRunAutoShoot()) {
            m_shootCellsCommand.schedule();
        } else {
            m_shootCellsCommand.cancel();
        }

        // Toggle for intaking
        if(m_oi.shouldRunAutoIntake()) {
            m_intakeCellsCommand.schedule();
        } else {
            m_intakeCellsCommand.cancel();
        }
        
    }

}