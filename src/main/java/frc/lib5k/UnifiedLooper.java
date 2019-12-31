package frc.lib5k;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Notifier;

public class UnifiedLooper {

    public static class Component {
        String name;
        Runnable run;

        public Component(String name, Runnable run) {
            this.name = name;
            this.run = run;
        }
    }

    // List to contain every update method
    private static ArrayList<Component> m_updateMethods = new ArrayList<Component>();

    // Thread
    Notifier m_notifier;

    public UnifiedLooper() {
        m_notifier = new Notifier(this::update);
    }

    public void start(double period) {
        m_notifier.startPeriodic(period);
    }

    /**
     * Register a library component to run in the looper
     * 
     * @param run Runnable update method
     */
    protected static void register(String name, Runnable run) {
        m_updateMethods.add(new Component(name, run));
    }

    private void update() {
        // Update every registered component
        for (Component cpnt : m_updateMethods) {
            // Try to run component
            try {
                cpnt.run.run();
            } catch (Exception e) {
                System.out.println("ERROR: Failed to run component: " + cpnt.name + ".\n" + e);
            }
        }
    }
}