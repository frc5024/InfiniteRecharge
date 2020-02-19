package frc.robot.commands;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.OI;
import frc.robot.autonomous.actions.cells.IntakeCells;
import frc.robot.autonomous.actions.cells.ShootCells;
import frc.robot.autonomous.actions.cells.UnjamCells;
import frc.robot.subsystems.CellSuperstructure;
import frc.robot.commands.actions.controlpanel.PositionPanel;
import frc.robot.commands.actions.controlpanel.RotatePanel;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.cellmech.Hopper;

/**
 * Test code for the intake/shooter superstructure. This will be replaced soon
 */
public class OperatorControl extends CommandBase {

    /** sub-commands */
    private IntakeCells m_intakeCellsCommand = new IntakeCells(5);
    private ShootCells m_shootCellsCommand = new ShootCells(5);
    private UnjamCells m_unjamCommend = new UnjamCells();
    private ClimbController m_climbController = new ClimbController();
    private PositionPanel m_positionCommand = new PositionPanel(new Translation2d(5, 5));
    private RotatePanel m_rotateCommand = new RotatePanel(4, new Translation2d(5, 5));

    /** Instance of OI */
    private OI m_oi = OI.getInstance();

    /** instance of hopper */
    private Hopper m_hopper = Hopper.getInstance();
    private CellSuperstructure m_cellStructure = CellSuperstructure.getInstance();

    @Override
    public void initialize() {

        // Lock the climber
        Climber.getInstance().lock();
    }

    @Override
    public void execute() {

        // Toggle for shooting
        if (m_oi.shouldShoot()) {

            // Start up the shooter command
            m_shootCellsCommand.schedule();

            // Kill the intake command
            m_intakeCellsCommand.cancel();
            m_unjamCommend.cancel();

            // Disable the intake toggle
            m_oi.resetIntakeInput();

            // Stop the bot from unjamming
            m_oi.resetUnjamInput();
        } else {
            m_shootCellsCommand.cancel();
        }

        // Toggle for intaking
        if (m_oi.shouldIntake()) {

            // Start up the intake command
            m_intakeCellsCommand.schedule();

            // Kill the shooting command (intake gets priority)
            m_shootCellsCommand.cancel();
            m_unjamCommend.cancel();

            // Stop the bot from unjamming
            m_oi.resetUnjamInput();

            // When the cell capacity is reached, ruble the operator controller
            if(m_cellStructure.isDoneIntake()) {
                m_hopper.startRumble();
            }

        } else {
            m_intakeCellsCommand.cancel();
        }

        // Pull the pin on the climber if climb started
        if (m_oi.shouldEjectClimber()) {
            m_climbController.schedule();
        } else if (m_oi.shouldCancelClimb()) {
            m_climbController.cancel();
        }

        // Check if the cell counter should be reset
        if (m_oi.shouldResetCellCount()) {
            Hopper.getInstance().forceCellCount(0);
        }

        if (m_oi.shouldUnjam()) {

            // Stop any other action
            m_oi.resetIntakeInput();

            // Start the un-jammer
            m_unjamCommend.schedule();

        } else {
            m_unjamCommend.cancel();
        }

        if(m_oi.shouldRotate()) {
            m_rotateCommand.schedule();
            m_positionCommand.cancel();
        } 

        if(m_oi.shouldPosition()) {
            m_positionCommand.schedule();
            m_rotateCommand.cancel();
        }

        // else if(m_positionCommand.isFinished()) {
        //     m_positionCommand.cancel();
        // }
    }

    /**
     * Kill all commands
     */
    public void killAllActions() {
        m_intakeCellsCommand.cancel();
        m_shootCellsCommand.cancel();
        m_climbController.cancel();
        m_rotateCommand.cancel();
        m_positionCommand.cancel();
    }

}