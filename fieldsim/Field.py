from pygame.surface import Surface
import pygame
from os import path
import json


# Constants
FIELD_DATA_PATH = path.join("fieldsim", "assets", "fields")


def _isFieldAvalible(year: int) -> bool:
    # Check for field cfg file
    return path.exists(path.join(FIELD_DATA_PATH, f"{year}field-cfg.json"))


class Field(Surface):

    # Field sprite surfaces
    _base_surf: Surface
    _top_surf: Surface

    # Field config data
    _field_cfg: dict

    def __init__(self, year: int) -> None:

        # Try to find the files
        if not _isFieldAvalible(year):
            raise FileNotFoundError(f"Can not find {year}field-cfg.json")

        # Load the field cfg
        self._field_cfg = json.load(
            open(path.join(FIELD_DATA_PATH, f"{year}field-cfg.json"), "r"))

        # Load the field images specified in the cfg
        self._base_surf = pygame.image.load(
            path.join(FIELD_DATA_PATH, self._field_cfg["assets"]["base"]))
        self._top_surf = pygame.image.load(
            path.join(FIELD_DATA_PATH, self._field_cfg["assets"]["top"])).convert_alpha()

    def getBase(self) -> Surface:
        return self._base_surf

    def getTop(self) -> Surface:
        return self._top_surf

    def getSize(self) -> (int, int):
        return (self._field_cfg["size"]["width"], self._field_cfg["size"]["height"])

    def getMappedX(self, val: int) -> float:
        return (val * self._field_cfg["pose_mapping"]["x"]) + self._field_cfg["origin_offset"]["x"]

    def getMappedY(self, val: int) -> float:
        return (val * self._field_cfg["pose_mapping"]["x"] * self._field_cfg["pose_mapping"]["y"]) + self._field_cfg["origin_offset"]["y"]


# Tests
if __name__ == "__main__":
    pygame.init()
    pygame.display.set_mode((1228, 635))
    f = Field(2020)
