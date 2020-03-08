package frc.robot.autonomous.paths.score;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.lib5k.utils.RobotLogger;
import frc.robot.autonomous.AutonomousStartpoints;
import frc.robot.autonomous.actions.TurnToCommand;
import frc.robot.autonomous.actions.cells.ShootCells;
import frc.robot.autonomous.paths.AutonomousPath;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.cellmech.Hopper;
import frc.robot.subsystems.cellmech.Intake;

public class BuddyScore extends AutonomousPath {

    @Override
    protected SequentialCommandGroup getCommand() {

        // Create a modified commandgroup that forces our ball counter up
        SequentialCommandGroup output = new SequentialCommandGroup(){
            @Override
            public void execute() {
                // Run internal command handler
                super.execute();

                // Force-set out ball counter
                Hopper.getInstance().forceCellCount(3);
            }
        };

        double shootTimeout = 10.0;
        double pushTimeout = 2.0;
        double runTimeout = 3.0;

        // Turn to startpoint
        output.addCommands(new TurnToCommand(getStartingPose().getRotation()));

        // Add a timeout command for shooting
        ParallelRaceGroup shootCommand = new ShootCells(5).withTimeout(shootTimeout);

        // Add a timeout command for "fake intaking"
        ParallelRaceGroup intakeCommand = new CommandBase() {

            @Override
            public void initialize() {
                RobotLogger.getInstance().log("BuddyScore", "Dropping intake");
                Intake.getInstance().intake();
            }
            
            @Override
            public void end(boolean x) {
                RobotLogger.getInstance().log("BuddyScore", "Raising intake");
                Intake.getInstance().stow();
                
            }
        }.withTimeout(shootTimeout - 0.5);

        // Combine both commands
        output.addCommands(new ParallelCommandGroup(shootCommand, intakeCommand).withTimeout(shootTimeout + 0.1));

        // Push our buddy off the line
        output.addCommands(new CommandBase() {
            @Override
            public void initialize() {
                RobotLogger.getInstance().log("BuddyScore", "Pushing buddy");
                DriveTrain.getInstance().drive(4.0, 0.0);
            }
            
            @Override
            public void end(boolean x) {
                RobotLogger.getInstance().log("BuddyScore", "Stopped pushing");
                DriveTrain.getInstance().stop();
                
            }
        }.withTimeout(pushTimeout));

        // Get off the line ourselves
        output.addCommands(new CommandBase() {
            @Override
            public void initialize() {
                RobotLogger.getInstance().log("BuddyScore", "Pushing buddy");
                DriveTrain.getInstance().drive(-2.0, 0.0);
            }
            
            @Override
            public void end(boolean x) {
                RobotLogger.getInstance().log("BuddyScore", "Stopped pushing");
                DriveTrain.getInstance().stop();
                
            }
        }.withTimeout(runTimeout));
        
        return output;
    }

    @Override
    public Pose2d getStartingPose() {
        return new Pose2d(AutonomousStartpoints.SECTOR_LINE_INFRONT_OF_GOAL, Rotation2d.fromDegrees(-180));
    }
    
}