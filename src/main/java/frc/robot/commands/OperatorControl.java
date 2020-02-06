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
        if (m_oi.shouldShoot()) {

            // Start up the shooter command
            m_shootCellsCommand.schedule();

            // Kill the intake command
            m_intakeCellsCommand.cancel();

            // Disable the intake toggle
            m_oi.resetIntakeInput();
        } else {
            m_shootCellsCommand.cancel();
        }

        // Toggle for intaking
        if (m_oi.shouldIntake()) {

            // Start up the intake command
            m_intakeCellsCommand.schedule();

            // Kill the shooting command (intake gets priority)
            m_shootCellsCommand.cancel();

            // Disable the shooter toggle
            m_oi.resetShooterInput();
        } else {
            m_intakeCellsCommand.cancel();
        }

    }

}