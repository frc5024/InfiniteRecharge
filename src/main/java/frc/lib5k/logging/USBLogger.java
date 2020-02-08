package frc.lib5k.logging;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class USBLogger implements AutoCloseable {

    private Notifier m_thread;
    private boolean m_lastStateEnabled, m_lastUSBConnectionState = false;
    private ArrayList<String> m_messageBuffer = new ArrayList<>();
    private OutputStream m_fileStream;
    private String relPath;

    public USBLogger(String relPath) {
        this.relPath = relPath;

        // Start the thread
        m_thread = new Notifier(this::update);
        m_thread.startPeriodic(0.5);

        // Create the next file
        // try {
        //     splitLogfile();
        // } catch (IOException e) {
        //     DriverStation.reportError("Failed to start USBLogger. Running dormant thread", false);
        // }

    }

    public void writeln(String line) {
        m_messageBuffer.add(line);
    }

    public void splitLogfile() throws IOException {

        // Close the file
        close();

        // Get the next filename
        String newFile = createFilename(new Date());

        // Determine the new logfile path
        Path filepath = Paths.get(getUSBPath().toString(), newFile);

        // Set the new filestream
        m_fileStream = Files.newOutputStream(filepath, StandardOpenOption.CREATE_NEW);

    }

    public boolean isUSBAttached() {
        return Files.exists(getUSBPath());
    }

    private void update() {

        // Check if a USB storage device is connected to the RoboRIO
        boolean usbAttached = isUSBAttached();
        if (!usbAttached) {

            // force-set the state
            m_lastStateEnabled = false;

            // Stop the logging by blocking the thread
            return;

        } else if (!m_lastUSBConnectionState && usbAttached) {
            // Clear the message buffer
            m_messageBuffer.clear();

            // Split the logfile to re-start the logging session
            try {
                splitLogfile();
            } catch (IOException e) {
                DriverStation.reportError("Failed to write to external logfile on USB stick", false);
            }
        }

        // Set the last known usb state
        m_lastUSBConnectionState = usbAttached;

        // Check if we just disabled. If so, save the file, and start a new logging
        // session
        if (m_lastStateEnabled && DriverStation.getInstance().isDisabled()) {
            try {
                // Split the logfile, and start a new logging session
                splitLogfile();
            } catch (IOException e) {
                DriverStation.reportError("Failed to write to external logfile on USB stick", false);
            }
        }

        // Set last state
        m_lastStateEnabled = !DriverStation.getInstance().isDisabled();

        // Write data buffer to logfile
        try {
            if (m_fileStream != null) {
                for (String line : m_messageBuffer) {
                    m_fileStream.write(String.format("%s%n", line).getBytes());
                }
            }
        } catch (IOException e) {
            DriverStation.reportError("Failed to write message buffer to USB", true);
        }

        // Clear the message buffer
        m_messageBuffer.clear();

    }

    @Override
    public void close() throws IOException {
        if (m_fileStream != null) {
            m_fileStream.close();
        }
    }

    /**
     * Create a filename with a time.
     *
     * @param time The time that is saved in the filename.
     * @return The filename at the format "{filePrefix}-{date/time}.txt".
     */
    private String createFilename(Date time) {
        // Get current date/time, format is YYYY-MM-DD.HH_mm_ss
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd.HH_mm_ss", Locale.getDefault());

        return "RobotLog-" + formater.format(time) + ".txt";
    }

    private Path getUSBPath() {
        if (RobotBase.isReal()) {
            return Paths.get("/media/sda1/", relPath);
        } else {
            return Paths.get(Filesystem.getOperatingDirectory().getAbsolutePath(), "simlogs");
        }
    }
}