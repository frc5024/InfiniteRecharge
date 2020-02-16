package frc.lib5k.kinematics.motionprofiling;

public class MotionConstraints {
    private double max_vel, max_jerk, max_acc;

    /**
     * Create a MotionConstraints object
     * 
     * @param max_velocity     Maximum velocity in m/s
     * @param max_acceleration Maximum acceleration in m/s/s
     * @param max_jerk         Maximum jerk in m/s/s/s (A good starting point is 60)
     */
    public MotionConstraints(double max_velocity, double max_acceleration, double max_jerk) {
        this.max_vel = (max_velocity);
        this.max_acc = (max_acceleration);
        this.max_jerk = (max_jerk);
    }

    public double getMaxAccel() {
        return max_acc;
    }

    public void setMaxAccel(double max_acc) {
        this.max_acc = max_acc;
    }

    public double getMaxJerk() {
        return max_jerk;
    }

    public void setMaxJerk(double max_jerk) {
        this.max_jerk = max_jerk;
    }

    public double getMaxVelocity() {
        return max_vel;
    }

    public void setMaxVelocity(double max_vel) {
        this.max_vel = max_vel;
    }
}