package com.phukka.macro.devices.screen;

import com.phukka.macro.Main;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ImageSearch {

    public static class RGB {
        public int red, green, blue;

        public RGB(int color) {
            this.red = (color >> 16) & 0xFF;
            this.green = (color >> 8) & 0xFF;
            this.blue = color & 0xFF;
        }
    }

    private static int[] searchImage(BufferedImage baseImage, BufferedImage imageToFind, int startX, int endX, int endY) {
        int widthToFind = imageToFind.getWidth();
        int heightToFind = imageToFind.getHeight();

        // Create the mask matrix using pixel values of the imageToFind
        RGB[][] maskRGB = new RGB[heightToFind][widthToFind];

        for (int y = 0; y < heightToFind; y++) {
            for (int x = 0; x < widthToFind; x++) {
                maskRGB[y][x] = new RGB(imageToFind.getRGB(x, y));
            }
        }

        // get the pixel data for baseImage
        int[] basePixels = getPixelData(baseImage);

        for (int y = 0; y <= endY - heightToFind + 1; y++) {
            for (int x = startX; x <= endX - widthToFind + 1; x++) {
                if (matchMask(x, y, basePixels, baseImage.getWidth(), maskRGB)) {
                    return new int[]{x, y, x + widthToFind, y + heightToFind};
                }
            }
        }
        return new int[]{-1, -1, -1, -1};
    }

    // modify matchMask method to accept basePixels and baseImageWidth instead of baseImage
    private static boolean matchMask(int startX, int startY, int[] basePixels, int baseImageWidth, RGB[][] maskRGB) {
        int heightToFind = maskRGB.length;
        int widthToFind = maskRGB[0].length;

        // Calculate the step size for x and y
        int stepY = (int) Math.sqrt(heightToFind);
        int stepX = (int) Math.sqrt(widthToFind);

        // Iterate over the pixels in the image to search in
        // using the step size, to speed up the search
        for (int y = 0; y < heightToFind; y += stepY) {
            for (int x = 0; x < widthToFind; x += stepX) {

                // Check if the current pixel is within the image boundaries
                if (startX + x >= baseImageWidth || startY + y >= basePixels.length / baseImageWidth) {
                    return false;
                }

                RGB pixel = new RGB(basePixels[(startY + y) * baseImageWidth + (startX + x)]);

                // Calculate color differences for each channel
                int redDiff = Math.abs(pixel.red - maskRGB[y][x].red);
                int greenDiff = Math.abs(pixel.green - maskRGB[y][x].green);
                int blueDiff = Math.abs(pixel.blue - maskRGB[y][x].blue);

                int colorDifferenceSquared = redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
                if (colorDifferenceSquared > COLOR_VARIANCE * COLOR_VARIANCE) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] getPixelData(BufferedImage image) {
        return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public static int[] getCenter(int[] bounds) {
        int[] center = new int[2];
        center[0] = (bounds[0] + bounds[2]) / 2;
        center[1] = (bounds[1] + bounds[3]) / 2;
        return center;
    }

    private static final int COLOR_VARIANCE = 60;

    public static int[] findCenter(ImagePosition imageToSearchIn, BufferedImage imageToFind) {
        int[] bounds = find(imageToSearchIn, imageToFind);
        if (bounds[0] == -1) {
            return bounds;
        }
        bounds[0] += imageToSearchIn.x();
        bounds[1] += imageToSearchIn.y();
        bounds[2] += imageToSearchIn.x();
        bounds[3] += imageToSearchIn.y();
        return getCenter(bounds);
    }

    public static int[] find(ImagePosition imageToSearchIn, BufferedImage imageToFind) {

        int baseImageWidth = imageToSearchIn.image().getWidth();
        int baseImageHeight = imageToSearchIn.image().getHeight();
        int imageToFindWidth = imageToFind.getWidth();

        int numThreads = Runtime.getRuntime().availableProcessors();
        int minSliceWidth = (int) Math.ceil(imageToFindWidth * 2.0);

        // Calculate the number of slices based on the number of threads and the minimum slice width
        int numSlices = Math.max(1, baseImageWidth / minSliceWidth);
        // Adjust the number of slices if it's greater than the number of threads
        numSlices = Math.min(numSlices, numThreads);
        // Calculate the actual slice width
        int sliceWidth = baseImageWidth / numSlices;

        // Calculate the overlap between slices
        int overlap = (int)Math.ceil(imageToFindWidth / 2.0);

        if (baseImageWidth < 2 * imageToFindWidth) {
            numSlices = 1;
            sliceWidth = baseImageWidth;

        }
        if (numThreads > numSlices) {
            numThreads = numSlices;
        }
        //System.out.println("slices: " + numSlices + " slice width: " + sliceWidth + " imageToFindWidth: " + imageToFindWidth + " baseImageWidth: " + baseImageWidth + " baseImageHeight: " + baseImageHeight + " numThreads: " + numThreads + " minSliceWidth: " + minSliceWidth);
        List<Future<int[]>> futures = new ArrayList<>();

        for (int i = 0; i < numSlices; i++) {
            int startX = i * sliceWidth - overlap;
            int endX = (i + 1) * sliceWidth + overlap;

            // Ensure the start and end values are within the image boundaries
            startX = Math.max(0, startX);
            endX = Math.min(baseImageWidth, endX);

            int finalStartX = startX;
            int finalEndX = endX;

            try {
                Future<int[]> future = Main.getExecutor().submit(() -> searchImage(imageToSearchIn.image(), imageToFind, finalStartX, finalEndX, baseImageHeight));
                futures.add(future);
            } catch (RejectedExecutionException ignored) {
                //if we close the program before these finish, we get this exception
                //So we just ignore it
            }
        }

        for (Future<int[]> future : futures) {
            try {
                int[] result = future.get();
                if (result[0] != -1) {

                    futures.forEach(f ->
                        f.cancel(true)
                    );

                    return result;
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException ignored) {

            }
        }
        return new int[]{-1, -1, -1, -1};
    }

    public static int getPixel(int x, int y) {
        // Capture the screen
        ImagePosition area = Main.getScreen().captureArea(x, y, x+1, y+1);
        return getPixelData(area.image())[0];
    }

    public static int[] getPixelRGB(int x, int y) {
        // Capture the screen
        ImagePosition area = Main.getScreen().captureArea(x, y, x+1, y+1);
        RGB rgb = new RGB(getPixelData(area.image())[0]);
        return new int[] {rgb.red, rgb.green, rgb.blue};
    }
}