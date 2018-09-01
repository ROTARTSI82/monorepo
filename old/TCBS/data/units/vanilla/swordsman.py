#!venv/bin python
# -*- coding: UTF-8 -*-

"""
The code for the AI of your soldier!
This is an example, so feel free to modify this code!
"""

import random
import time

import pygame
from pygame.locals import *
import math


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


vanilla_swordsman_red1 = from_spritesheet((0, 89, 75, 75))
vanilla_swordsman_red2 = from_spritesheet((95, 91, 115, 75))
vanilla_swordsman_blue1 = from_spritesheet((209, 100, 75, 75))
vanilla_swordsman_blue2 = from_spritesheet((305, 98, 120, 70))


class SandboxSwordsman(pygame.sprite.Sprite):
    """
    This is the version of your soldier that would be loaded in sandbox mode.
    """
    name = "Swordsman - $25"  # What to display at the top when this unit is selected.
    cost = 25  # How many coins your soldier costs to place

    def __init__(self, pos, team):
        # Define basic attributes
        pygame.sprite.Sprite.__init__(self)
        self.team = team
        self.speed = 2.5
        self.target = None

        # Melee attributes
        self.health = 125
        self.meleeDamage = 40
        self.meleeCooldown = 1
        self.lastMeleeAttack = 0
        self.rotation = 0
        self.velocity = pygame.math.Vector2(0, 0)

        # Set the icon to a red square if we're on the red team, and a blue one if we're on the blue team.
        if self.team == "red":
            self.image1 = vanilla_swordsman_red1
            self.image2 = vanilla_swordsman_red2
        elif self.team == "blue":
            self.image1 = vanilla_swordsman_blue1
            self.image2 = vanilla_swordsman_blue2

        # Set the position to pos
        self.image = self.image1
        self.masterimage = self.image1
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

        # Move towards the target
        targetpos = pygame.math.Vector2(self.target.rect.center)
        mypos = pygame.math.Vector2(self.rect.center)
        dx, dy = (targetpos.x - mypos.x, targetpos.y - mypos.y)
        self.rotation = math.degrees(math.atan2(-dy, dx)) - 90
        travelTime = mypos.distance_to(targetpos) / self.speed
        if travelTime != 0:
            self.velocity = pygame.math.Vector2((dx / travelTime), (dy / travelTime))
        mypos += self.velocity
        self.rect.center = [int(mypos.x), int(mypos.y)]

        old_rect_pos = self.rect.center
        self.image = pygame.transform.rotate(self.masterimage, self.rotation)
        self.rect = self.image.get_rect()
        self.rect.center = old_rect_pos

        # If self.rangeCooldown seconds have passed since self.lastRangeAttack,
        # Shoot a SmartBullet

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
        target = random.choice(hitlist)
        if (time.time() - self.lastMeleeAttack) > self.meleeCooldown:
            target.health -= self.meleeDamage
            self.lastMeleeAttack = time.time()
            old_rect_pos = self.rect.topleft
            if self.masterimage == self.image1:
                self.masterimage = self.image2
                self.image = pygame.transform.rotate(self.masterimage, self.rotation)
            else:
                self.masterimage = self.image1
                self.image = pygame.transform.rotate(self.masterimage, self.rotation)
            self.rect = self.image.get_rect()
            self.rect.topleft = old_rect_pos

    def on_bullet_hit(self, hitlist):
        """
        NotImplemented

        :param hitlist: List of bullet sprites touching your soldier
        :return:
        """
        pass


class MultiplayerSwordsman(pygame.sprite.Sprite):
    """
    This is the version of your soldier that would be loaded in multiplayer mode.
    """
    name = "Swordsman - $25"
    cost = 25

    def __init__(self, pos, team, unitid, rotation=0):
        pygame.sprite.Sprite.__init__(self)
        self.team = team
        self.speed = 2.5
        self.target = None
        self.unitid = unitid

        # Melee attributes
        self.health = 125
        self.meleeDamage = 40
        self.meleeCooldown = 1
        self.lastMeleeAttack = 0
        self.rotation = rotation
        self.velocity = pygame.math.Vector2(0, 0)

        # Set the icon to a red square if we're on the red team, and a blue one if we're on the blue team.
        if self.team == "red":
            self.image1 = vanilla_swordsman_red1
            self.image2 = vanilla_swordsman_red2
        elif self.team == "blue":
            self.image1 = vanilla_swordsman_blue1
            self.image2 = vanilla_swordsman_blue2

        # Set the position to pos
        self.image = self.image1
        self.masterimage = self.image1
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
            travelTime = mypos.distance_to(targetpos) / self.speed
            if travelTime != 0:
                self.velocity = pygame.math.Vector2((dx / travelTime), (dy / travelTime))
            mypos += self.velocity
            self.rect.center = [int(mypos.x), int(mypos.y)]

        old_rect_pos = self.rect.center
        self.image = pygame.transform.rotate(self.masterimage, self.rotation)
        self.rect = self.image.get_rect()
        self.rect.center = old_rect_pos


        if self.health <= 0:
            self.kill()

    def on_soldier_hit(self, hitlist, calledbyhost):
        """
        Is called when two enemy soldiers collide
        Add code to damage your enemy here

        :param hitlist: List of enemy soldiers touching your soldier
        :return: None
        """
        global c
        # If self.meleeCooldown seconds have passed since self.lastMeleeAttack,
        # Damage a random enemy on hitlist by self.meleeDamage
        target = random.choice(hitlist)
        if (time.time() - self.lastMeleeAttack) > self.meleeCooldown:
            if self.team == "red" and target in multBUnits:
                if not calledbyhost:
                    c.Send({"action": "callfunc", "unitid": target.unitid, "sentbyhost": calledbyhost,
                            "func": "damage", "args": [self.meleeDamage, ], "kwargs": {}})
            if self.team == "blue" and target in multRUnits:
                if calledbyhost:
                    c.Send({"action": "callfunc", "unitid": target.unitid, "sentbyhost": calledbyhost,
                            "func": "damage", "args": [self.meleeDamage, ], "kwargs": {}})
            self.lastMeleeAttack = time.time()
            old_rect_pos = self.rect.topleft
            if self.masterimage == self.image1:
                self.masterimage = self.image2
                self.image = pygame.transform.rotate(self.masterimage, self.rotation)
            else:
                self.masterimage = self.image1
                self.image = pygame.transform.rotate(self.masterimage, self.rotation)
            self.rect = self.image.get_rect()
            self.rect.topleft = old_rect_pos

    def on_bullet_hit(self, hitlist, calledbyhost):
        """
        NotImplemented

        :param hitlist: List of bullet sprites touching your soldier
        :return:
        """
        pass


MultiplayerSwordsman.__name__ = "vanilla_swordsman"
