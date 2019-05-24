package com.rotartsi.jgame.mechanics;

import com.rotartsi.jgame.event.AnyEvent;
import com.rotartsi.jgame.math.Vector2;
import com.rotartsi.jgame.sprite.Group;
import com.rotartsi.jgame.sprite.Sprite;
import com.rotartsi.jgame.util.ScreenBounds;
import com.rotartsi.jgame.util.SettingsBundle;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.LinkedList;

/*
import pygame
import math
from roengine.util import Dummy
from roengine.config import PLAYER_KEYBINDS, USE_ROTOZOOM

__all__ = ["PlatformerEntity"]


class PlatformerEntity(pygame.sprite.Sprite):
    keybinds = PLAYER_KEYBINDS

    speed = 5
    jump_power = 10
    gravity = 0.5

    climb_skill = 1
    climb_velocity = 5
    term_y = 10

    bounds_checks = ('+y', '-x', '+x')
    collidables = pygame.sprite.Group()

    def __init__(self, image, pos=(0, 0)):
        pygame.sprite.Sprite.__init__(self)

        self.position = pygame.math.Vector2(0, 0)
        self.velocity = pygame.math.Vector2(0, 0)
        self.rotation = 0

        self.is_climbing = False
        self.grounded = False
        self.firing = False
        self.bounds = None

        self.input_state = {"forward": False, "backward": False, "jump": False}

        self.image = image
        self.master_image = image
        self.rect = self.image.get_rect()
        self.rect.center = pos

    def update(self):
        self.grounded = False
        self.is_climbing = False
        self.check_y_collisions()
        if self.bounds is not None:
            self.check_bounds(self.bounds, ('+y', '-y') if "+y" in self.bounds_checks else ('-y', ))
        self.update_input_state()
        self.apply_gravity()
        self.clamp_velocity()
        if self.bounds is not None:
            self.check_bounds(self.bounds, ('-y', ))
        self.position.y += self.velocity.y
        self.update_rect()

    def update_rot(self, target_pos, scale=1.0, update_rect=False):
        delta_pos = [target_pos[0] - self.position.x, target_pos[1] - self.position.y]
        self.rotation = math.degrees(math.atan2(-delta_pos[1], delta_pos[0])) - 90
        if USE_ROTOZOOM:
            self.image = pygame.transform.rotozoom(self.master_image, self.rotation, scale)
        else:
            self.image = pygame.transform.rotate(self.master_image, self.rotation)
        if update_rect:
            self.rect = self.image.get_rect()
            self.update_rect()

    def update_event(self, event):
        if event.type == pygame.MOUSEBUTTONDOWN:
            self.firing = True
        if event.type == pygame.MOUSEBUTTONUP:
            self.firing = False
        if event.type == pygame.KEYDOWN:
            if event.key in self.keybinds['forward']:
                self.input_state["forward"] = True
            if event.key in self.keybinds['backward']:
                self.input_state["backward"] = True
            if event.key in self.keybinds["jump"]:
                self.input_state["jump"] = True
        if event.type == pygame.KEYUP:
            if event.key in self.keybinds['forward']:
                self.input_state["forward"] = False
            if event.key in self.keybinds['backward']:
                self.input_state["backward"] = False
            if event.key in self.keybinds["jump"]:
                self.input_state["jump"] = False

    def clamp_velocity(self):
        self.velocity.y = max(-self.term_y, min(self.velocity.y, self.term_y))

    def apply_gravity(self):
        if not (self.grounded or self.is_climbing):
            self.velocity.y += self.gravity

    def update_input_state(self):
        if self.input_state["forward"]:
            self.position.x += self.speed
            self.update_rect()
            self.check_px_cols()
            if self.bounds is not None and "+x" in self.bounds_checks:
                self.check_bounds(self.bounds, ('+x',))
        if self.input_state["backward"]:
            self.position.x -= self.speed
            self.update_rect()
            self.check_nx_cols()
            if self.bounds is not None and "-x" in self.bounds_checks:
                self.check_bounds(self.bounds, ('-x',))
        if self.input_state["jump"] and self.grounded:
            self.velocity.y -= self.jump_power

        if self.is_climbing:
            self.velocity.y = -self.climb_velocity

    def update_rect(self):
        self.rect.center = [int(self.position.x), int(self.position.y)]

    def update_pos(self):
        self.position = pygame.math.Vector2(self.rect.center)

    def check_y_collisions(self):
        self.update_rect()
        if self.velocity.y > 0:
            hit = pygame.sprite.spritecollide(self, self.collidables, False)
            if hit:
                getattr(hit[0], 'on_collide', Dummy)('+y', self)
                self.rect.bottom = hit[0].rect.top
                self.update_pos()
                self.grounded = True
                self.velocity.y = 0
        if self.velocity.y < 0:
            hit = pygame.sprite.spritecollide(self, self.collidables, False)
            if hit:
                getattr(hit[0], 'on_collide', Dummy)('-y', self)
                self.rect.top = hit[0].rect.bottom
                self.update_pos()
                self.velocity.y = 0

    def check_px_cols(self):
        self.update_rect()
        hit = pygame.sprite.spritecollide(self, self.collidables, False)
        if hit:
            getattr(hit[0], 'on_collide', Dummy)('+x', self)
            self.is_climbing = getattr(hit[0], 'climb_difficulty', float('inf')) <= self.climb_skill
            self.rect.right = hit[0].rect.left
            self.update_pos()

    def check_nx_cols(self):
        self.update_rect()
        hit = pygame.sprite.spritecollide(self, self.collidables, False)
        if hit:
            getattr(hit[0], 'on_collide', Dummy)('-x', self)
            self.is_climbing = getattr(hit[0], 'climb_difficulty', float('inf')) <= self.climb_skill
            self.rect.left = hit[0].rect.right
            self.update_pos()

    def check_bounds(self, surface, checks=("+y", "-y", "+x", "-x")):
        self.update_rect()
        if self.rect.left < 0 and "-x" in checks:
            self.is_climbing = True
            self.rect.left = 0
        if self.rect.right > surface.get_width() and "+x" in checks:
            self.is_climbing = True
            self.rect.right = surface.get_width()

        if self.rect.top < 0 and "-y" in checks:
            self.rect.top = 0
            self.velocity.y = 0
        if self.rect.bottom > surface.get_height() and "+y" in checks:
            self.rect.bottom = surface.get_height()
            self.velocity.y = 0
            self.grounded = True
        self.position = pygame.math.Vector2(self.rect.center)
        #self.update_rect()

 */

