package com.phukka.macro.devices.screen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Loads images from the file system and caches them in a map
 */
public class ImageRepository {

    public final Map<String, BufferedImage> imageCache = new HashMap<>();

    public ImageRepository() {
    }

    /**
     * Gets the image from the repository, if it doesn't exist it will load it from the file system
     * @param imageName The name of the image to load
     * @return {@link BufferedImage} "If exists"
     */
    public BufferedImage get(String imageName) {
        try {
            if (imageCache.containsKey(imageName)) {
                return imageCache.get(imageName);
            }

            String path = "./data/images/";
            String imagePath = path + imageName + ".png";
            File file = new File(imagePath);
            if (!file.exists()) {
                System.out.println("Image not found: " + imagePath);
                return null;
            }
            BufferedImage image = ImageIO.read(new File(imagePath));
            imageCache.put(imageName, image);
            return image;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
