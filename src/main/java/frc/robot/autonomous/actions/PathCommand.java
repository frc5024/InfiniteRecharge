package frc.robot.autonomous.actions;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.lib5k.kinematics.purepursuit.Path;

public class PathCommand extends CommandBase {

    private Path m_path;

    public PathCommand(Path path) {
        this.m_path = path;

    }

    @Override
    public void execute() {

        // Find the closest point to rbt + R
        for (Pose2d pose : m_path.getPoses()) {

            // Determine pose distance from robot
            
        }
    }
}