/**
 * Not yet implemented...
 * TODO: Implement this
 */
public class PlatformerEntity extends Sprite {
    public double speed = 5;
    public double climbSpeed = 5;

    public double speedMult = 1;
    public double gravityMult = 1;
    public double jumpMult = 1;
    public double airFriction = 0.025;

    public double jumpPower = 0;
    public double gravity = 0;

    public int climbSkill = 1;

    public Vector2[] terminalVelocity = new Vector2[]{new Vector2(10, 10), new Vector2(-10, -10)};

    public SettingsBundle keybinds;

    protected HashMap<String, Boolean> internalState = new HashMap<>();

    public Group collidables;

    protected ScreenBounds bounds;

    public double frictionMult = 1;
    public double climbSpeedMult = 1;
    public Vector2[] xBounceMult = new Vector2[]{new Vector2(1, 1), new Vector2(1, 1)};
    public Vector2[] yBounceMult = new Vector2[]{new Vector2(1, 1), new Vector2(1, 1)};

    /**
     * Not yet implemented...
     *
     * @param img NOT IMPLEMENTED
     */
    public PlatformerEntity(BufferedImage img, ScreenBounds bounds) {
        super(img);

        internalState.put("forward", false);
        internalState.put("backward", false);
        internalState.put("jump", false);
        internalState.put("climb", false);
        internalState.put("grounded", false);
        //internalState.put("firing", false);
        this.bounds = bounds;
    }

