package com.phukka.macro.devices.screen;

import com.phukka.macro.Main;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ImageSearch {

    public static class RGB {
        public byte red, green, blue;

        public RGB(int color) {
            this.red = (byte) ((color >> 16) & 0xFF);
            this.green = (byte) ((color >> 8) & 0xFF);
            this.blue = (byte) (color & 0xFF);
        }
    }

    /**
     * colors should be nearly identical
     * however this is overridable by passing in a defined colour variance
     */
    private static final int COLOR_VARIANCE = 100;


    private static int[] searchImage(BufferedImage baseImage, BufferedImage imageToFind, int startX, int endX, int endY) {
        return searchImage(baseImage, imageToFind, startX, endX, endY, COLOR_VARIANCE);
    }

    private static int[] searchImage(BufferedImage baseImage, BufferedImage imageToFind, int startX, int endX, int endY, int definedColourVariance) {
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
                if (matchMask(x, y, basePixels, baseImage.getWidth(), maskRGB, definedColourVariance)) {
                    return new int[]{x, y, x + widthToFind, y + heightToFind};
                }
            }
        }
        return null;
    }


    // modify matchMask method to accept basePixels and baseImageWidth instead of baseImage
    private static boolean matchMask(int startX, int startY, int[] basePixels, int baseImageWidth, RGB[][] maskRGB, int definedColourVariance) {
        int heightToFind = maskRGB.length;
        int widthToFind = maskRGB[0].length;


        byte redDiff = 0;
        byte greenDiff = 0;
        byte blueDiff = 0;

        double difference =  0;

        // Iterate over the pixels in the image to search in
        // using the step size, to speed up the search
        for (int y = 0; y < heightToFind; y+=2) {
            for (int x = 0; x < widthToFind; x+=2) {

                // Check if the current pixel is within the image boundaries
                if (startX + x >= baseImageWidth || startY + y >= basePixels.length / baseImageWidth) {
                    return false;
                }
                if (maskRGB[y][x].red == 0 && maskRGB[y][x].green == 0 && maskRGB[y][x].blue == 0) {
                    continue;
                }

                RGB pixel = new RGB(basePixels[(startY + y) * baseImageWidth + (startX + x)]);

                // Calculate color differences for each channel
                redDiff = (byte) Math.abs(pixel.red - maskRGB[y][x].red);
                greenDiff = (byte) Math.abs(pixel.green - maskRGB[y][x].green);
                blueDiff = (byte) Math.abs(pixel.blue - maskRGB[y][x].blue);

                difference =  Math.sqrt((redDiff * redDiff) + (greenDiff * greenDiff) + (blueDiff * blueDiff));
                if (difference > definedColourVariance) {
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

    /**
     * Searches for the imageToFind in the imageToSearchIn
     * @param imageToSearchIn the image to search in
     * @param imageToFind the image to find
     * @return the bounds of the imageToFind in the imageToSearchIn
     */


    public static int[] find(ImagePosition imageToSearchIn, BufferedImage imageToFind) {
        return find(imageToSearchIn, imageToFind, COLOR_VARIANCE);
    }


    public static boolean foundExact(ImagePosition imageToSearchIn, BufferedImage imageToFind){
        return find(imageToSearchIn, imageToFind, 50) != null;
    }


    public static int[] find(ImagePosition imageToSearchIn, BufferedImage imageToFind, int definedColourVariance) {

        int baseImageWidth = imageToSearchIn.image().getWidth();
        int baseImageHeight = imageToSearchIn.image().getHeight();


        int[] result = searchImage(imageToSearchIn.image(), imageToFind, 0, baseImageWidth, baseImageHeight, definedColourVariance);

        return result;
    }


    public static int[] find2(ImagePosition imageToSearchIn, BufferedImage imageToFind, int definedColourVariance) {

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
                Future<int[]> future = Main.getExecutor().submit(() -> searchImage(imageToSearchIn.image(), imageToFind, finalStartX, finalEndX, baseImageHeight, definedColourVariance));
                futures.add(future);
            } catch (RejectedExecutionException ignored) {
                //if we close the program before these finish, we get this exception
                //So we just ignore it
            }
        }

        for (Future<int[]> future : futures) {
            try {
                int[] result = future.get();
                if (result != null) {

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
        return null;//new int[]{-1, -1, -1, -1};
    }


    public static int[] findImage(ImagePosition imageToSearchIn, BufferedImage imageToFind) {

        int baseImageWidth = imageToSearchIn.image().getWidth();
        int baseImageHeight = imageToSearchIn.image().getHeight();

        int[] res = searchImage(imageToSearchIn.image(), imageToFind, 0, baseImageWidth, baseImageHeight, COLOR_VARIANCE);
        return res;
    }
}