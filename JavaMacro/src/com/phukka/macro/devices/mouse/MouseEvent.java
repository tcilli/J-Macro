package com.phukka.macro.devices.mouse;

import com.phukka.macro.Main;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;

import java.awt.PointerInfo;
import java.awt.MouseInfo;
import java.awt.Point;

public class MouseEvent {

    public static long mouseMovementPacker(short x, short y, int delay, boolean abs) {
        long mouseData = 0;
        mouseData |= ((long) x & 0xFFFFL) << 48;
        mouseData |= ((long) y & 0xFFFFL) << 32;
        mouseData |= ((long) delay & 0x7FFFFFFFL) << 1;
        mouseData |= abs ? 1 : 0;
        return mouseData;
    }

    public static byte mouseClickPacker(int mouseButton, int mouseButtonDown, int mouseButtonUp) {
        byte mouseData = 0;
        mouseData |= (byte) (mouseButton & 0xF) << 4; // mouseButton stored in upper 4 bits
        mouseData |= (byte) (mouseButtonDown | (mouseButtonUp << 1)) & 0xF; //click flags stored in lower 4 bits
        return mouseData;
    }

    public static void mouseMove(long mouseMoveData) {
        short x = (short) ((mouseMoveData >> 48) & 0xFFFF);
        short y = (short) ((mouseMoveData >> 32) & 0xFFFF);
        int delay = (int) ((mouseMoveData >> 1) & 0x7FFFFFFF);
        boolean abs = (mouseMoveData & 1) == 1;
        System.out.println("x: " + x + " y: " + y + " delay: " + delay + " abs: " + abs);
        if (delay > SLEEP_TIME && delay < 10000) {
            mouseMoveOverTimeEvent(x, y, delay, abs);
        } else {
            mouseMoveEvent(x, y, abs);
        }
    }

    public static void mouseClick(byte mouseClickData) {
        byte mouseButton = (byte) (mouseClickData >> 4);
        byte flags = (byte) (mouseClickData & 0xF);
        int downEvent = getDownEvent(mouseButton);
        int upEvent = getUpEvent(mouseButton);

        switch (flags) {
            case 1 -> mouseClickEvent(downEvent);
            case 2 -> mouseClickEvent(upEvent);
            case 3 -> mouseClickEvent(downEvent | upEvent);
            default -> throw new IllegalArgumentException("Invalid flags value: "+ flags);
        }
    }

    private static int getDownEvent(byte mouseButton) {
        return switch (mouseButton) {
            case 1 -> MOUSEEVENTF_LEFTDOWN;
            case 2 -> MOUSEEVENTF_RIGHTDOWN;
            case 3 -> MOUSEEVENTF_MIDDLEDOWN;
            default -> throw new IllegalArgumentException("Invalid mouseButton value: "+ mouseButton);
        };
    }

    private static int getUpEvent(byte mouseButton) {
        return switch (mouseButton) {
            case 1 -> MOUSEEVENTF_LEFTUP;
            case 2 -> MOUSEEVENTF_RIGHTUP;
            case 3 -> MOUSEEVENTF_MIDDLEUP;
            default -> throw new IllegalArgumentException("Invalid mouseButton value: "+ mouseButton);
        };
    }



