# InfiniteRecharge

The source and tooling behind [FRC](https://www.firstinspires.org/robotics/frc) team [5024](https://www.thebluealliance.com/team/5024)'s 2020 competition robot. 

## Project setup & Installation

This project has multiple components, each requiring their own tools and software versions.

### Robot software

For robot software development, we are using the following tools:
```
Java: Version 11
Python: Version 3.7 or later
Gradle: Version 5.0
WPIlib: 2020.1.1
```

Gradle can be downloaded by following the instructions [HERE](https://gradle.org/install/) (Windows users should read the "Installing Manually" section), and WPILib's installation instructions can be found [HERE](https://docs.wpilib.org/en/latest/docs/getting-started/getting-started-frc-control-system/wpilib-setup.html).

Java should be automatically installed by the WPILib installer, and Python can be installed from [HERE](https://www.python.org/downloads/).

#### Robot control

This robot is controlled via [NI DriverStation](https://docs.wpilib.org/en/latest/docs/software/driverstation/driver-station.html). This software is available as part of the *FRC Game Tools bundle for 2020*. Installation instructions can be found [HERE](https://docs.wpilib.org/en/latest/docs/getting-started/getting-started-frc-control-system/frc-game-tools.html#installing-the-frc-game-tools).

Robot UI is handled by [Shuffleboard](https://docs.wpilib.org/en/latest/docs/software/wpilib-tools/shuffleboard/getting-started/shuffleboard-tour.html), which should be installed when installing WPILib.

#### Hardware tools

Some additional tools are required for working with other Robot hardware. Make sure to install each tool:

 - [Phoenix framework](https://phoenix-documentation.readthedocs.io/en/latest/ch05_PrepWorkstation.html#what-to-download-and-why) (for working with devices from [CTRE](http://www.ctr-electronics.com/))
 - RoboRIO imaging tool (installed as part of the *FRC Game Tools bundle* above)
 - [FRC Radio configuration utility](https://firstfrc.blob.core.windows.net/frc2020/Radio/FRC_Radio_Configuration_20_0_0.zip) 
 - [Limelight flashing tool](adshttps://limelightvision.io/pages/downloads) (grab the "new tool" from the downlods page)

#### Robot extras

Some additional tools are highly recommended for working with the [RoboRIO](https://www.ni.com/en-ca/support/model.roborio.html). These are [Filezilla](https://filezilla-project.org/) and [WinSCP](https://winscp.net/eng/download.php) for windows. Linux users can use [scp](https://en.wikipedia.org/wiki/Secure_copy) and [ssh](https://en.wikipedia.org/wiki/Secure_Shell).

### Computer vision

Our computer vision system is handled by a [Limelight 2.0 camera](https://limelightvision.io/). No tools are needed for using this device.

### Documentation generation

Documentation generation is handled by a Gradle plugin, and a Perl script. The perl script can only be run on [*nix systems](https://en.wikipedia.org/wiki/Unix-like).

This script requires a recent version of [GIT](https://git-scm.com/), and [Perl 5.010](https://www.perl.org/) or later.