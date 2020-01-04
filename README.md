# InfiniteRecharge <br> ![CI Badge](https://github.com/frc5024/infiniterecharge/workflows/FRC%20Build%20%26%20Test/badge.svg)

The source and tooling behind [FRC](https://www.firstinspires.org/robotics/frc) team [5024](https://www.thebluealliance.com/team/5024)'s 2020 competition robot. 

## Table of contents

 - [Setup & Installation](#project-setup--installation)
 - [Building the project](#building-the-project)
 - [Code deployment](#deployment)
 - [Documentation](#documentation)
 - [Vendor Libraries](#vendor-libraries)
 - [Networking](#networking)
 - [Game resources](#frc-game-resources)
 - [Troubleshooting](#troubleshooting)


## Project setup & Installation

This project has multiple components, each requiring their own tools and software versions.

### Robot software

For robot software development, we are using the following tools:
```
Java: Version 11
Python: Version 3.7 or later
Gradle: Version 5.0
WPIlib: 2020.1.2
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


## Building the project

This project runs on [Gradle](https://gradle.org/), so most of the work of installing dependancies, and configuring builds is automatically handled. The first time the code is built, Gradle will automatically download all the tools you need. 

There are two ways to build the robot project. 
  - Pressing `CTRL+SHIFT+P` in VSCode, searching for `WPILib: Build robot code`, and running the task
  - Opening the project in a terminal, and running `.\gradlew.bat build` (Windows) or `./gradlew build` (Linux)

### GitHub CI

Every time code is pushed to GitHub, or a pull request is opened, our [CI Pipeline](https://github.com/marketplace/actions/frc-build-test) will automatically build a copy of the code on GitHub's servers, and report back with a status. This can be checked with the badge at the top of the README file, or by looking for an icon beside each commit. Checkmark is a success, Yellow circle is "Build in progress", and a red X is "failure". 


## Deployment

All code deployment to the robot is done over the robot's internal network. In the shop, you can either connect to the robot's wifi access point, or connect with an ethernet cable to the grey networking switch mounted inside the robot.

Once connected, robot code deployment should be done through the VSCode task: `WPILib: Deploy robot code`. It will handle almost everything for you.


## Documentation

Here are the links to the multiple documentation sources for this project

 - [Robot information](https://cs.5024.ca/webdocs/docs/robots/recharge)
 - [Robot javadoc](https://cs.5024.ca/InfiniteRecharge/)
 - [General FRC documentation](https://docs.wpilib.org/en/latest/)
 - [WPILib API](https://first.wpi.edu/FRC/roborio/release/docs/java/index.html)
 - [CTRE documentation](https://phoenix-documentation.readthedocs.io/en/latest/index.html)
 - [CTRE API](http://www.ctr-electronics.com/downloads/api/java/html/index.html)
 - [Limelight documentation](http://docs.limelightvision.io/en/latest/)

### Building robot documentation

The robot javadoc must be built manually. This **must** be done on a linux system, or inside a [WSL](https://en.wikipedia.org/wiki/Windows_Subsystem_for_Linux) environment.

There is a Perl script in the project root that will handle generation. Just `cd` into the project root, and run:

```sh
perl doctool.pl -p
```

## Vendor libraries


Many of the libraries used by this codebase are available in the WPILib *vendor JSON* format. The JSON files for each library can be found at:

```
https://raw.githubusercontent.com/wpilibsuite/allwpilib/master/wpilibNewCommands/WPILibNewCommands.json
https://raw.githubusercontent.com/wpilibsuite/allwpilib/master/wpilibOldCommands/WPILibOldCommands.json
https://www.kauailabs.com/dist/frc/2020/navx_frc.json
http://devsite.ctr-electronics.com/maven/release/com/ctre/phoenix/Phoenix-latest.json
http://www.revrobotics.com/content/sw/max/sdk/REVRobotics.json
https://dev.imjac.in/maven/jaci/pathfinder/PathfinderOLD-latest.json
```

These should auto-update each time the project is built.


## Networking
As mentioned in the [Deployment section](#deployment), all robot communications are done through the robot's internal network. 

This network uses the following topology:
```
                           RoboRIO
                              |
Other Wireless devices (( Robot AP
                              |
                        Network Switch
                         |         |
             Limelight --+         +-- Other Wired devices
```

The "Other Wired/Wireless devices" would be things like

 - Programmer's laptops when deploying code
 - The Laptop running DriverStation

The network uses the `10.50.24.x` address space with dynamic IP addresses assigned from `10.50.24.20` and up.

Some devices on the network have static addresses

| Device    | Address       |
| --------- | ------------- |
| Router    | `10.50.24.1`  |
| RoboRIO   | `10.50.24.2`  |
| Limelight | `10.50.24.11` |

**Note: The wireless AP's password is `raiderrobotics`**

## FRC Game resources

*FIRST* provides us with a few useful documents containing information about the competition. For convenience, the two most important of these are included in this repo in the `docs/gameseason` folder. The following links will bring you to the rest of the needed files. Make sure to keep a copy of these on your computer at all times, we reference them a lot.

These files are usually password-protected. The password is: `UNKNOWN`

 - _Game animation_
 - [Game manual](https://firstfrc.blob.core.windows.net/frc2020/Manual/2020FRCGameSeasonManual.pdf)
 - [Pre-kickoff rules](https://firstfrc.blob.core.windows.net/frc2019/PreKickoffRuleChanges.pdf)
 - [Half-field drawings](https://firstfrc.blob.core.windows.net/frc2020/PlayingField/2020TeamDrawing-HalfField.pdf)
 - [Full field diagrams](https://firstfrc.blob.core.windows.net/frc2020/PlayingField/2020FieldDrawing-SeasonSpecific.pdf)
 - [Field photos](https://firstfrc.blob.core.windows.net/frc2020/PlayingField/NASAFieldImages.zip) (**Large Download**)

## Troubleshooting

Here are some solutions for known problems

### Gradle wrapper errors

Try installing Gradle (if not already installed), opening a command prompt / terminal in the project root, and running `gradle.exe wrapper` (Windows) or `gradle wrapper` (Linux)

### Deployment errors

 - Check to make sure you are properly connected with the robot, and it is turned on
 - Try deploying again (sometimes a double-deploy is needed)
 - Try restarting the robot from driverstation
 - Try killing the power to the robot, then turning it on again
 - Use Ethernet
