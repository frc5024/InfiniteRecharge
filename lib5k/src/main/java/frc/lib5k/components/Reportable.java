package frc.lib5k.components;

import frc.lib5k.utils.RobotLogger;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;

public class Reportable {
    protected RobotLogger logger = RobotLogger.getInstance();

    protected void reportHALDevice(tResourceType type, int id, String name) {
        // Arg 1 should be type
        HAL.report(0, id, 0, name);
    }

}