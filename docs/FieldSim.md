# FieldSim

Fieldsim is a python-based top-down robot simulation GUI that allows us to test drivebase code using HALSIM.

## Setup

Along with the tools required for [robot development](../README.md#robot-software), FieldSim requires [Python3](https://www.python.org/), [Python3-pip](https://pip.pypa.io/en/stable/installing/) (pip may be automatically installed) and some libraries. With python and pip installed, navigate to the project root, and run

```sh
pip3 install -r requirements.txt # Some systems may use "pip" instead of "pip3"
```

## Program requirements

FieldSim requires:
 - An Xbox controller with USB connection
 - A decently powerful computer

## Usage

Before starting fieldsim, you must start HALSIM. To do this, open the project in VSCode, press `CTRL` + `SHIFT` + `p`, then search for `WPILib: Simulate robot code on desktop`. A box should pop up asking which HALSIM library to use. Select the first option, then press "OK". Plug in the controller if you haven't already.

*If you see an error about the project build failing, just press "proceed" and ignore the message.*

Inside HALSIM, drag the controller from the "System Joysticks" box to the "Joysticks" box in port 0. Now, set the "Robot State" to "Teleoperated".

Finally, you can start FieldSim. For Linux users, just run `./start-fieldsim.sh`. Windows users must run the python script located at `src/main/python/fieldsim/fieldsim.py`, and pass the filepath to the field image as it's argument. (This should be `images/2020field.png`)

Now, you can use the Xbox controller to drive the bot around. 