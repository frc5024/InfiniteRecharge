package frc.robot.autonomous.actions.subsystemActions;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.CellSuperstructure;
import frc.robot.subsystems.cellmech.Hopper;

public class Intake extends CommandBase {

    private int m_count;
    private int m_target;

    public Intake() {
        this(5);
    }
    public Intake(int cellCount) {
        m_count = cellCount;
        m_target = Hopper.getInstance().getCellCount() + cellCount;
    }

    @Override
    public void initialize() {
        CellSuperstructure.getInstance().intakeStuff(m_count);
    }

    @Override
    public void execute() {
        
    }

    @Override
    public void end(boolean interrupted) {
        CellSuperstructure.getInstance().stopStuff();
    }

    @Override
    public boolean isFinished() {
        return (Hopper.getInstance().getCellCount() >= m_target);
    }
}