    public void setBounds(ScreenBounds bounds) {
        this.bounds = bounds;
    }

    @Override
    public void update() {
        internalState.put("grounded", false);
        internalState.put("climb", false);
        checkYCollisions();
        checkBounds();
        updateInputState();
        if (!internalState.get("grounded")) {
            vel.y += gravity;
        }
        if (vel.x > 0) {
            vel.x -= airFriction;
        } else if (vel.x < 0) {
            vel.x += airFriction;
        }
        clampVelocity();
        pos.y = pos.y + vel.y;
        updateRect();
    }

    protected void checkBounds() {
        if (absPos.x < bounds.minCoords[0] && bounds.doCollide("-x")) { // - x
            internalState.put("climb", bounds.left.climbDifficulty <= climbSkill);
            pos.x = bounds.minCoords[0] + (size.x / 2);
            vel.x = 0;
            bounds.handleCollision("-x", this);
            //vel = vel.multiply(bounds.left.xBounce[1]).multiply(xBounceMult[1]);
        }
        if (absPos.y < bounds.minCoords[1] && bounds.doCollide("-y")) { // - y
            vel.y = 0;
            pos.y = bounds.minCoords[1] + (size.y / 2);
            bounds.handleCollision("-y", this);
            if (vel.x > 0) {
                vel.x -= bounds.bottom.friction * frictionMult;
            } else if (vel.x < 0) {
                vel.x += bounds.bottom.friction * frictionMult;
            }
        }
        if (pos.x + size.x / 2 > bounds.maxCoords[0] && bounds.doCollide("+x")) { // + x
            pos.x = bounds.maxCoords[0] - (size.x / 2);
            internalState.put("climb", bounds.right.climbDifficulty <= climbSkill);
            vel.x = 0;
            bounds.handleCollision("+x", this);
            //vel = vel.multiply(bounds.right.xBounce[1]).multiply(xBounceMult[1]);
        }
        if (pos.y + size.y / 2 > bounds.maxCoords[1] && bounds.doCollide("+y")) { // + y
            pos.y = bounds.maxCoords[1] - (size.y / 2);
            internalState.put("grounded", true);
            vel.y = 0;
            bounds.handleCollision("+y", this);
        }
        updateRect();
    }

    protected void clampVelocity() {
        vel.y = Math.max(terminalVelocity[1].y, Math.min(vel.y, terminalVelocity[0].y));
        vel.x = Math.max(terminalVelocity[1].x, Math.min(vel.x, terminalVelocity[0].x));
    }

    public void updateEvent(AnyEvent event) {
        if (event.matchesString(keybinds.get("keybinds.fire.activate",
                "id:mouse.pressed,button:1"))) {
            // internalState.put("firing", true);
        }
        if (event.matchesString(keybinds.get("keybinds.fire.deactivate",
                "id:mouse.released,button:1"))) {
            // internalState.put("firing", false);
        }
        if (event.matchesString(keybinds.get("keybinds.forward.activate",
                "id:key.pressed,keyCode:68"))) {
            internalState.put("forward", true);
        }
        if (event.matchesString(keybinds.get("keybinds.forward.deactivate",
                "id:key.released,keyCode:68"))) {
            internalState.put("forward", false);
        }
        if (event.matchesString(keybinds.get("keybinds.backward.activate",
                "id:key.pressed,keyCode:65"))) {
            internalState.put("backward", true);
        }
        if (event.matchesString(keybinds.get("keybinds.backward.deactivate",
                "id:key.released,keyCode:65"))) {
            internalState.put("backward", false);
        }
        if (event.matchesString(keybinds.get("keybinds.jump.activate",
                "id:key.pressed,keyCode:32"))) {
            internalState.put("jump", true);
        }
        if (event.matchesString(keybinds.get("keybinds.jump.deactivate",
                "id:key.released,keyCode:32"))) {
            internalState.put("jump", false);
        }
    }

