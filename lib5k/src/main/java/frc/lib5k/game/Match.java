package frc.lib5k.game;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.DriverStation.MatchType;

/**
 * Methods for reading Match data
 */
public class Match {

    /**
     * Get the game specific message (data sent by FMS about current match)
     * 
     * @return Current GSM
     */
    public static String getGSM() {
        return DriverStation.getInstance().getGameSpecificMessage();
    }

    /**
     * Get an estimation of the time remaining in the current match (current mode)
     * 
     * @return Estimated time left in match
     */
    public static double getMatchTime() {
        return DriverStation.getInstance().getMatchTime();
    }

    /**
     * Check if the robot is currently on a field
     * 
     * @return Is the robot on a field
     */
    public static boolean isOfficialMatch() {
        return DriverStation.getInstance().isFMSAttached();
    }

    /**
     * Get the FRC event name
     * 
     * @return Event name
     */
    public static String getEventName() {
        return DriverStation.getInstance().getEventName();
    }

    /**
     * Get the match type
     * 
     * @return Match type
     */
    public static MatchType getMatchType() {
        return DriverStation.getInstance().getMatchType();
    }

    /**
     * Get the current match number
     * 
     * @return Current match number
     */
    public static int getMatchNumber() {
        return DriverStation.getInstance().getMatchNumber();
    }

    /**
     * Get the current match replay number
     * 
     * @return Match replay number
     */
    public static int getReplayNumber() {
        return DriverStation.getInstance().getReplayNumber();
    }

    /**
     * Get the current alliance
     * 
     * @return Current alliance
     */
    public static Alliance getAlliance() {
        return DriverStation.getInstance().getAlliance();
    }

    /**
     * Get the alliance station ID of the humans (where we are set up on the field)
     * 
     * @return Alliance station ID
     */
    public static int getAllianceStationID() {
        return DriverStation.getInstance().getLocation();
    }
}