package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.lib5k.utils.RobotLogger;

/**
 * A command that will simply log a pre-set message to the console when run
 */
public class LogCommand extends InstantCommand {

    private String msg;

    /**
     * A command that will simply log a pre-set message to the console when run
     * 
     * @param msg Message to log
     */
    public LogCommand(String msg) {
        this.msg = msg;
    }

    @Override
    public void execute() {
        RobotLogger.getInstance().log(msg);
    }
}