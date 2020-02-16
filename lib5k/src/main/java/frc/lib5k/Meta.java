package frc.lib5k;

public class Meta {
    public static void startLib5K() {
        // Create the UnifiedLooper
        UnifiedLooper lib5k_looper = new UnifiedLooper();

        // Start the looper
        lib5k_looper.start(0.02);
    }
}