package com.rutuj.photofiltersapp.view;

import com.google.common.collect.ImmutableList;
import com.rutuj.photofiltersapp.activity.ConvertPhotoActivity;
import com.rutuj.photofiltersapp.dependency.DaggerServiceComponent;
import com.rutuj.photofiltersapp.dependency.ServiceComponent;
import com.rutuj.photofiltersapp.model.ConversionType;
import com.rutuj.photofiltersapp.util.FileUtil;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainPanel extends JPanel {

    private JLabel filePathLabel;
    private JButton openButton;
    private JButton saveButton;
    private JButton applyGreyscaleButton;
    private JButton applyInversionButton;
    private JButton applySepiaButton;
    private DraggableZoomablePanel originalImagePanel;
    private DraggableZoomablePanel filteredImagePanel;
    private BufferedImage originalImage;
    private BufferedImage filteredImage;
    private File selectedFile;
    private List<String> filteredImagePaths;

    private static final ServiceComponent DAGGER = DaggerServiceComponent.create();

    public MainPanel() {
        setLayout(new BorderLayout());

        // Create top panel with file buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        openButton = new JButton("Open");
        saveButton = new JButton("Save");
        saveButton.setEnabled(false);
        topPanel.add(openButton);
        topPanel.add(saveButton);
        add(topPanel, BorderLayout.NORTH);

        // Create image panels
        originalImagePanel = new DraggableZoomablePanel();
        originalImagePanel.setBorder(BorderFactory.createTitledBorder("Original Image"));
        filteredImagePanel = new DraggableZoomablePanel();
        filteredImagePanel.setBorder(BorderFactory.createTitledBorder("Filtered Image"));
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(originalImagePanel), new JScrollPane(filteredImagePanel));
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // Create bottom panel with filter buttons
        JPanel bottomPanel = new JPanel(new GridLayout(1, 3));
        applyGreyscaleButton = new JButton("Apply Greyscale");
        applyInversionButton = new JButton("Apply Inversion");
        applySepiaButton = new JButton("Apply Sepia");
        bottomPanel.add(applyGreyscaleButton);
        bottomPanel.add(applyInversionButton);
        bottomPanel.add(applySepiaButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveFilteredImages();
            }
        });

        applyGreyscaleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter(ConversionType.GREYSCALE);
            }
        });

        applyInversionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter(ConversionType.INVERSION);
            }
        });

        applySepiaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                applyFilter(ConversionType.SEPIA);
            }
        });
    }

    private void chooseFile() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "bmp", "gif");
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            try {
                originalImage = FileUtil.loadImage(selectedFile);
                originalImagePanel.setImage(originalImage);
                saveButton.setEnabled(false);
                // Reset filtered image paths
                filteredImagePaths = null;
                filteredImagePanel.setImage(null);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void applyFilter(ConversionType conversionType) {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "Please choose a file first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<ConversionType> conversions = new ArrayList<>();
        conversions.add(conversionType);

        ConvertPhotoActivity activity = DAGGER.provideConvertPhotoActivity();
        filteredImagePaths = activity.handleRequest(selectedFile.getAbsolutePath(), conversions);

        try {
            filteredImage = FileUtil.loadImage(new File(filteredImagePaths.get(0)));
            filteredImagePanel.setImage(filteredImage);
            saveButton.setEnabled(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error displaying filtered image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveFilteredImages() {
        if (filteredImagePaths == null || filteredImagePaths.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No filtered images to save.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean saved = false;
        for (String path : filteredImagePaths) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(path));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png", "bmp", "gif");
            fileChooser.setFileFilter(filter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();
                try {
                    String fileName = saveFile.getName().toLowerCase();
                    if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") && !fileName.endsWith(".png") && !fileName.endsWith(".bmp") && !fileName.endsWith(".gif")) {
                        JOptionPane.showMessageDialog(this, "Please choose a valid image file format.", "Error", JOptionPane.ERROR_MESSAGE);
                        continue;
                    }
                    File sourceFile = new File(path);
                    FileUtil.copyFile(sourceFile, saveFile);
                    saved = true;
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Error saving filtered image: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        if (saved) {
            JOptionPane.showMessageDialog(this, "Filtered images saved successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class DraggableZoomablePanel extends JPanel {
        private BufferedImage image;
        private Point imageCorner;
        private Point prevPt;
        private double zoomFactor = 1.0;

        public DraggableZoomablePanel() {
            this.imageCorner = new Point(0, 0);
            ClickListener clickListener = new ClickListener();
            DragListener dragListener = new DragListener();
            ZoomListener zoomListener = new ZoomListener();
            this.addMouseListener(clickListener);
            this.addMouseMotionListener(dragListener);
            this.addMouseWheelListener(zoomListener);
        }

        public void setImage(BufferedImage image) {
            this.image = image;
            this.imageCorner = new Point(0, 0);
            this.zoomFactor = 1.0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int width = (int) (image.getWidth() * zoomFactor);
                int height = (int) (image.getHeight() * zoomFactor);
                g.drawImage(image, imageCorner.x, imageCorner.y, width, height, this);
            }
        }

        private class ClickListener extends MouseAdapter {
            public void mousePressed(MouseEvent e) {
                prevPt = e.getPoint();
            }
        }

        private class DragListener extends MouseMotionAdapter {
            public void mouseDragged(MouseEvent e) {
                Point currentPt = e.getPoint();
                imageCorner.translate(
                        (int) ((currentPt.x - prevPt.x)),
                        (int) ((currentPt.y - prevPt.y))
                );
                prevPt = currentPt;
                repaint();
            }
        }

        private class ZoomListener extends MouseAdapter {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double factor = (e.getWheelRotation() < 0) ? 1.1 : 0.9;
                zoomFactor *= factor;

                int mouseX = e.getX();
                int mouseY = e.getY();

                imageCorner.x = (int) (mouseX - (mouseX - imageCorner.x) * factor);
                imageCorner.y = (int) (mouseY - (mouseY - imageCorner.y) * factor);

                repaint();
            }
        }
    }
}
