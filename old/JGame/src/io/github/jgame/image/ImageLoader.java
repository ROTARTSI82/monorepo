package io.github.jgame.image;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

public class ImageLoader {
    public ImageLoader() {
    }

    public Image fromFile(String file) throws IOException {
        URL url = this.getClass().getClassLoader().getResource(file);
        assert url != null;
        return new ImageIcon(ImageIO.read(url)).getImage();
    }

    public Image fromUrl(String url) throws IOException {
        URL urlObj = new URL(url);
        return new ImageIcon(ImageIO.read(urlObj)).getImage();
    }
}