    private void updateInputState() {
        if (internalState.get("forward")) {
            vel.x += speed;
        }
        if (internalState.get("backward")) {
            vel.x -= speed;
        }
        if (internalState.get("jump") && internalState.get("grounded")) {
            vel.y -= jumpPower;
        }

        if (vel.x < 0) {
            clampVelocity();
            pos.x += vel.x;
            updateRect();
            checkNXCollisions();
            // checkBounds();
        } else if (vel.x > 0) {
            clampVelocity();
            pos.x += vel.x;
            updateRect();
            checkPXCollisions();
            // checkBounds();
        }
//        System.out.println("pos = " + pos);

        if (internalState.get("climb")) {
            vel.y -= climbSpeed;
        }
    }

    private void checkPXCollisions() {
        LinkedList<Sprite> cols = collidesWith(collidables);
        if (cols.size() > 0) {
            Sprite c = cols.get(0);
            if (c instanceof PlatformerObstacle) {
                PlatformerObstacle col = (PlatformerObstacle) c;
                if (!col.doCollide("+x")) {
                    return;
                }
                internalState.put("climb", (col.climbDifficulty <= climbSkill));
                col.updateRect();
                pos.x = col.absPos.x - (size.x / 2d);
                updateRect();
                vel.x = 0;
                col.onCollide("+x", this);
//                vel = vel.multiply(col.xBounce[0]).multiply(xBounceMult[0]);
            }
        }
    }

    private void checkNXCollisions() {
        LinkedList<Sprite> cols = collidesWith(collidables);
        if (cols.size() > 0) {
            Sprite c = cols.get(0);
            if (c instanceof PlatformerObstacle) {
                PlatformerObstacle col = (PlatformerObstacle) c;
                if (!col.doCollide("-x")) {
                    return;
                }
                internalState.put("climb", (col.climbDifficulty <= climbSkill));
                pos.x = (col.pos.x + (col.size.x / 2)) + (size.x / 2d);
                updateRect();
                vel.x = 0;
                col.onCollide("-x", this);
//                vel = vel.multiply(col.xBounce[0]).multiply(xBounceMult[0]);
            }
        }
    }

    protected void checkYCollisions() {
        if (vel.y > 0) {
            LinkedList<Sprite> cols = collidesWith(collidables);
            if (cols.size() > 0) {
                Sprite c = cols.get(0);
                if (c instanceof PlatformerObstacle) {
                    PlatformerObstacle col = (PlatformerObstacle) c;
                    if (!col.doCollide("+y")) {
                        return;
                    }
                    pos.y = col.absPos.y - (size.y / 2d);
                    updateRect();
                    internalState.put("grounded", true);
                    vel.y = 0;
                    col.onCollide("+y", this);
//                    vel = vel.multiply(col.yBounce[1]).multiply(yBounceMult[1]);
                    if (vel.x > 0) {
                        vel.x -= col.friction * frictionMult;
                    } else if (vel.x < 0) {
                        vel.x += col.friction * frictionMult;
                    }
                }
            }
        } else if (vel.y < 0) {
            LinkedList<Sprite> cols = collidesWith(collidables);
            if (cols.size() > 0) {
                Sprite c = cols.get(0);
                if (c instanceof PlatformerObstacle) {
                    PlatformerObstacle col = (PlatformerObstacle) c;
                    if (!col.doCollide("-y")) {
                        return;
                    }
                    col.onCollide("-y", this);
                    pos.y = (col.pos.y + (col.size.y / 2)) + (size.y / 2d);
                    updateRect();
                    vel.y = 0;
//                    vel = vel.multiply(col.yBounce[0]).multiply(yBounceMult[0]);
                }
            }
        }
    }
}
