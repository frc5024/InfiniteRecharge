package frc.lib5k.utils;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.util.WPILibVersion;
import frc.lib5k.roborio.RR_HAL;
import frc.lib5k.utils.RobotLogger.Level;

public class MOTD {
    private static RobotLogger logger = RobotLogger.getInstance();

    /**
     * Print an MOTD with some version info
     */
    public static void printFullMOTD() {
        String message = new StringBuilder().append("--- MOTD ---\n").append("Versions:\n")
                .append(String.format("\tWPILib: %s%n", WPILibVersion.Version))
                .append(String.format("\tLib5K: %s%n", RR_HAL.getLibraryVersion())).append(String
                        .format("\tFPGA: %d.%d%n", RobotController.getFPGAVersion(), RobotController.getFPGARevision()))
                .toString();

        logger.log(message, Level.kRobot);
    }
}