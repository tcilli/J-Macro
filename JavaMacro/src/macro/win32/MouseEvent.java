package macro.win32;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import macro.Main;
import com.sun.jna.platform.win32.WinUser;

import java.awt.*;

public class MouseEvent {

    public static void mouseMove(long mouseData) {
        short x = (short) ((mouseData >> 48) & 0xFFFF);
        short y = (short) ((mouseData >> 32) & 0xFFFF);
        int delay = (int) mouseData >> 1;
        boolean abs = (mouseData & 1) == 1;

        if (delay > SLEEP_TIME && delay < 10000) {
            mouseMoveOverTimeEvent(x, y, delay, abs);
        } else {
            mouseMoveEvent(x, y, abs);
        }
    }

    public static void mouseClick(short mouseData) {
        byte mouseButton = (byte) (mouseData >> 8);
        byte flags = (byte) (mouseData & 0xFF);

        int downEvent = getDownEvent(mouseButton);
        int upEvent = getUpEvent(mouseButton);

        switch (flags) {
            case 1 -> mouseClickEvent(downEvent);
            case 2 -> mouseClickEvent(upEvent);
            case 3 -> mouseClickEvent(downEvent | upEvent);
            default -> throw new IllegalArgumentException("Invalid flags value");
        }
    }

    private static int getDownEvent(byte mouseButton) {
        return switch (mouseButton) {
            case 1 -> MOUSEEVENTF_LEFTDOWN;
            case 2 -> MOUSEEVENTF_RIGHTDOWN;
            case 3 -> MOUSEEVENTF_MIDDLEDOWN;
            default -> throw new IllegalArgumentException("Invalid mouseButton value");
        };
    }

    private static int getUpEvent(byte mouseButton) {
        return switch (mouseButton) {
            case 1 -> MOUSEEVENTF_LEFTUP;
            case 2 -> MOUSEEVENTF_RIGHTUP;
            case 3 -> MOUSEEVENTF_MIDDLEUP;
            default -> throw new IllegalArgumentException("Invalid mouseButton value");
        };
    }

    private static void mouseMoveOverTimeEvent(int x, int y, int delay, boolean abs) {

        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point currentPosition = pointerInfo.getLocation();

        delay = SLEEP_TIME * (Math.round((float) delay / SLEEP_TIME));

        if (Math.abs(currentPosition.getX() - x) <= 10 && Math.abs(currentPosition.getY() - y) <= 10  || delay < 10 || !abs) {
            try {
                Thread.sleep(delay);
                mouseMoveEvent(x, y, abs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        int deltaX = (int) (x - currentPosition.getX());
        int deltaY = (int) (y - currentPosition.getY());
        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        double progress;
        double iterations = (double) delay / SLEEP_TIME;

        while (iterations > 0) {
            iterations--;

            progress = Math.min(1.0, (float) elapsedTime / delay);
            double nextX = (currentPosition.getX() + (progress * deltaX));
            double nextY = (currentPosition.getY() + (progress * deltaY));
            mouseMoveEvent((int) nextX, (int) nextY, true);
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        //make sure the mouse ends up at the correct position
        mouseMoveEvent(x, y, true);
    }


    private static void mouseMoveEvent(int x, int y, boolean abs) {
        win_input_event.input.setType("mi");
        win_input_event.type.setValue(WinUser.INPUT.INPUT_MOUSE);
        win_input_event.input.mi.dx.setValue((long) x * SCREEN_SCALE_FACTOR_X);
        win_input_event.input.mi.dy.setValue((long) y * SCREEN_SCALE_FACTOR_Y);
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
    private static final int SLEEP_TIME = 10;
    private static final int MOUSEEVENTF_MOVE = 1;
    private static final int MOUSEEVENTF_LEFTDOWN = 2;
    private static final int MOUSEEVENTF_LEFTUP = 4;
    private static final int MOUSEEVENTF_RIGHTDOWN = 8;
    private static final int MOUSEEVENTF_RIGHTUP = 16;
    private static final int MOUSEEVENTF_MIDDLEDOWN = 32;
    private static final int MOUSEEVENTF_MIDDLEUP = 64;
    private static final int MOUSEEVENTF_ABS = 0x8000;

    public static void getMousePosition() {
        PointerInfo info = MouseInfo.getPointerInfo();
        Main.getConsoleBuffer().append("mousePosX: ").append(info.getLocation().getX()).append(" mousePosY: ").append(info.getLocation().getY());
        Main.pushConsoleMessage();
    }
}
