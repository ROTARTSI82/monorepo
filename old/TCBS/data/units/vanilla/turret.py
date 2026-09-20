#!venv/bin python
# -*- coding: UTF-8 -*-

"""
The code for the AI of your soldier!
This is an example, so feel free to modify this code!
"""

import random
import time
import math

import pygame
from pygame.locals import *

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


vanilla_turret_red = from_spritesheet((15, 358, 65, 65))
vanilla_turret_blue = from_spritesheet((227, 366, 65, 65))


class SandboxTurret(pygame.sprite.Sprite):
    """
    This is the version of your soldier that would be loaded in sandbox mode.
    """
    name = "Turret - $70"  # What to display at the top when this unit is selected.
    cost = 70  # How many coins your soldier costs to place

    def __init__(self, pos, team):
        # Define basic attributes
        pygame.sprite.Sprite.__init__(self)
        self.team = team
        self.target = None

        # Melee attributes
        self.health = 225

        # Bullet attributes
        self.lastRangeAttack = 0
        self.rangeCooldown = 0.25

        # Set the icon to a red square if we're on the red team, and a blue one if we're on the blue team.
        if self.team == "red":
            self.image = vanilla_turret_red
        elif self.team == "blue":
            self.image = vanilla_turret_blue
        self.masterimage = self.image
        self.rotation = 0

        # Set the position to pos
        self.rect = self.image.get_rect()
        self.rect.center = pos

    def pack(self):
        """
        Return the arguments that __init__() takes

        :rtype: ((int, int), str)
        """
        return self.rect.center, self.team

    def update(self):
        """
        Is called every tick. Add code to move your soldier here.

        :return: None
        """
        global sndbxRUnits, sndbxBUnits, bullets

        # Don't do anything if the battle is over
        if len(sndbxBUnits) == 0 or len(sndbxRUnits) == 0:
            return

        # Check if the target is still alive,
        # and set a new target if our old target is dead
        if self.team == "blue":
            if self.target not in sndbxRUnits:
                self.target = random.choice(sndbxRUnits.sprites())
        if self.team == "red":
            if self.target not in sndbxBUnits:
                self.target = random.choice(sndbxBUnits.sprites())

        targetpos = pygame.math.Vector2(self.target.rect.center)
        mypos = pygame.math.Vector2(self.rect.center)
        dx, dy = (targetpos.x - mypos.x, targetpos.y - mypos.y)
        self.rotation = math.degrees(math.atan2(-dy, dx)) - 90
        old_rect_pos = self.rect.center
        self.image = pygame.transform.rotate(self.masterimage, self.rotation)
        self.rect = self.image.get_rect()
        self.rect.center = old_rect_pos

        # If self.rangeCooldown seconds have passed since self.lastRangeAttack,
        # Shoot a SmartBullet
        if (time.time() - self.lastRangeAttack) > self.rangeCooldown:
            bullets.add(TurretBullet(self.rect.center, self.team, self.target))
            self.lastRangeAttack = time.time()

        # Remove us from sprite group if health reaches 0
        if self.health <= 0:
            self.kill()

    def on_soldier_hit(self, hitlist):
        """
        Is called when two enemy soldiers collide
        Add code to damage your enemy here

        :param hitlist: List of enemy soldiers touching your soldier
        :return: None
        """
        # If self.meleeCooldown seconds have passed since self.lastMeleeAttack,
        # Damage a random enemy on hitlist by self.meleeDamage
        pass

    def on_bullet_hit(self, hitlist):
        """
        NotImplemented

        :param hitlist: List of bullet sprites touching your soldier
        :return:
        """
        pass