    private static void mouseMoveOverTimeEvent(int x, int y, int delay, boolean abs) {

        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point currentPosition = pointerInfo.getLocation();

        int currentX = (int) currentPosition.getX();
        int currentY = (int) currentPosition.getY();

        if (Math.abs(currentX - x) <= 10 && Math.abs(currentY - y) <= 10) {
            try {
                Thread.sleep(delay);
                mouseMoveEvent(x, y, abs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        int targetX = abs ? x : currentX + x;
        int targetY = abs ? y : currentY + y;

        try {
            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;

            while (elapsedTime < delay) {
                double progress = (double) elapsedTime / delay;
                int nextX = currentX + (int) (progress * (targetX - currentX));
                int nextY = currentY + (int) (progress * (targetY - currentY));
                mouseMoveEvent(nextX, nextY, true);
                Thread.sleep(SLEEP_TIME);
                elapsedTime = System.currentTimeMillis() - startTime;
            }
            Main.getRobot().mouseMove(targetX, targetY);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void mouseMoveEvent2(int x, int y, boolean abs) {
        if (abs) {
            Main.getRobot().mouseMove(x, y);
        } else {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            Point currentPosition = pointerInfo.getLocation();
            int currentX = (int) currentPosition.getX();
            int currentY = (int) currentPosition.getY();
            Main.getRobot().mouseMove(currentX + x, currentY + y);
        }
    }

    private static void mouseClickEvent(int flags) {
        win_input_event.input.setType("mi");
        win_input_event.type.setValue(WinUser.INPUT.INPUT_MOUSE);
        win_input_event.input.mi.dwFlags.setValue(flags);
        WinUser.INPUT[] inputs = {win_input_event};
        User32.INSTANCE.SendInput(nInput, inputs, win_input_event.size());
    }

    private static final WinUser.INPUT win_input_event = new WinUser.INPUT();
    private static final WinDef.DWORD nInput = new WinDef.DWORD(1);
    private static final double SCREEN_SCALE_FACTOR_X = (65535.0 / User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN));
    private static final double SCREEN_SCALE_FACTOR_Y = (65535.0 / User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN));
    private static final int SLEEP_TIME = 5;
    private static final int MOUSEEVENTF_MOVE = 0x0001;
    private static final int MOUSEEVENTF_LEFTDOWN = 0x0002;
    private static final int MOUSEEVENTF_LEFTUP = 0x0004;
    private static final int MOUSEEVENTF_RIGHTDOWN = 0x0008;
    private static final int MOUSEEVENTF_RIGHTUP = 0x0010;
    private static final int MOUSEEVENTF_MIDDLEDOWN = 0x0020;
    private static final int MOUSEEVENTF_MIDDLEUP = 0x0040;
    private static final int MOUSEEVENTF_ABS = 0x8000;

    public static void getMousePosition() {
        PointerInfo info = MouseInfo.getPointerInfo();
        Main.getConsoleBuffer().append("mousePosX: ").append(info.getLocation().getX()).append(" mousePosY: ").append(info.getLocation().getY());
        Main.pushConsoleMessage();
    }


    /**
     * methods for easy use
     */
    public static void click() {
        mouseClickEvent(MOUSEEVENTF_LEFTDOWN);
        mouseClickEvent(MOUSEEVENTF_LEFTUP);
    }
    public static void leftClick() {
        mouseClickEvent(MOUSEEVENTF_LEFTDOWN);
        mouseClickEvent(MOUSEEVENTF_LEFTUP);
    }
    public static void leftClickDown() {
        mouseClickEvent(MOUSEEVENTF_LEFTDOWN);
    }
    public static void leftClickUp() {
        mouseClickEvent(MOUSEEVENTF_LEFTUP);
    }
    public static void rightClick() {
        mouseClickEvent(MOUSEEVENTF_RIGHTDOWN);
        mouseClickEvent(MOUSEEVENTF_RIGHTUP);
    }
    public static void rightClickDown() {
        mouseClickEvent(MOUSEEVENTF_RIGHTDOWN);
    }
    public static void rightClickUp() {
        mouseClickEvent(MOUSEEVENTF_RIGHTUP);
    }
    public static void middleClick() {
        mouseClickEvent(MOUSEEVENTF_MIDDLEDOWN);
        mouseClickEvent(MOUSEEVENTF_MIDDLEUP);
    }
    public static void middleClickDown() {
        mouseClickEvent(MOUSEEVENTF_MIDDLEDOWN);
    }
    public static void middleClickUp() {
        mouseClickEvent(MOUSEEVENTF_MIDDLEUP);
    }
    public static void doubleClick() {
        mouseClickEvent(MOUSEEVENTF_LEFTDOWN);
        mouseClickEvent(MOUSEEVENTF_LEFTUP);
        mouseClickEvent(MOUSEEVENTF_LEFTDOWN);
        mouseClickEvent(MOUSEEVENTF_LEFTUP);
    }

    public static int[] position() {
        PointerInfo info = MouseInfo.getPointerInfo();
        return new int[]{(int) info.getLocation().getX(), (int) info.getLocation().getY()};
    }

    public static void move(int x, int y, boolean abs) {
        mouseMoveEvent(x, y, abs);
    }
    public static void move(int x, int y) {
        mouseMoveEvent(x, y, true);
    }
    public static void move(int x, int y, int delay, boolean abs) {
        mouseMoveOverTimeEvent(x, y, delay, abs);
    }
    public static void move(int x, int y, int delay) {
        mouseMoveOverTimeEvent(x, y, delay, true);
    }

    public static void ghostClick(int x, int y, int button) {
        int startX = MouseCallback.getX();
        int startY = MouseCallback.getY();
        mouseMoveEvent(x, y, true);
        if (button == 1) {
            leftClick();
        }
        if (button == 2) {
            rightClick();
        }
        if (button == 3) {
            middleClick();
        }
        mouseMoveEvent(startX, startY, true);
    }

    public static void mouseMoveEvent(int x, int y, boolean abs) {
        try {
            MouseCallback.disableUserMovement();

            if (!abs) {
                x += MouseCallback.getX();
                y += MouseCallback.getY();
            }

            x = (int) Math.ceil(x * SCREEN_SCALE_FACTOR_X) + 1;
            y = (int) Math.ceil(y * SCREEN_SCALE_FACTOR_Y) + 1;

            win_input_event.input.setType("mi");
            win_input_event.type.setValue(WinUser.INPUT.INPUT_MOUSE);
            win_input_event.input.mi.dx.setValue(x);
            win_input_event.input.mi.dy.setValue(y);
            win_input_event.input.mi.dwFlags.setValue(MOUSEEVENTF_MOVE | MOUSEEVENTF_ABS); //signal: move to absolute position
            win_input_event.input.mi.dwExtraInfo.setValue(1);
            WinUser.INPUT[] inputs = {win_input_event};
            User32.INSTANCE.SendInput(new WinDef.DWORD(1), inputs, win_input_event.size());

        } finally {

            MouseCallback.enableUserMovement();
        }
    }
}
