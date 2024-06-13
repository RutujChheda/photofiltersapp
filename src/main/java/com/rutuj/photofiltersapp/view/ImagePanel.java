package com.rutuj.photofiltersapp.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage image;
    private double scale;
    private int xOffset;
    private int yOffset;
    private int xDragStart;
    private int yDragStart;

    public ImagePanel() {
        this.scale = 1.0;
        this.xOffset = 0;
        this.yOffset = 0;

        addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    zoomIn();
                } else {
                    zoomOut();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                xDragStart = e.getX() - xOffset;
                yDragStart = e.getY() - yOffset;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                xOffset = e.getX() - xDragStart;
                yOffset = e.getY() - yDragStart;
                repaint();
            }
        });
    }

    public void setImage(BufferedImage image) {
        this.image = image;
        this.scale = 1.0;
        this.xOffset = 0;
        this.yOffset = 0;
        repaint();
    }

    public void zoomIn() {
        scale *= 1.1;
        repaint();
    }

    public void zoomOut() {
        scale /= 1.1;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            int width = (int) (image.getWidth() * scale);
            int height = (int) (image.getHeight() * scale);
            g.drawImage(image, xOffset, yOffset, width, height, this);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        if (image != null) {
            int width = (int) (image.getWidth() * scale);
            int height = (int) (image.getHeight() * scale);
            return new Dimension(width, height);
        } else {
            return super.getPreferredSize();
        }
    }
}
