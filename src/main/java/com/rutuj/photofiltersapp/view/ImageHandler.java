package com.rutuj.photofiltersapp.view;

import com.google.common.collect.ImmutableList;
import com.rutuj.photofiltersapp.activity.ConvertPhotoActivity;
import com.rutuj.photofiltersapp.dependency.DaggerServiceComponent;
import com.rutuj.photofiltersapp.dependency.ServiceComponent;
import com.rutuj.photofiltersapp.model.ConversionType;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageHandler {
    private final ExecutorService executorService;
    private final ConvertPhotoActivity activity;
    private final ServiceComponent dagger;
    private final EnumMap<ConversionType, BufferedImage> storedResults = new EnumMap<>(ConversionType.class);

    public ImageHandler() {
        this.executorService = Executors.newSingleThreadExecutor();
        this.dagger = DaggerServiceComponent.create();
        this.activity = dagger.provideConvertPhotoActivity();
    }

    public BufferedImage loadImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                clearStoredResults(); // Clear stored results when a new image is loaded
                return ImageIO.read(file);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to load image. Please check the file path and try again.");
            }
        }
        return null;
    }

    public void saveImage(BufferedImage image) {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image files", ImageIO.getWriterFileSuffixes());
        fileChooser.setFileFilter(filter);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, FilenameUtils.getExtension(file.getName()), file);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Failed to save image.");
            }
        }
    }

    public void applyFiltersAndStoreResults(BufferedImage originalImage, Runnable callback) {
        executorService.submit(() -> {
            try {
                System.out.println("Applying filters to the new image...");
                BufferedImage imageToFilter = copyImage(originalImage);
                File tempFile = File.createTempFile("tempImage", ".png");
                tempFile.deleteOnExit();
                ImageIO.write(imageToFilter, "png", tempFile);

                List<String> resultPaths = activity.handleRequest(tempFile.getAbsolutePath(),
                        ImmutableList.of(ConversionType.GREYSCALE, ConversionType.SEPIA, ConversionType.INVERSION));

                System.out.println("Filter results paths: " + resultPaths);

                if (resultPaths.size() == 3) {
                    BufferedImage greyscaleImage = readImage(resultPaths.get(0));
                    BufferedImage sepiaImage = readImage(resultPaths.get(1));
                    BufferedImage inversionImage = readImage(resultPaths.get(2));

                    if (greyscaleImage != null && sepiaImage != null && inversionImage != null) {
                        storedResults.put(ConversionType.GREYSCALE, greyscaleImage);
                        storedResults.put(ConversionType.SEPIA, sepiaImage);
                        storedResults.put(ConversionType.INVERSION, inversionImage);
                        System.out.println("Filters applied and images stored.");
                        SwingUtilities.invokeLater(callback);
                    } else {
                        System.out.println("One or more images could not be read.");
                        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Failed to apply filters: one or more images could not be read."));
                    }
                } else {
                    System.out.println("Unexpected number of result paths: " + resultPaths.size());
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Failed to apply filters: not all results were returned."));
                }
            } catch (IOException e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Failed to apply filters."));
            }
        });
    }

    private BufferedImage readImage(String path) {
        try {
            System.out.println("Reading image from path: " + path);
            BufferedImage img = ImageIO.read(new File(path));
            if (img == null) {
                System.out.println("ImageIO.read returned null for path: " + path);
            } else {
                System.out.println("Successfully read image from path: " + path);
            }
            return img;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public BufferedImage getStoredResult(ConversionType conversionType) {
        return storedResults.get(conversionType);
    }

    public static BufferedImage copyImage(BufferedImage source) {
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    public void clearStoredResults() {
        storedResults.clear();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}