class MultiplayerTurret(pygame.sprite.Sprite):
    """
    This is the version of your soldier that would be loaded in multiplayer mode.
    """
    name = "Turret - $70"
    cost = 70

    def __init__(self, pos, team, unitid, rotation=0):
        pygame.sprite.Sprite.__init__(self)
        self.team = team
        self.unitid = unitid
        self.health = 255

        self.target = None
        self.rotation = rotation

        self.lastRangeAttack = 0
        self.rangeCooldown = 0.25

        if team == "red":
            self.image = vanilla_turret_red
        elif team == "blue":
            self.image = vanilla_turret_blue
        self.masterimage = self.image

        self.rect = self.image.get_rect()
        self.rect.center = pos

    def _pack(self):
        return self.rect.center, self.team, self.unitid, self.rotation

    def damage(self, amount):
        self.health -= amount

    def update(self, calledbyhost):
        global multRUnits, multBUnits, bullets, BBullets, RBullets
        global activeBDict, activeRDict

        # Don't do anything if the battle is over
        if len(multBUnits) == 0 or len(multRUnits) == 0:
            return

        # Check if the target is still alive,
        # and set a new target if our old target is dead
        if self.team == "blue" and calledbyhost:
            if getattr(self.target, "unitid", None) not in activeRDict.keys():
                self.target = random.choice(multRUnits.sprites())
            elif self.target is not None:
                self.target = activeRDict[self.target.unitid]
            if self.target is None:
                self.target = random.choice(multRUnits.sprites())
        if self.team == "red" and not calledbyhost:
            if getattr(self.target, "unitid", None) not in activeBDict.keys():
                self.target = random.choice(multBUnits.sprites())
            elif self.target is not None:
                self.target = activeBDict[self.target.unitid]
            if self.target is None:
                self.target = random.choice(multBUnits.sprites())

        # Move towards the target
        if self.target is not None:
            targetpos = pygame.math.Vector2(self.target.rect.center)
            mypos = pygame.math.Vector2(self.rect.center)
            dx, dy = (targetpos.x - mypos.x, targetpos.y - mypos.y)
            self.rotation = math.degrees(math.atan2(-dy, dx)) - 90
        old_rect_pos = self.rect.center
        self.image = pygame.transform.rotate(self.masterimage, self.rotation)
        self.rect = self.image.get_rect()
        self.rect.center = old_rect_pos

        if (time.time() - self.lastRangeAttack) > self.rangeCooldown:
            if self.team == "red" and not calledbyhost:
                RBullets.add(MultiplayerTurretBullet(self.rect.center, self.team, self.target))
                self.lastRangeAttack = time.time()
            if self.team == "blue" and calledbyhost:
                BBullets.add(MultiplayerTurretBullet(self.rect.center, self.team, self.target))
                self.lastRangeAttack = time.time()

        if self.health <= 0:
            self.kill()

    def on_soldier_hit(self, hitlist, calledbyhost):
        """
        Is called when two enemy soldiers collide
        Add code to damage your enemy here

        :param hitlist: List of enemy soldiers touching your soldier
        :return: None
        """
        # If self.meleeCooldown seconds have passed since self.lastMeleeAttack,
        # Damage a random enemy on hitlist by self.meleeDamage
        pass

    def on_bullet_hit(self, hitlist, calledbyhost):
        """
        NotImplemented

        :param hitlist: List of bullet sprites touching your soldier
        :return:
        """
        pass


class MultiplayerTurretBullet(pygame.sprite.Sprite):
    def __init__(self, pos, team, target=None):
        # Define basic attributes
        pygame.sprite.Sprite.__init__(self)
        self.team = team
        self.speed = 20
        self.damage = 12.5
        self.target = target
        self.velocity = pygame.math.Vector2(0, 0)

        # Set the image to a yellow sqaure and the posistion to pos
        self.image = pygame.Surface([15, 15], SRCALPHA, 32).convert_alpha()
        pygame.draw.circle(self.image, [255, 255, 0], [7, 7], 5)
        self.rect = self.image.get_rect()
        self.rect.center = pos
        self.pos = pygame.math.Vector2(self.rect.center)
        if self.target is not None:
            targetpos = pygame.math.Vector2(self.target.rect.center)
            dx, dy = (targetpos.x - self.pos.x, targetpos.y - self.pos.y)
            traveltime = self.pos.distance_to(targetpos) / self.speed
            if traveltime != 0:
                self.velocity = pygame.math.Vector2((dx / traveltime), (dy / traveltime))
            else:
                self.velocity = pygame.math.Vector2(0, 0)

    def _pack(self):
        return self.rect.center, self.team

    def update(self, calledbyhost):
        """
        Add code to move your bullet!

        :rtype: None
        """
        global multRUnits, multBUnits, screen

        # Don't do anything if the battle is over
        if len(multBUnits) == 0 or len(multRUnits) == 0:
            return

        # Move towards the target
        if self.velocity == pygame.math.Vector2(0, 0) and self.target is not None:
            targetpos = pygame.math.Vector2(self.target.rect.center)
            self.pos = pygame.math.Vector2(self.rect.center)
            dx, dy = (targetpos.x - self.pos.x, targetpos.y - self.pos.y)
            traveltime = self.pos.distance_to(targetpos) / self.speed
            if traveltime != 0:
                self.velocity = pygame.math.Vector2((dx / traveltime), (dy / traveltime))
            else:
                self.velocity = pygame.math.Vector2(0, 0)
        self.pos += self.velocity
        self.rect.center = [int(self.pos.x), int(self.pos.y)]

        if self.rect.centery > screen.get_height() or self.rect.centery < 0:
            self.kill()
        if self.rect.centerx > screen.get_width() or self.rect.centerx < 0:
            self.kill()

    def on_bullet_hit(self, hitlist, calledbyhost):
        """
        Called when the bullet touches a soldier

        :param hitlist: List of soldiers touching the bullet
        :return: None
        """
        global multRUnits, multBUnits, c
        # Damage a random enemy touching the bullet
        for k in hitlist:
            if self.team == "red" and k in multBUnits:
                if not calledbyhost:
                    c.Send({"action": "callfunc", "unitid": k.unitid, "sentbyhost": calledbyhost,
                            "func": "damage", "args": [self.damage, ], "kwargs": {}})
                self.kill()
                return
            if self.team == "blue" and k in multRUnits:
                if calledbyhost:
                    c.Send({"action": "callfunc", "unitid": k.unitid, "sentbyhost": calledbyhost,
                            "func": "damage", "args": [self.damage, ], "kwargs": {}})
                self.kill()
                return


