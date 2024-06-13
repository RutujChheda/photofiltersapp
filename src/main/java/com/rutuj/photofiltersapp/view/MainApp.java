package com.rutuj.photofiltersapp.view;

import com.rutuj.photofiltersapp.model.ConversionType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class MainApp {
    private static JLabel originalImageLabel;
    private static JLabel filteredImageLabel;
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

        JPanel topButtonPanel = new JPanel();
        topButtonPanel.add(openButton);
        topButtonPanel.add(saveButton);

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

        originalImageLabel = new JLabel();
        filteredImageLabel = new JLabel();

        JPanel imagePanel = new JPanel(new GridLayout(1, 2));
        imagePanel.add(new JScrollPane(originalImageLabel));
        imagePanel.add(new JScrollPane(filteredImageLabel));

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
            originalImageLabel.setIcon(new ImageIcon(originalImage));
            filteredImage = ImageHandler.copyImage(originalImage);
            filteredImageLabel.setIcon(new ImageIcon(filteredImage));
            // Apply all filters and store the results for later use
            imageHandler.applyFiltersAndStoreResults(originalImage, () -> {
                // Update UI once filters are applied and results are stored
                SwingUtilities.invokeLater(() -> {
                    // Any additional UI updates after processing
                });
            });
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
            filteredImage = imageHandler.getStoredResult(conversionType);
            if (filteredImage != null) {
                filteredImageLabel.setIcon(new ImageIcon(filteredImage));
            } else {
                JOptionPane.showMessageDialog(null, "Failed to apply filter.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please upload an image first.");
        }
    }
}


