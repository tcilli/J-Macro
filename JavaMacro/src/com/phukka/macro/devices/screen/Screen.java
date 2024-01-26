package com.phukka.macro.devices.screen;

import com.phukka.macro.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;


public class Screen {

    static Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());

    public static ImagePosition capture() {
        //System.out.println("Screen capturing is expensive, try to avoid it use captureArea to reduce complexity.");
        return new ImagePosition(Main.getRobot().createScreenCapture(screenRect), 0, 0);
    }

    public static ImagePosition captureArea(int x, int y, int x2, int y2) {
        return captureArea(new int[]{x, y, x2, y2});
    }

    public static ImagePosition captureArea(int[] bounds) {
        Rectangle area = new Rectangle(bounds[0], bounds[1], (bounds[2] - bounds[0]), (bounds[3] - bounds[1]));
        return new ImagePosition(Main.getRobot().createScreenCapture(area), Math.abs(screenRect.x - area.x), Math.abs(screenRect.y - area.y));
    }

    public void display(BufferedImage image, int screenIndex) {

        if (image == null) {
            throw new IllegalArgumentException("Null image");
        }
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


    private static final double ZOOM_FACTOR = 1.1; // Zoom factor change on each step
    private static double zoomLevel = 1.0; // Initial zoom level

    public void displayPIP(BufferedImage image, int[] coordinates, int screenIndex, float scale, String name) {
        // Get the screen devices
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gdArray = ge.getScreenDevices();

        zoomLevel = scale;
        // Check if the screen index is valid
        if (screenIndex < 0 || screenIndex >= gdArray.length) {
            throw new IllegalArgumentException("Invalid screen index");
        }
        GraphicsDevice selectedScreen = gdArray[screenIndex];

        // Calculate zoomed dimensions
        int zoomedWidth = (int) (image.getWidth() * zoomLevel);
        int zoomedHeight = (int) (image.getHeight() * zoomLevel);

        ImageIcon imageIcon = new ImageIcon(image.getScaledInstance(zoomedWidth, zoomedHeight, Image.SCALE_DEFAULT));
        JLabel label = new JLabel(imageIcon);

        // Remove previous components from the JFrame
        frame.getContentPane().removeAll();

        JPanel panel = new JPanel() {
            private Point origin; // To keep track of the panning origin
            private Point zoomPoint; // To track the point for zooming
            private BufferedImage compositeImage; // Composite image with red rectangle

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if (compositeImage == null) {
                    createCompositeImage(); // Create the composite image only once
                }

                int zoomedWidth = (int) (image.getWidth() * zoomLevel);
                int zoomedHeight = (int) (image.getHeight() * zoomLevel);

                // Draw the composite image onto the panel
                g.drawImage(compositeImage, 0, 0, zoomedWidth, zoomedHeight, this);

                // ... (rest of your code remains the same)
            }

            private void createCompositeImage() {
                // Create a new BufferedImage to draw the modified image (original image + red rectangle)
                compositeImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = compositeImage.createGraphics();

                // Draw the original image onto the composite image
                g2d.drawImage(image, 0, 0, null);

                // Calculate coordinates for the rectangle without any scaling
                int x1 = coordinates[0];
                int y1 = coordinates[1];
                int x2 = coordinates[2];
                int y2 = coordinates[3];

                int rectX = Math.min(x1, x2);
                int rectY = Math.min(y1, y2);
                int rectWidth = Math.abs(x2 - x1);
                int rectHeight = Math.abs(y2 - y1);

                // Draw a red rectangle onto the composite image (without scaling)
                g2d.setColor(Color.RED);
                g2d.drawRect(rectX, rectY, rectWidth, rectHeight);

                // Dispose the Graphics2D object
                g2d.dispose();
            }

            // ... (rest of the code for mouse listeners remains the same)
        };


        // Add the mouse wheel listener for zooming
        panel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int notches = e.getWheelRotation();
                if (notches < 0) {
                    zoomLevel *= ZOOM_FACTOR; // Zoom in
                } else {
                    zoomLevel /= ZOOM_FACTOR; // Zoom out
                }
                panel.repaint();
            }
        });

        // Add the panel with the image and rectangle to the frame
        frame.getContentPane().add(panel);

        // Pack the frame to fit the size of the image
        frame.pack();

        Insets insets = frame.getInsets();
        int frameWidth = zoomedWidth + insets.left + insets.right;
        int frameHeight = zoomedHeight + insets.top + insets.bottom;

        // Set the frame size and position
        frame.setSize(frameWidth, frameHeight);
        frame.setLocation(selectedScreen.getDefaultConfiguration().getBounds().getLocation());

        // Make the frame visible
        frame.setVisible(true);
        frame.setTitle(name);

    }


    public Screen() {
        frame = new JFrame("Captured Image");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setResizable(true);
    }



    public JFrame frame;
    private static Point origin; // To keep track of the panning origin
}