MultiplayerTurret.__name__ = "vanilla_turret"
MultiplayerTurretBullet.__name__ = "vanilla_turret_bullet"


class TurretBullet(pygame.sprite.Sprite):
    """
    The Bullets shot by your soldier!
    """
    def __init__(self, pos, team, target):
        # Define basic attributes
        pygame.sprite.Sprite.__init__(self)
        self.team = team
        self.speed = 20
        self.damage = 12.5
        self.target = target
        self.velocity = pygame.math.Vector2(0, 0)

        # Set the image to a yellow sqaure and the posistion to pos
        self.image = pygame.Surface([15, 15], SRCALPHA, 32).convert_alpha()
        pygame.draw.circle(self.image, [255, 255, 0], [7, 7], 5)
        self.rect = self.image.get_rect()
        self.rect.center = pos

        targetpos = pygame.math.Vector2(self.target.rect.center)
        self.pos = pygame.math.Vector2(self.rect.center)
        dx, dy = (targetpos.x - self.pos.x, targetpos.y - self.pos.y)
        traveltime = self.pos.distance_to(targetpos) / self.speed
        if traveltime != 0:
            self.velocity = pygame.math.Vector2((dx / traveltime), (dy / traveltime))
        else:
            self.velocity = pygame.math.Vector2(0, 0)

    def update(self):
        """
        Add code to move your bullet!

        :rtype: None
        """
        global sndbxRUnits, sndbxBUnits

        # Don't do anything if the battle is over
        if len(sndbxBUnits) == 0 or len(sndbxRUnits) == 0:
            return

        # Move towards the target
        if self.velocity == pygame.math.Vector2(0, 0):
            targetpos = pygame.math.Vector2(self.target.rect.center)
            self.pos = pygame.math.Vector2(self.rect.center)
            dx, dy = (targetpos.x - self.pos.x, targetpos.y - self.pos.y)
            traveltime = self.pos.distance_to(targetpos) / self.speed
            if traveltime != 0:
                self.velocity = pygame.math.Vector2((dx / traveltime), (dy / traveltime))
            else:
                self.velocity = pygame.math.Vector2(0, 0)
        self.pos += self.velocity
        self.rect.center = [int(self.pos.x), int(self.pos.y)]

        if self.rect.centery > screen.get_height() or self.rect.centery < 0:
            self.kill()
        if self.rect.centerx > screen.get_width() or self.rect.centerx < 0:
            self.kill()

    def on_bullet_hit(self, hitlist):
        """
        Called when the bullet touches a soldier

        :param hitlist: List of soldiers touching the bullet
        :return: None
        """
        global sndbxRUnits, sndbxBUnits
        # Damage a random enemy touching the bullet
        for k in hitlist:
            if self.team == "red" and k in sndbxBUnits:
                k.health -= self.damage
                self.kill()
                return
            if self.team == "blue" and k in sndbxRUnits:
                k.health -= self.damage
                self.kill()
                return
