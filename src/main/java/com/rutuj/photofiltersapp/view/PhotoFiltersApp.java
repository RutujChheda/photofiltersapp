package com.rutuj.photofiltersapp.view;

import javax.swing.*;
import java.awt.*;

public class PhotoFiltersApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Photo Filters App");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setLayout(new BorderLayout());

            MainPanel mainPanel = new MainPanel();
            frame.add(mainPanel, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }
}
