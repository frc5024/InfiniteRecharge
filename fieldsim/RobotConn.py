from networktables import NetworkTables
from .NetUtil import isServiceAlive
import pygame
from os import path
from .Field import Field

# Constants
TE, AM = 50, 24
FIELDSIM_NT_TABLE = "SmartDashboard"
SPRITE_DATA_PATH = path.join("fieldsim", "assets")

# Determine host to connect to
robot_locations = [
    "localhost",
    "10.{TE}.{AM}.2",
    "172.22.11.2",
]

# NT service info
nt_port = 1735
nt_host = "127.0.0.1"

# Check for the correct NT host to read from
print("Searching for robots on the network...")
for host in robot_locations:
    if isServiceAlive(host, nt_port):
        nt_host = host
        break

print(f"Robot found at {nt_host}")

# Init NT
NetworkTables.initialize(server=nt_host)
sd = NetworkTables.getTable(FIELDSIM_NT_TABLE)


class Robot(object):

    _size: tuple
    _surf: pygame.surface.Surface

    def __init__(self, size: (int, int)):
        self._size = size

        # Load the robot sprite
        self._surf = pygame.image.load(path.join(SPRITE_DATA_PATH, "robot-sprite.png")
                                       ).convert_alpha()

        # Resize the sprite
        self._surf = pygame.transform.scale(self._surf, size)

    def getPose(self) -> (float, float, float):

        # Read robot pose data from nt
        rbt_position = sd.getString("[DriveTrain] pose", "None").split(" ")

        # If no pose found, place the robot on the screen at an angle
        if rbt_position[0] == "None":
            return (3, 0, 45)

        # Parse pose data
        x = float(rbt_position[1][:-1])
        y = float(rbt_position[3][:-2])
        theta = float(rbt_position[-1][:-2])

        return (x, y, theta)

    def render(self, surf: pygame.surface.Surface, field: Field) -> None:

        # Get the robot pose
        pose = self.getPose()

        # Map the coords
        x = field.getMappedX(pose[0]) + (self._size[1] / 2)
        y = field.getMappedY(pose[1])
        
        print((x,y))

        # Transform the sprite based on theta
        rot_sprite = pygame.transform.rotate(self._surf, pose[2] * -1)

        # Blit the surface to the base surface
        surf.blit(rot_sprite, (x - (rot_sprite.get_width() / 2),
                               y - (rot_sprite.get_height() / 2)))
