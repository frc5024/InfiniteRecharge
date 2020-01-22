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

    // Path points
    private ArrayList<Pose2d> m_points;

    /**
     * Create a motion path from points
     * 
     * @param waypoints Path waypoints to follow
     */
    public Path(Pose2d... waypoints) {
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

    /**
     * Smooth out a path with a smoothing factor
     * 
     * @return This object (for use in builder syntax)
     */
    public Path smooth() {
        return smooth(DEFAULT_SMOOTHNESS);
    }

    /**
     * Smooth out a path with a smoothing factor
     * 
     * @param smoothness Path smoothness (Should be between 0 and 1)
     * @return This object (for use in builder syntax)
     */
    public Path smooth(double smoothness) {

        // TODO: implement smoothing

        return this;
    }

    /**
     * Get array of poses
     * 
     * @return Poses
     */
    public Pose2d[] getPoses() {
        return m_points.toArray(new Pose2d[m_points.size()]);
    }
}