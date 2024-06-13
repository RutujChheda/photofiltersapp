package com.rutuj.photofiltersapp.view;

import com.rutuj.photofiltersapp.model.ConversionType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class MainApp {
    private static ImagePanel originalImagePanel;
    private static ImagePanel filteredImagePanel;
    private static BufferedImage originalImage;
    private static BufferedImage filteredImage;
    private static ImageHandler imageHandler;

    public static void main(String[] args) {
        imageHandler = new ImageHandler();
        SwingUtilities.invokeLater(MainApp::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Photo Filters App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton openButton = new JButton("Open");
        openButton.addActionListener(MainApp::openImage);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(MainApp::saveImage);

        JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.addActionListener(e -> zoomIn());

        JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.addActionListener(e -> zoomOut());

        JPanel topButtonPanel = new JPanel();
        topButtonPanel.add(openButton);
        topButtonPanel.add(saveButton);
        topButtonPanel.add(zoomInButton);
        topButtonPanel.add(zoomOutButton);

        JButton greyscaleButton = new JButton("Apply Greyscale");
        greyscaleButton.addActionListener(e -> applyFilter(ConversionType.GREYSCALE));

        JButton inversionButton = new JButton("Apply Inversion");
        inversionButton.addActionListener(e -> applyFilter(ConversionType.INVERSION));

        JButton sepiaButton = new JButton("Apply Sepia");
        sepiaButton.addActionListener(e -> applyFilter(ConversionType.SEPIA));

        JPanel bottomButtonPanel = new JPanel();
        bottomButtonPanel.add(greyscaleButton);
        bottomButtonPanel.add(inversionButton);
        bottomButtonPanel.add(sepiaButton);

        originalImagePanel = new ImagePanel();
        filteredImagePanel = new ImagePanel();

        JPanel imagePanel = new JPanel(new GridLayout(1, 2));
        imagePanel.add(new JScrollPane(originalImagePanel));
        imagePanel.add(new JScrollPane(filteredImagePanel));

        frame.add(topButtonPanel, BorderLayout.NORTH);
        frame.add(imagePanel, BorderLayout.CENTER);
        frame.add(bottomButtonPanel, BorderLayout.SOUTH);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                imageHandler.shutdown();
            }
        });

        frame.pack();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximize the window
        frame.setVisible(true);
    }

    private static void openImage(ActionEvent event) {
        originalImage = imageHandler.loadImage();
        if (originalImage != null) {
            originalImagePanel.setImage(originalImage);
            filteredImage = ImageHandler.copyImage(originalImage);
            filteredImagePanel.setImage(filteredImage);
            imageHandler.clearStoredResults();
            imageHandler.applyFiltersAndStoreResults(originalImage, () -> SwingUtilities.invokeLater(() -> {
                System.out.println("Filters applied and results stored.");
                filteredImagePanel.setImage(ImageHandler.copyImage(originalImage));
            }));
        }
    }

    private static void saveImage(ActionEvent event) {
        if (filteredImage != null) {
            imageHandler.saveImage(filteredImage);
        } else {
            JOptionPane.showMessageDialog(null, "No filtered image to save.");
        }
    }

    private static void applyFilter(ConversionType conversionType) {
        if (originalImage != null) {
            BufferedImage result = imageHandler.getStoredResult(conversionType);
            if (result != null) {
                filteredImage = result;
                filteredImagePanel.setImage(filteredImage);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to apply filter.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please upload an image first.");
        }
    }

    private static void zoomIn() {
        originalImagePanel.zoomIn();
        filteredImagePanel.zoomIn();
    }

    private static void zoomOut() {
        originalImagePanel.zoomOut();
        filteredImagePanel.zoomOut();
    }
}

