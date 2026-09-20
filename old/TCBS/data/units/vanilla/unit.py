#!venv/bin python
# -*- coding: UTF-8 -*-

"""
Loads all six units
"""

if False:
    # Ignore this code. It makes PyCharm happy
    # Since I call this script via execfile, PyCharm thinks
    # all the variables are undefined and gives me endless warnings :(
    from turret import *
    from wizard import *
    from swordsman import *
    from peasant import *
    from infantryman import *
    from unit import *
    from exampleUnit import *


def from_spritesheet(rectangle):
    """
    Load an image from the spritesheet.

    :param rectangle: [x, y, width, height]
    :rtype: pygame.Surface
    """
    rect = pygame.Rect(rectangle)
    image = pygame.Surface(rect.size).convert()
    image.blit(pygame.image.load("units/vanilla/spritesheet.png"), (0, 0), rect)
    image.set_colorkey((255, 255, 255), RLEACCEL)
    return image


executefile("units/vanilla/exampleUnit.py")
executefile("units/vanilla/infantryman.py")
executefile("units/vanilla/peasant.py")
executefile("units/vanilla/swordsman.py")
executefile("units/vanilla/turret.py")
executefile("units/vanilla/wizard.py")

serializableBullets = [MultiplayerTurretBullet, MultiplayerInfantrymanBullet,
                       MultiplayerSmartBullet, MultiplayerWizardBullet]
sandboxUnits = [SandboxTurret, SandboxSwordsman, SandboxPeasant,
                SandboxInfantryman, SandboxExampleUnit, SandboxWizard]
multiplayerUnits = [MultiplayerTurret, MultiplayerSwordsman, MultiplayerPeasant,
                    MultiplayerInfantryman, MultiplayerExampleUnit, MultiplayerWizard]
