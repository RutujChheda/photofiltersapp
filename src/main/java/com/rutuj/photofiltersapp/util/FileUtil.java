package com.rutuj.photofiltersapp.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class FileUtil {
    private FileUtil() {}

    public static BufferedImage loadImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }
}



