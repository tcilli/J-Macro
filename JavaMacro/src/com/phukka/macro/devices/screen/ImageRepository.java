package com.phukka.macro.devices.screen;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ImageRepository {

    public final Map<String, BufferedImage> imageMap = new HashMap<>();

    public ImageRepository() {
    }

    public BufferedImage get(String imageName) {
        try {
            if (imageMap.containsKey(imageName)) {
                return imageMap.get(imageName);
            }

            String path = "./data/images/";
            String imagePath = path + imageName + ".png";
            File file = new File(imagePath);
            if (!file.exists()) {
                System.out.println("Image not found: " + imagePath);
                return null;
            }
            BufferedImage image = ImageIO.read(new File(imagePath));
            imageMap.put(imageName, image);
            return image;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
