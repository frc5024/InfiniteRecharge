from networktables import NetworkTables
import socket


def isServiceAlive(host: str, port: int) -> bool:
    sock: socket.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    try:
        return not sock.connect_ex((host, port))
    except socket.gaierror as e:
        return False


# Constants
TE, AM = 50, 24

# Determine host to connect to
robot_locations = [
    "localhost",
    "10.{TE}.{AM}.2",
    "172.22.11.2",
]

# NT service info
nt_port = 1735
# nt_host = "127.0.0.1"
nt_host = "10.50.24.2"

# # Check for the correct NT host to read from
# print("Searching for robots on the network...")
# for host in robot_locations:
#     if isServiceAlive(host, nt_port):
#         nt_host = host
#         break

print(f"Robot found at {nt_host}")

# Init NT
NetworkTables.initialize(server=nt_host)
sd = NetworkTables.getTable("Lib5K-Telemetry/Components/FlywheelTuner-Shooter")

## Main app ##

import datetime as dt
import matplotlib.pyplot as plt
import matplotlib.animation as animation


# Create figure for plotting
fig = plt.figure()
ax = fig.add_subplot(1, 1, 1)
xs = []
ys = []

# This function is called periodically from FuncAnimation
def animate(i, xs, ys):
    
    # Read if the system is enabled (if not, don't graph anything)
    if not sd.getEntry("running").getBoolean(False):
        return
    
    # Read velocity
    velocity = sd.getEntry("rpm").getNumber(0.0)

    # Add x and y to lists
    xs.append(dt.datetime.now().strftime('%H:%M:%S.%f'))
    ys.append(velocity)
    
    # Limit x and y lists to 20 items
    xs = xs[-40:]
    ys = ys[-40:]

    # Draw x and y lists
    ax.clear()
    ax.plot(xs, ys)

    # Format plot
    plt.xticks(rotation=45, ha='right')
    plt.subplots_adjust(bottom=0.30)
    plt.title('Shooter Velocity Over Time')
    plt.ylabel('Velocity')

# Set up plot to call animate() function periodically
ani = animation.FuncAnimation(fig, animate, fargs=(xs, ys), interval=10)
plt.show()