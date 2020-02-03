package frc.lib5k.kinematics.purepursuit;

import java.util.ArrayList;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.util.Units;

public class Path {

    // Amount of spacing between points during the "fill in" operation
    private static final double POINT_SPACING = Units.inchesToMeters(6.0);

    // Path points
    private ArrayList<Translation2d> m_points;

    /**
     * Create a motion path from points
     * 
     * @param smoothness Path smoothness (Should be between 0 and 1)
     * @param waypoints  Path waypoints to follow
     */
    public Path(Translation2d... waypoints) {
        this.m_points = new ArrayList<>();

        // Fill in extra points
        int i = 0;
        for (; i < waypoints.length - 1; i++) {

            // Get the start and end translations
            Translation2d startTrans = waypoints[i];
            Translation2d endTrans = waypoints[i + 1];

            // Create a new Translation of the difference between poses
            Translation2d vector = new Translation2d(endTrans.getX() - startTrans.getX(),
                    endTrans.getY() - startTrans.getY());

            // Determine the number of points we can fit between the two poses
            double innerCount = Math.ceil(Math.hypot(vector.getX(), vector.getY()) / POINT_SPACING);

            // Find the interior length
            double ilen = (1.0 / Math.sqrt((vector.getX() * vector.getX()) + (vector.getY() * vector.getY())));
            vector = new Translation2d(vector.getX() * ilen, vector.getY() * ilen);

            // Add inner points
            for (int j = 0; j < innerCount; j++) {
                m_points.add(new Translation2d((startTrans.getX() + (vector.getX() * j)),
                        (startTrans.getY() + (vector.getY() * j))));
            }
        }

        // Add the last point to the points list
        m_points.add(waypoints[i]);
    }

    public Translation2d[] getPoses() {
        return m_points.toArray(new Translation2d[m_points.size()]);
    }

}