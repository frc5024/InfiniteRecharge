package frc.lib5k.components;

import edu.wpi.cscore.MjpegServer;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import frc.lib5k.utils.FileUtils;
import frc.lib5k.utils.RobotLogger;
import frc.lib5k.utils.RobotLogger.Level;

/**
 * Automatic camera streaming.
 */
public class AutoCamera {
    static int m_cameraCount = 0;
    RobotLogger logger = RobotLogger.getInstance();
    UsbCamera m_UsbCamera;
    MjpegServer m_cameraServer;

    public AutoCamera() {
        this("Unnamed (Automatic) Camera", 0);
    }

    public AutoCamera(int usb_slot) {
        this("Unnamed Camera", usb_slot);
    }

    public AutoCamera(String name, int usb_slot) {
        // Create a USBCamera
        m_UsbCamera = CameraServer.getInstance().startAutomaticCapture(name, usb_slot);
        m_UsbCamera.setVideoMode(VideoMode.PixelFormat.kMJPEG, 320, 240, 15);

        // Add self to shuffleboard
        Shuffleboard.getTab("DriverStation").add(m_UsbCamera);
    }

    /**
     * Set the camera resolution and framerate
     * 
     * @param height Height in pixels
     * @param width  Width in pixels
     * @param fps    Frames per second
     */
    public void setResolution(int height, int width, int fps) {
        m_UsbCamera.setVideoMode(VideoMode.PixelFormat.kMJPEG, height, width, fps);
        logger.log(m_UsbCamera.getName() + "'s resolution set to " + width + "x" + height, Level.kLibrary);
    }

    /**
     * Load a json file as a config
     * 
     * @param filepath Path to json file
     */
    public void loadJsonConfig(String filepath) {

        try {
            String config = FileUtils.readFile(filepath);

            m_UsbCamera.setConfigJson(config);

            logger.log(m_UsbCamera.getName() + "'s config has been loaded from: " + filepath, Level.kLibrary);
        } catch (Exception e) {
            logger.log("Unable to load camera config file: " + filepath, Level.kWarning);
        }
    }

    /**
     * Set the camera connection mode
     * 
     * @param enabled Should the camera always be streaming video?
     */
    public void keepCameraAwake(boolean enabled) {
        ConnectionStrategy strategy = enabled ? ConnectionStrategy.kKeepOpen : ConnectionStrategy.kAutoManage;
        String strategy_string = enabled ? "Stay Awake" : "Auto Manage";

        m_UsbCamera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
        logger.log(m_UsbCamera.getName() + "'s connection mode has been set to: " + strategy_string, Level.kLibrary);
    }

}