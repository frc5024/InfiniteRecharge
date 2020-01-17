package frc.lib5k.simulation.fieldsim;

import edu.wpi.first.networktables.NetworkTableEntry;

public class FieldSim {

    private static FieldSim s_instance = null;

    /* Data entries */
    NetworkTableEntry m_robotPose;
    NetworkTableEntry m_closestTarget;

    private FieldSim() {

    }

    public static FieldSim getInstance() {
        if (s_instance == null) {
            s_instance = new FieldSim();
        }

        return s_instance;
    }
}