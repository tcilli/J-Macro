package macro.win32;

import com.sun.jna.platform.win32.User32;
import macro.win32.inferfaces.MouseInterface;

import java.awt.*;

public class MouseEvent {

    public static void move_mouse(long mouseData) {
        short x = (short) ((mouseData >> 48) & 0xFFFF);
        short y = (short) ((mouseData >> 32) & 0xFFFF);
        int delay = (int) mouseData >> 1;
        boolean abs = (mouseData & 1) == 1;

        if (delay > SLEEP_TIME && delay < 10000) {
            mouse_move_over_time(x, y, delay, abs);
        } else {
            if (abs) {
                mouse_move_abs(x, y);
            } else {
                mouse_move_rel(x, y);
            }
        }
    }

    public static void click_mouse(short mouseData) {
        byte mouseButton = (byte) (mouseData >> 8);
        byte flags = (byte) (mouseData & 0xFF);

        if ((flags & 0x03) != 0) {
            if (mouseButton == 1) mouse_click_left();
            else if (mouseButton == 2) mouse_click_right();
            else if (mouseButton == 3) mouse_click_middle();
        } else {
            if ((flags & 0x01) != 0) {
                if (mouseButton == 1) mouse_click_leftdown();
                else if (mouseButton == 2) mouse_click_rightdown();
                else if (mouseButton == 3) mouse_click_middledown();
            }
            if ((flags & 0x02) != 0) {
                if (mouseButton == 1) mouse_click_leftup();
                else if (mouseButton == 2) mouse_click_rightup();
                else if (mouseButton == 3) mouse_click_middleup();
            }
        }
    }

    public static void mouse_move_over_time(int x, int y, int delay, boolean abs) {

        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point currentPosition = pointerInfo.getLocation();

        delay = SLEEP_TIME * (Math.round((float) delay / SLEEP_TIME));

        if (Math.abs(currentPosition.getX() - x) <= 10 && Math.abs(currentPosition.getY() - y) <= 10  || delay < 10 || !abs) {
            try {
                Thread.sleep(delay);
                if (abs) {
                    mouse_move_abs(x, y);
                } else {
                    mouse_move_rel(x, y);
                }
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
            double nextY = (currentPosition.getY() + (progress * deltaY));;
            mouse_move_abs((int) nextX, (int) nextY);

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }
        //make sure the mouse ends up at the correct position
        mouse_move_abs(x, y);
    }

    public static void mouse_move_abs(int x, int y) {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_ABS | MOUSEEVENTF_MOVE, (x * SCREEN_SCALE_FACTOR_X), (y * SCREEN_SCALE_FACTOR_Y), 0, 0);
    }
    public static void mouse_move_rel(int x, int y) {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_MOVE, x, y, 0, 0);
    }
    public static void mouse_click_left() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
    }
    public static void mouse_click_leftdown() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
    }
    public static void mouse_click_leftup() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
    }
    public static void mouse_click_right() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
    }
    public static void mouse_click_rightdown() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
    }
    public static void mouse_click_rightup() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
    }
    public static void mouse_click_middle() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_MIDDLEDOWN, 0, 0, 0, 0);
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_MIDDLEUP, 0, 0, 0, 0);
    }
    public static void mouse_click_middledown() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_MIDDLEDOWN, 0, 0, 0, 0);
    }
    public static void mouse_click_middleup() {
        MouseInterface.INSTANCE.mouse_event(MOUSEEVENTF_MIDDLEUP, 0, 0, 0, 0);
    }

    private static final int SLEEP_TIME = 10;
    private static final int MOUSEEVENTF_MOVE = 1;
    private static final int MOUSEEVENTF_LEFTDOWN = 2;
    private static final int MOUSEEVENTF_LEFTUP = 4;
    private static final int MOUSEEVENTF_RIGHTDOWN = 8;
    private static final int MOUSEEVENTF_RIGHTUP = 16;
    private static final int MOUSEEVENTF_MIDDLEDOWN = 32;
    private static final int MOUSEEVENTF_MIDDLEUP = 64;
    private static final int MOUSEEVENTF_ABS = 0x8000;

    private static final int SCREEN_SCALE_FACTOR_X = (65535 / User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN));
    private static final int SCREEN_SCALE_FACTOR_Y = (65535 / User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN));
}
