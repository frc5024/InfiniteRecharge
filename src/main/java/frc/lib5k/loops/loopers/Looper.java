package frc.lib5k.loops.loopers;

import edu.wpi.first.wpilibj.Notifier;

/**
 * An abstract for anything that can be looped. Basically a wrapper for a Notifier
 */
public abstract class Looper {
    protected Notifier thread;
    protected double period;

    public Looper() {

        // Create the notifier
        thread = new Notifier(this::update);
    }

    public void start(double period) {
        this.period = period;
        this.thread.startPeriodic(period);
    }
    
    public void stop() {
        this.thread.stop();
    }

    protected abstract void update();
}