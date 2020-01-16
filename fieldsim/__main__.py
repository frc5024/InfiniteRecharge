import pygame
import math

from .Colors import *
from .Field import Field
from .RobotConn import Robot

# Constants
FRC_YEAR = 2020
ROBOT_SIZE = (60, 60)

# Init pygame
pygame.init()
gameDisplay = pygame.display.set_mode((1350, 650))
pygame.display.set_caption('5024 Fieldsim')
clock = pygame.time.Clock()

# Create a field & robot
field = Field(FRC_YEAR)
robot = Robot(ROBOT_SIZE)

# Get field layers
field_base = field.getBase()
field_top = field.getTop()

# Tracker for last connection state
last_conn_state = False

while True:

    # Handle pushing connection info
    current_robot_state = robot.connected
    if current_robot_state != last_conn_state:
        print("Robot " + "Connected" if current_robot_state else "Disconnected")
        last_conn_state = current_robot_state

    # Handle window close
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            quit()

    # Clear the frame
    gameDisplay.fill(white)

    # Draw the field
    gameDisplay.blit(field_base, (0, 0))

    # Draw the "robot"
    robot.render(gameDisplay, field)

    # Add top of field
    gameDisplay.blit(field_top, (0, 0))

    # Update the screen
    pygame.display.update()
    clock.tick(60)

pygame.quit()
quit()
