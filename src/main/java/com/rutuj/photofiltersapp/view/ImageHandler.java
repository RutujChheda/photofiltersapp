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
import java.util.function.Consumer;

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
                BufferedImage imageToFilter = copyImage(originalImage);
                File tempFile = File.createTempFile("tempImage", ".png");
                tempFile.deleteOnExit();
                ImageIO.write(imageToFilter, "png", tempFile);
                List<String> resultPaths = activity.handleRequest(tempFile.getAbsolutePath(),
                        ImmutableList.of(ConversionType.INVERSION, ConversionType.GREYSCALE, ConversionType.SEPIA));
                if (resultPaths.size() == 3) {
                    storedResults.put(ConversionType.INVERSION, ImageIO.read(new File(resultPaths.get(0))));
                    storedResults.put(ConversionType.GREYSCALE, ImageIO.read(new File(resultPaths.get(1))));
                    storedResults.put(ConversionType.SEPIA, ImageIO.read(new File(resultPaths.get(2))));
                    SwingUtilities.invokeLater(callback);
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(null, "Failed to apply filters."));
            }
        });
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

    public void shutdown() {
        executorService.shutdown();
    }
}
