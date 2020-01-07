import pygame
from networktables import NetworkTables
import sys
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
distance_mul = (1228 / 54) 

# image loading
field = pygame.image.load(sys.argv[1])

def getRobotPosition() -> tuple:

    rbt_position = sd.getString("[DriveTrain] pose", "None").split(" ")

    if rbt_position[0] == "None":
        return (200,200,0)

    # print(rbt_position)

    x = float(rbt_position[1][:-1])
    y = float(rbt_position[3][:-2])
    theta = float(rbt_position[-1][:-2])

    return (x * distance_mul,y*distance_mul, theta)


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
    pygame.draw.rect(gameDisplay,blue,(x -rbt_size[0] / 2 ,y - rbt_size[1]/2, rbt_size[0] , rbt_size[1]))

    # r_rbt = pygame.transform.rotate(rbt, theta)
        

    # gameDisplay.blit(r_rbt, (x+rbt_size[0]/2,y+rbt_size[1]/2))
    # Update the screen
    pygame.display.update()
    clock.tick(60)

pygame.quit()
quit()