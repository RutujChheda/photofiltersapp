package com.rutuj.photofiltersapp.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class ImageUtil {
    private ImageUtil() {}

    public static BufferedImage loadImage(String filePath) throws IOException {
        File input = new File(filePath);
        return ImageIO.read(input);
    }

    public static BufferedImage loadImage(File file) throws IOException {
        return ImageIO.read(file);
    }
}
