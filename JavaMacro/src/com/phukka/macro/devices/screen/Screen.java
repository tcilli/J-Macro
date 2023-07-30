package com.phukka.macro.devices.screen;

import com.phukka.macro.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Screen {

    Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    public ImagePosition capture() {
        //System.out.println("Screen capturing is expensive, try to avoid it use captureArea to reduce complexity.");
        return new ImagePosition(Main.getRobot().createScreenCapture(screenRect), 0 ,0);
    }

    public ImagePosition captureArea(int x, int y, int x2, int y2) {
        return captureArea(new int[]{x, y, x2, y2});
    }

    public ImagePosition captureArea(int[] bounds) {
        Rectangle area = new Rectangle(bounds[0], bounds[1], (bounds[2] - bounds[0]), (bounds[3] - bounds[1]));
        return new ImagePosition(Main.getRobot().createScreenCapture(area), Math.abs(screenRect.x - area.x),  Math.abs(screenRect.y - area.y));
    }

    public void display(BufferedImage image, int screenIndex) {
        // Get the screen devices
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gdArray = ge.getScreenDevices();

        // Check if the screen index is valid
        if (screenIndex < 0 || screenIndex >= gdArray.length) {
            throw new IllegalArgumentException("Invalid screen index");
        }
        GraphicsDevice selectedScreen = gdArray[screenIndex];

        // If the JFrame is already visible, just update the image
        if (frame.isVisible()) {
            ImageIcon imageIcon = new ImageIcon(image);
            JLabel label = new JLabel(imageIcon);

            // Remove previous image from the JFrame
            frame.getContentPane().removeAll();

            // Add the label with the new captured image to the frame
            frame.getContentPane().add(label);

            // Pack the frame to fit the size of the new image
            frame.pack();

        } else {
            // If the JFrame is not visible, move it to the desired screen
            frame.setLocation(selectedScreen.getDefaultConfiguration().getBounds().getLocation());

            // Create a JLabel and set the ImageIcon with the captured image
            ImageIcon imageIcon = new ImageIcon(image);
            JLabel label = new JLabel(imageIcon);

            // Add the label to the frame
            frame.getContentPane().add(label);

            // Pack the frame to fit the size of the image
            frame.pack();

            // Make the frame visible
            frame.setVisible(true);
        }
    }

    public Screen() {
        frame = new JFrame("Captured Image");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setResizable(false);
    }

    private final JFrame frame;

}
