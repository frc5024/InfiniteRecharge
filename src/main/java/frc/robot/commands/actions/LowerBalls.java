package frc.robot.commands.actions;

import com.ctre.phoenix.Logger;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.utils.RobotLogger;
import frc.robot.OI;
import frc.robot.subsystems.CellSuperstructure;

public class LowerBalls extends CommandBase {

    // Operator interface for reading driver inputs
    private OI m_oi = OI.getInstance();
    private RobotLogger logger = RobotLogger.getInstance();
    private CellSuperstructure m_cellSuperstructure = CellSuperstructure.getInstance();

    @Override
    public void initialize() {
        logger.log("Hopper", "Lowering Balls");
        m_cellSuperstructure.moveToBottom();
        
    }

    @Override
    public void end(boolean interrupted) {
        logger.log("Hopper", "Finished lowering balss");
        m_oi.resetLower();
    }

    @Override
    public boolean isFinished() {
        return m_cellSuperstructure.isDone();
    }
}