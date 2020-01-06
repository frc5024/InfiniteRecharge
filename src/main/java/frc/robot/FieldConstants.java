package frc.robot;

import edu.wpi.first.wpilibj.util.Units;

public class FieldConstants {

    /**
     * Field length in meters
     */
    public static final double FIELD_LENGTH = Units.inchesToMeters(629.25);

    /**
     * Field width in meters
     */
    public static final double FIELD_WIDTH = Units.inchesToMeters(323.25);

    /**
     * Distance from your alliance wall to the opponent's sector line in meters
     */
    public static final double ALLIANCE_WALL_TO_FAR_SECTOR = (FIELD_LENGTH / 2) + Units.inchesToMeters(194.63);

    /**
     * Ball diameter in meters
     */
    public static final double BALL_DISMETER = Units.inchesToMeters(7.0);
}