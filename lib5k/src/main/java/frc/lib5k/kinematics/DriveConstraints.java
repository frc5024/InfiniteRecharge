package frc.lib5k.kinematics;

/**
 * Used to set bounds for robot movement
 */
public class DriveConstraints {
    double minV, maxV, maxTurn;

    public DriveConstraints(double minVelocity, double maxVelocity) {
        this(minVelocity, maxVelocity, 1);
    }

    /**
     * Robot movement constraints
     * 
     * @param minVelocity Minimum velocity (this should usually be 0)
     * @param maxVelocity Maximum velocity
     * @param maxTurn Maximum turn rate
     */
    public DriveConstraints(double minVelocity, double maxVelocity, double maxTurn) {
        this.minV = minVelocity;
        this.maxV = maxVelocity;
        this.maxTurn = maxTurn;
    }

    public double getMaxVel() {
        return maxV;
    }

    public double getMinVel() {
        return minV;
    }

    public double getMaxTurn() {
        return maxTurn;
    }

    public void setMaxVel(double max) {
        this.maxV = max;
    }

    public void setMinVel(double min) {
        this.minV = min;
    }

    public void setMaxTurn(double turn) {
        this.maxTurn = turn;
    }

}