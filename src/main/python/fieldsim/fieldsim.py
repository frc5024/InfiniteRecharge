import pygame
from networktables import NetworkTables
import sys
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
white = (255,255,255)

# Robot size
rbt_size = (50,50)
rbt  = pygame.Surface(rbt_size)

# Others
distance_mul = round(1228 / 48) 

# image loading
field = pygame.image.load(sys.argv[1])

def getRobotPosition() -> tuple:

    rbt_position = sd.getString("[DriveTrain] pose", "None").split(" ")

    if rbt_position[0] == "None":
        return (200,200,45)


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


while True:

    # Handle window close
    for event in pygame.event.get():
        if event.type == pygame.QUIT:
            pygame.quit()
            quit()

    # Clear the frame
    gameDisplay.fill(white)

    # Draw the field
    gameDisplay.blit(field, (0,0))

    # Draw the "robot"
    x,y,theta = getRobotPosition()


    y += (635/2)
    x += (80 + rbt_size[1] / 2) 

    drawRegularPolygon(gameDisplay, blue, theta, x,y, rbt_size[0], rbt_size[1])
        

    # Update the screen
    pygame.display.update()
    clock.tick(60)

pygame.quit()
quit()