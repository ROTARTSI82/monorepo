package com.rotartsi.jgame.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import static com.rotartsi.jgame.util.StringManager.fmt;

public class AnyEvent {
    public InputEvent event;
    private Logger logger = LogManager.getLogger(AnyEvent.class);

    public MouseEvent mouseEvent;
    public KeyEvent keyEvent;
    public MouseWheelEvent mouseWheelEvent;

    public EventInType descriptor;
    public String location;
    public String id;
    public String scrollType;

    private enum EventInType {
        KEY, MOUSE, WHEEL
    }

    public AnyEvent(MouseEvent e) {
        event = e;
        mouseEvent = e;
        descriptor = EventInType.MOUSE;
        assignID();
    }

    public AnyEvent(KeyEvent e) {
        event = e;
        keyEvent = e;
        descriptor = EventInType.KEY;
        assignID();
    }

    public AnyEvent(MouseWheelEvent e) {
        event = e;
        mouseWheelEvent = e;
        mouseEvent = mouseWheelEvent;
        descriptor = EventInType.WHEEL;
        assignID();
    }

    public AnyEvent(InputEvent e) {
        event = e;
        if (e instanceof KeyEvent) {
            keyEvent = (KeyEvent) e;
            descriptor = EventInType.KEY;
            return;
        }
        if (e instanceof MouseWheelEvent) {
            mouseWheelEvent = (MouseWheelEvent) e;
            mouseEvent = mouseWheelEvent;
            descriptor = EventInType.WHEEL;
            return;
        }
        if (e instanceof MouseEvent) {
            mouseEvent = (MouseEvent) e;
            descriptor = EventInType.MOUSE;
        }
        assignID();
    }

    private void assignID() {
        switch (event.getID()) {
            case (KeyEvent.KEY_PRESSED): {
                id = "key.pressed";
                break;
            }
            case (KeyEvent.KEY_RELEASED): {
                id = "key.released";
                break;
            }
            case (KeyEvent.KEY_TYPED): {
                id = "key.typed";
                break;
            }
            case (MouseEvent.MOUSE_RELEASED): {
                id = "mouse.released";
                break;
            }
            case (MouseEvent.MOUSE_PRESSED): {
                id = "mouse.pressed";
                break;
            }
            case (MouseEvent.MOUSE_MOVED): {
                id = "mouse.moved";
                break;
            }
            case (MouseEvent.MOUSE_EXITED): {
                id = "mouse.exit";
                break;
            }
            case (MouseEvent.MOUSE_ENTERED): {
                id = "mouse.enter";
                break;
            }
            case (MouseEvent.MOUSE_DRAGGED): {
                id = "mouse.dragged";
                break;
            }
            case (MouseEvent.MOUSE_CLICKED): {
                id = "mouse.clicked";
                break;
            }
            case (MouseWheelEvent.MOUSE_WHEEL): {
                id = "wheel.moved";
                break;
            }
            default: {
                id = "unknown";
                break;
            }
        }
        if (descriptor == EventInType.KEY) {
            switch (keyEvent.getKeyLocation()) {
                case KeyEvent.KEY_LOCATION_STANDARD:
                    location = "standard";
                    break;
                case KeyEvent.KEY_LOCATION_LEFT:
                    location = "left";
                    break;
                case KeyEvent.KEY_LOCATION_RIGHT:
                    location = "right";
                    break;
                case KeyEvent.KEY_LOCATION_NUMPAD:
                    location = "numpad";
                    break;
                default:
                case KeyEvent.KEY_LOCATION_UNKNOWN:
                    location = "unknown";
                    break;
            }
        }
        if (descriptor == EventInType.WHEEL) {
            switch (mouseWheelEvent.getScrollType()) {
                case MouseWheelEvent.WHEEL_UNIT_SCROLL: {
                    scrollType = "unit";
                    break;
                }
                case MouseWheelEvent.WHEEL_BLOCK_SCROLL: {
                    scrollType = "block";
                }
                default: {
                    scrollType = "unknown";
                }
            }
        }
    }

    public boolean matchesString(String str) {
        boolean ret = true;
        try {
            for (String event : str.split(";")) {
                ret &= matchesSingleEvent(event);
            }

        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            logger.warn(fmt("Skipping bad event string: %s", str), e);
            return false;
        }
        return ret;
    }

    private boolean matchesSingleEvent(String event) {
        try {
            for (String i : event.split(",")) {
                String[] pair = i.split(":");
                switch (pair[0]) {
                    case "id": {
                        if (!pair[1].equals(id)) {
                            return false;
                        }
                        break;
                    }
                    case "mods": {
                        if (!pair[1].equals("" + this.event.getModifiersEx())) {
                            return false;
                        }
                        break;
                    }
                    case "button": {
                        if (!pair[1].equals("" + mouseEvent.getButton())) {
                            return false;
                        }
                        break;
                    }
                    case "keyCode": {
                        if (!pair[1].equals("" + keyEvent.getKeyCode())) {
                            return false;
                        }
                        break;
                    }
                    case "scrollDirection": {
                        if (!pair[1].equals("" + Math.min(1, Math.max(mouseWheelEvent.getUnitsToScroll(), -1)))) {
                            return false;
                        }
                        break;
                    }
                    case "absPos": {
                        if (!pair[1].equals(fmt("(%s, %s)", mouseEvent.getXOnScreen(),
                                mouseEvent.getYOnScreen()))) {
                            return false;
                        }
                        break;
                    }
                    case "pos": {
                        if (!pair[1].equals(fmt("(%s, %s)", mouseEvent.getX(), mouseEvent.getY()))) {
                            return false;
                        }
                        break;
                    }
                    case "scrollType": {
                        if (!pair[1].equals(scrollType)) {
                            return false;
                        }
                        break;
                    }
                    case "scrollAmount": {
                        if (!pair[1].equals("" + mouseWheelEvent.getScrollAmount())) {
                            return false;
                        }
                        break;
                    }
                    case "wheelRot": {
                        if (!pair[1].equals("" + mouseWheelEvent.getWheelRotation())) {
                            return false;
                        }
                        break;
                    }
                    case "preciseWheelRot": {
                        if (!pair[1].equals("" + mouseWheelEvent.getPreciseWheelRotation())) {
                            return false;
                        }
                        break;
                    }
                    case "scrollUnits": {
                        if (!pair[1].equals("" + mouseWheelEvent.getUnitsToScroll())) {
                            return false;
                        }
                        break;
                    }
                    case "keyLocation": {
                        if (!pair[1].equals(location)) {
                            return false;
                        }
                        break;
                    }
                    case "clicks": {
                        if (!pair[1].equals("" + mouseEvent.getClickCount())) {
                            return false;
                        }
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            logger.warn(fmt("Skipping bad event string: %s", event), e);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("id:").append(id);
        builder.append(",mods:").append(event.getModifiersEx());
        if (descriptor == EventInType.KEY) {
            builder.append(",keyCode:").append(keyEvent.getKeyCode());
            builder.append(",keyLocation:").append(location);
        }
        if (descriptor == EventInType.MOUSE || descriptor == EventInType.WHEEL) {
            builder.append(",button:").append(mouseEvent.getButton());
            builder.append(",clicks:").append(mouseEvent.getClickCount());
        }
        if (descriptor == EventInType.WHEEL) {
            builder.append(",scrollDirection:").append(
                    Math.min(1, Math.max(mouseWheelEvent.getUnitsToScroll(), -1)));
        }
        return builder.toString();
    }
}
