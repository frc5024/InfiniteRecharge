package frc.lib5k.kinematics.purepursuit;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.util.Units;

public class Path {

    // Default path smoothing factor
    private static final double DEFAULT_SMOOTHNESS = 0.75;

    // Amount of spacing between points during the "fill in" operation
    private static final double POINT_SPACING = Units.inchesToMeters(6.0);

    // Path smoothness
    private double m_smoothness;

    // Path points
    private ArrayList<Pose2d> m_points;

    public Path(Pose2d... waypoints) {
        this(DEFAULT_SMOOTHNESS, waypoints);
    }

    /**
     * Create a motion path from points
     * 
     * @param smoothness Path smoothness (Should be between 0 and 1)
     * @param waypoints  Path waypoints to follow
     */
    public Path(double smoothness, Pose2d... waypoints) {
        this.m_smoothness = smoothness;
        this.m_points = new ArrayList<>();

        // Fill in extra points
        int i = 0;
        for (; i < waypoints.length - 1; i++) {

            // Get the start and end translations
            Translation2d startTrans = waypoints[i].getTranslation();
            Translation2d endTrans = waypoints[i + 1].getTranslation();

            // Create a new Translation of the difference between poses
            Translation2d vector = new Translation2d(endTrans.getX() - startTrans.getX(),
                    endTrans.getY() - startTrans.getY());

            // Determine the number of points we can fit between the two poses
            double innerCount = Math.ceil(Math.hypot(vector.getX(), vector.getY()) / POINT_SPACING);

            // Normalize the vector
            double ilen = (1.0 / Math.sqrt((vector.getX() * vector.getX()) + (vector.getY() * vector.getY())));
            vector = new Translation2d(vector.getX() * ilen, vector.getY() * ilen);

            // Add inner points
            for (int j = 0; j < innerCount; j++) {
                m_points.add(new Pose2d(new Translation2d((startTrans.getX() + (vector.getX() * j)),
                        (startTrans.getY() + (vector.getY() * j))), waypoints[i].getRotation()));
            }
        }

        // Add the last point to the points list
        m_points.add(waypoints[i + 1]);
    }
}