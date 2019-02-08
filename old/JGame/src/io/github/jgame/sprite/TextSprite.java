package io.github.jgame.sprite;

import io.github.jgame.image.ImageManager;
import io.github.jgame.math.Vector2;

import java.awt.*;

public class TextSprite extends Sprite {
    private String text;
    private Font font;
    private Color textColor;

    public TextSprite(String string, Font spriteFont, Color color) {
        super(ImageManager.fromText(string, spriteFont, color));

        font = spriteFont;
        textColor = color;
        text = string;
    }

    public void updateText(String string, Font spriteFont, Color color) {
        font = spriteFont;
        textColor = color;
        text = string;
        updateImageFromProperties();
    }

    public void updateText(String string) {
        text = string;
        updateImageFromProperties();
    }

    private void updateImageFromProperties() {
        image = ImageManager.fromText(text, font, textColor);
        size = new Vector2(image.getWidth(null), image.getHeight(null));
        absPos = pos.subtract(new Vector2(size.x / 2, size.y / 2));
        rect = new Rectangle((int) absPos.x, (int) absPos.y, (int) size.x, (int) size.y);
    }
}
