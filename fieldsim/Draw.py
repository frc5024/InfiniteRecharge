import pygame


def drawRegularPolygon(surface, color, theta, x, y, w, h):
    x -= w/2
    y -= h/2

    hw = w/2
    hh = h/2

    # Rect points
    points = [
        (-hw, -hh),
        (hw, -hh),
        (hw, hh),
        (-hw, hh)
    ]

    rotated_point = [pygame.math.Vector2(p).rotate(theta) for p in points]

    vCenter = pygame.math.Vector2((x, y))
    rect_points = [(vCenter + p) for p in rotated_point]
