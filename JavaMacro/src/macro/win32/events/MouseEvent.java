package macro.win32.events;

import macro.Main;

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
                Main.getRobot().mouseMove(nextX, nextY);
                Thread.sleep(SLEEP_TIME);
                elapsedTime = System.currentTimeMillis() - startTime;
            }
            Main.getRobot().mouseMove(targetX, targetY);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void mouseMoveEvent(int x, int y, boolean abs) {
        win_input_event.input.setType("mi");
        win_input_event.type.setValue(WinUser.INPUT.INPUT_MOUSE);
        win_input_event.input.mi.dx.setValue(abs ? (long) x * SCREEN_SCALE_FACTOR_X : x);
        win_input_event.input.mi.dy.setValue(abs ? (long) y * SCREEN_SCALE_FACTOR_Y : y);
        win_input_event.input.mi.dwFlags.setValue(abs ? MOUSEEVENTF_ABS | MOUSEEVENTF_MOVE : MOUSEEVENTF_MOVE);
        WinUser.INPUT[] inputs = {win_input_event};
        User32.INSTANCE.SendInput(nInput, inputs, win_input_event.size());
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
    private static final int SCREEN_SCALE_FACTOR_X = (65535 / User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN));
    private static final int SCREEN_SCALE_FACTOR_Y = (65535 / User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN));
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
}