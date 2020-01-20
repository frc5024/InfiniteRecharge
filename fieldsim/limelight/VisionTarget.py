import pygame
from ..Colors import blue

class VisionTarget(Object):
    
    x: int
    y: int
    
    def __init__(self, x,y):
        self.x = x
        self.y = y
        
    def render(self, surf: pygame.surface.Surface):
        pygame.draw.line(surf, blue, (self.x, self.y), (self.x, self.y))