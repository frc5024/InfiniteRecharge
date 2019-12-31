package frc.lib5k.roborio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.util.WPILibVersion;

/**
 * Tools for messing with the HAL in questionable ways. Do not touch this unless
 * you understand the RoboRIO FPGA, the DriverStation protocol, and FRCNetComm.
 */
public class RR_HAL {

    /**
     * Report a custom programming language via FRCNetComm
     * 
     * @param language Language name
     */
    public static void reportLanguage(String language) {
        HAL.report(tResourceType.kResourceType_Language, 7, 0, language);
    }

    /**
     * Report a custom framework via FRCNetComm
     * 
     * @param framework_name Framework name
     */
    public static void reportFramework(String framework_name) {
        HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_ROS, 0, framework_name);
    }

    /**
     * Get the Lib5K version (wpi version + Lib5K identifier + FRC year)
     * 
     * @return Lib5K version
     */
    public static String getLibraryVersion() {

        // Read the WPILib version
        String wpi_version = WPILibVersion.Version;

        // Determine the FRC game year
        String frc_year;
        int version_index = wpi_version.indexOf(".");

        if (version_index != -1) {
            frc_year = wpi_version.substring(0, version_index);
        } else {
            frc_year = "unknown";
        }

        // Format output
        return String.format("%s (Lib5K %s)", wpi_version, frc_year);

    }

    /**
     * Report a custom FRC Version
     * 
     * @param language Robot programming language
     * @param version  FRC version
     */
    public static void reportFRCVersion(String language, String version) {

        // Ensure robot is real
        if (RobotBase.isReal()) {

            // Try to write to FRC Version file
            try {
                final File file = new File("/tmp/frc_versions/FRC_Lib_Version.ini");

                if (file.exists()) {
                    file.delete();
                }

                file.createNewFile();

                // Write version data
                try (OutputStream output = Files.newOutputStream(file.toPath())) {

                    // Write language name
                    output.write(language.getBytes(StandardCharsets.UTF_8));

                    // Write library version
                    output.write(version.getBytes(StandardCharsets.UTF_8));
                }

            } catch (IOException ex) {
                DriverStation.reportError("[Lib5K] Could not write FRC_Lib_Version.ini: " + ex.toString(),
                        ex.getStackTrace());
            }
        } else {
            System.out.println("Cannot write custom FRC Version in a simulation");
        }
    }

    /**
     * Returns the name of the robot
     * if the name cannot be found, returns Unknown Robot
     * @return robot name
     */
    public static String getRobotName() {

        // Try to write to FRC Version file
        try {
            final File file = new File("/home/lvuser/robotName");

            FileReader reader = new FileReader(file);

            BufferedReader br = new BufferedReader(reader);

            String fileContent = "";

            fileContent = br.readLine();

            br.close();
            reader.close();

            return fileContent;
        } catch (Exception e) {
            return "Unknown Robot";
        }
    }
}
