package io.github.jgame.sprite;

import io.github.jgame.image.ImageManager;
import io.github.jgame.math.Vector2;

import java.awt.*;

/**
 * TextSprite. Very resource intensive. Avoid if possible. Further optimization is needed.
 */
public class TextSprite extends Sprite {
    /**
     * The message displayed. Updatable directly, but call {@link #updateImageFromProperties()} to update image.
     */
    private String text;

    /**
     * The font used. Updatable directly, but call {@link #updateImageFromProperties()} to update image.
     */
    private Font font;

    /**
     * The color of the text. Updatable directly, but call {@link #updateImageFromProperties()} to update image.
     */
    private Color textColor;

    /**
     * Create text!
     *
     * @param string     The string to display
     * @param spriteFont The font to use
     * @param color      The color to blit it in.
     */
    public TextSprite(String string, Font spriteFont, Color color) {
        super(ImageManager.fromText(string, spriteFont, color));

        font = spriteFont;
        textColor = color;
        text = string;
    }

    /**
     * Update the properties of the text.
     *
     * @param string New string.
     * @param spriteFont New font.
     * @param color New color.
     */
    public void updateText(String string, Font spriteFont, Color color) {
        font = spriteFont;
        textColor = color;
        text = string;
        updateImageFromProperties();
    }

    /**
     * Simply update the message.
     *
     * @param string New string.
     */
    public void updateText(String string) {
        text = string;
        updateImageFromProperties();
    }

    /**
     * Get a new image from the current properties of the {@code TextSprite}
     */
    private void updateImageFromProperties() {
        image = ImageManager.fromText(text, font, textColor);
        size = new Vector2(image.getWidth(null), image.getHeight(null));
        super.updateRect();
    }
}
