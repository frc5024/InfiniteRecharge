package frc.lib5k.components.motors.motorutils;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.SpeedController;

/**
 * A helper class for watching for motor stalls
 */
public class StallDetector {

    /**
     * Mapping of motors to their stall current
     */
    public enum MotorStallCurrents {
        REV_NEO(105), MINI_CIM(89);

        public int stallCurrent;

        MotorStallCurrents(int x) {
            this.stallCurrent = x;
        }
    }

    // Motor SpeedController
    private SpeedController controller;

    // Motor type
    private MotorStallCurrents type;

    // PDP channel track
    private int pdpChannel;
    private PowerDistributionPanel pdp;

    // State tracker
    private boolean lastOutput = false;

    /**
     * Create a StallDetector
     * 
     * @param controller Motor's controller
     * @param motorType  Motor type
     * @param pdpModule  Power Distribution Panel CAN ID
     * @param pdpChannel PDP power channel of motor
     */
    public StallDetector(SpeedController controller, MotorStallCurrents motorType, int pdpModule, int pdpChannel) {
        // Set locals
        this.controller = controller;
        this.pdpChannel = pdpChannel;
        this.type = motorType;

        // Get a PDP instance
        this.pdp = new PowerDistributionPanel(pdpModule);

    }

    /**
     * Update and check for a motor stall
     * 
     * @return Has motor stalled?
     */
    public boolean update() {

        // Read the current draw of the motor channel
        double current = pdp.getCurrent(pdpChannel);

        // Check the PDP for excessive current draw
        return lastOutput = (current >= type.stallCurrent);
    }

    /**
     * Check for a motor stall without updating the checker
     * 
     * @return Has motor stalled?
     */
    public boolean isStalled() {
        return lastOutput;
    }
}