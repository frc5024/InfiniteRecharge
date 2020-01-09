import pygame
from networktables import NetworkTables
import math

print("Starting fieldsim")

# Init pygame
pygame.init()
gameDisplay = pygame.display.set_mode((1228,635))
pygame.display.set_caption('5024 Fieldsim')
clock = pygame.time.Clock()

# Init NT
NetworkTables.initialize(server='localhost')
sd = NetworkTables.getTable('SmartDashboard')

# Colors
blue = (0,0,180)
grey = (98,98,98)
white = (255,255,255)

# Robot size
rbt_size = (60,60)
rbt  = pygame.Surface(rbt_size)

# Others
distance_mul = round(1228 / 16)  # Image width / aprox field width (meters)

# image loading
field_base = pygame.image.load("images/2020field-base.png")
field_top = pygame.image.load("images/2020field-top.png").convert_alpha()
rbt_surf = pygame.image.load("images/robot-sprite.png").convert_alpha()
rbt_surf = pygame.transform.scale(rbt_surf, rbt_size)

def getRobotPosition() -> tuple:

    rbt_position = sd.getString("[DriveTrain] pose", "None").split(" ")
    print(rbt_position)

    if rbt_position[0] == "None":
        return (100,0,45)


    x = float(rbt_position[1][:-1])
    y = float(rbt_position[3][:-2])
    theta = float(rbt_position[-1][:-2])

    return (x * distance_mul,y*distance_mul, theta)

def drawRegularPolygon(surface, color, theta, x, y, w,h):
    x -= w/2
    y-= h/2

    hw = w/2
    hh = h/2

    # Rect points
    points = [
        (-hw,-hh),
        (hw,-hh),
        (hw,hh),
        (-hw,hh)
    ]

    rotated_point = [pygame.math.Vector2(p).rotate(theta) for p in points]

    vCenter = pygame.math.Vector2((x ,y ))
    rect_points = [(vCenter + p) for p in rotated_point]

    pygame.draw.polygon(surface, color, rect_points)

def drawRobot(x,y,theta):
    rbt = pygame.transform.rotate(rbt_surf, -theta)
    gameDisplay.blit(rbt, (x - (rbt.get_width() / 2),y - (rbt.get_height() / 2)))


while True:

    # Handle window close
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            quit()

    # Clear the frame
    gameDisplay.fill(white)

    # Draw the field
    gameDisplay.blit(field_base, (0,0))

    # Draw the "robot"
    x,y,theta = getRobotPosition()

    # Shift position to match real field positioning
    y += (635/2)
    x += (60 + rbt_size[1] / 2) 

    # Draw the robot
    drawRobot(x,y,theta)


    # Add top of field
    gameDisplay.blit(field_top, (0,0))

    # Update the screen
    pygame.display.update()
    clock.tick(60)

pygame.quit()
quit()