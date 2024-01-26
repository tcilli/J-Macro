package com.phukka.macro.imaging;

import com.phukka.macro.devices.Keyboard;
import com.phukka.macro.devices.screen.Screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test implements Keyboard.Listener {

    private static ExecutorService executor;

    @Override
    public void onKeyPressed(int vkCode) {
        if(vkCode == -92) {

            long start = System.currentTimeMillis();
            //testing 1k
            for (int i = 0; i < 100; i++) {
                captureWitJRobot();
            }
            System.out.println("Time taken: " + (System.currentTimeMillis() - start) / 100 + "ms");
        }
        System.out.println(vkCode);
    }

    @Override
    public void onKeyReleased(int vkCode) {

    }



    public static void main(String[] args) {

        Keyboard keyboard = new Keyboard();
        Test test = new Test();
        keyboard.addListener("", test);

        executor = Executors.newCachedThreadPool();
        executor.submit(keyboard);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            keyboard.unhook();
        }));
    }



    public void captureWithBitmap() { //22ms average
        ScreenCapture sc = new ScreenCapture("RuneScape");
        BufferedImage image = sc.current_image();
    }


    public void captureWitJRobot() { //15 - 20ms
        try {
            ScreenCapture sc = new ScreenCapture("RuneScape");
            Robot r = new Robot();
            BufferedImage image = r.createScreenCapture(new Rectangle(0, 0, 1920, 1080));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

