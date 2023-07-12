package macro.jnative;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import macro.Main;

import java.awt.*;

public class NativeInput {

    private static final int MOUSEEVENTF_MOVE = 1;
    private static final int MOUSEEVENTF_LEFTDOWN = 2;
    private static final int MOUSEEVENTF_LEFTUP = 4;
    private static final int MOUSEEVENTF_RIGHTDOWN = 8;
    private static final int MOUSEEVENTF_RIGHTUP = 16;
    private static final int MOUSEEVENTF_MIDDLEDOWN = 32;
    private static final int MOUSEEVENTF_MIDDLEUP = 64;
    //private static final int MOUSEEVENTF_WHEEL = 2048;
    private static final int MOUSEEVENTF_ABS = 0x8000;

    public static final int KEYEVENTF_KEYDOWN = 0;
    public static final int KEYEVENTF_KEYUP = 2;

    public static final int MOUSE_BUTTON_LEFT = 1;
    public static final int MOUSE_BUTTON_RIGHT = 2;
    public static final int MOUSE_BUTTON_MIDDLE = 3;
    
    private static final WinUser.INPUT input = new WinUser.INPUT();
    private static final WinDef.DWORD nInput = new WinDef.DWORD(1);

    private static final int SCREEN_SCALE_FACTOR_X = (65535 / User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN));
    private static final int SCREEN_SCALE_FACTOR_Y = (65535 / User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN));


    public static void pressKeyDown(int c) {
        sendKey(c, KEYEVENTF_KEYDOWN);
    }
    public static void pressKeyUp(int c) {
        sendKey(c, KEYEVENTF_KEYUP);
    }
    public static void pressKey(int c) {
        sendKey(c, KEYEVENTF_KEYDOWN);
        sendKey(c, KEYEVENTF_KEYUP);

    }
    public static void click(int mouseButton)
    {
        mouseClick(
                mouseButton == 1 ? MOUSEEVENTF_LEFTDOWN   | MOUSEEVENTF_LEFTUP :
                mouseButton == 2 ? MOUSEEVENTF_RIGHTDOWN  | MOUSEEVENTF_RIGHTUP :
                                   MOUSEEVENTF_MIDDLEDOWN | MOUSEEVENTF_MIDDLEUP);
    }
    public static void clickDown(int mouseButton)
    {
        mouseClick(
                mouseButton == 1 ? MOUSEEVENTF_LEFTDOWN :
                mouseButton == 2 ? MOUSEEVENTF_RIGHTDOWN :
                                   MOUSEEVENTF_MIDDLEDOWN);
    }
    public static void clickUp(int mouseButton)
    {
        mouseClick(
                mouseButton == 1 ? MOUSEEVENTF_LEFTUP :
                mouseButton == 2 ? MOUSEEVENTF_RIGHTUP :
                                   MOUSEEVENTF_MIDDLEUP);
    }

    private static void mouseClick(int flags)
    {
        input.input.setType("mi");
        input.type.setValue(WinUser.INPUT.INPUT_MOUSE);
        input.input.mi.dwFlags.setValue(flags);
        WinUser.INPUT[] inputs = { input };
        User32.INSTANCE.SendInput(nInput, inputs, input.size());
    }
    public static void mouseMove(int x, int y, int delay, boolean absolute)
    {
        if (delay > 0) {
            mouseMoveStraight(x, y, true, delay);
            return;
        }
        if (x >= 1 && x <= 65535 && y >= 1 && y <= 65535)
        {
            input.input.setType("mi");
            input.type.setValue(WinUser.INPUT.INPUT_MOUSE);
            input.input.mi.dx.setValue((long) x * SCREEN_SCALE_FACTOR_X);
            input.input.mi.dy.setValue((long) y * SCREEN_SCALE_FACTOR_Y);
            input.input.mi.dwFlags.setValue(absolute ? MOUSEEVENTF_ABS | MOUSEEVENTF_MOVE : MOUSEEVENTF_MOVE);
            WinUser.INPUT[] inputs = { input };
            User32.INSTANCE.SendInput(nInput, inputs, input.size());
        } else {
            Main.getConsoleBuffer().append("Invalid x: ").append(x).append(" and y ").append(y).append(", Must be within 1 - 65535");
            Main.pushConsoleMessage();
        }
    }
    private static void sendKey(int c, int flag) {
        input.type.setValue(WinUser.INPUT.INPUT_KEYBOARD);
        input.input.setType("ki");
        input.input.ki.wScan.setValue(0);
        input.input.ki.time.setValue(0);
        input.input.ki.dwExtraInfo.setValue(0);
        input.input.ki.wVk.setValue(c);
        input.input.ki.dwFlags.setValue(flag);
        WinUser.INPUT[] inputs = { input };
        User32.INSTANCE.SendInput(nInput, inputs, input.size());
    }

    public static void mouseMoveStraight(int targetX, int targetY, boolean absolute, int delay) {
        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point currentPosition = pointerInfo.getLocation();

        if (targetX >= 1 && targetX <= 65535 && targetY >= 1 && targetY <= 65535) {
            input.input.setType("mi");
            input.type.setValue(WinUser.INPUT.INPUT_MOUSE);

            int deltaX = (int) (targetX - currentPosition.getX());
            int deltaY = (int) (targetY - currentPosition.getY());

            long startTime = System.currentTimeMillis();
            long elapsedTime = 0;
            double nextX, nextY, progress;

            while (elapsedTime < delay) {
                progress = Math.min(1.0, (double) elapsedTime / delay);
                nextX = (currentPosition.getX() + (progress * deltaX));
                nextY = (currentPosition.getY() + (progress * deltaY));

                input.input.mi.dx.setValue((long) nextX * SCREEN_SCALE_FACTOR_X);
                input.input.mi.dy.setValue((long) nextY * SCREEN_SCALE_FACTOR_Y);
                input.input.mi.dwFlags.setValue(absolute ? MOUSEEVENTF_ABS | MOUSEEVENTF_MOVE : MOUSEEVENTF_MOVE);

                WinUser.INPUT[] inputs = {input};
                User32.INSTANCE.SendInput(nInput, inputs, input.size());
               try {
                   Thread.sleep(5); // Sleep for a short duration to prevent excessive CPU usage
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                elapsedTime = System.currentTimeMillis() - startTime;
            }

            //add a direct click at the target location to account for any precision lost in the math
            input.input.mi.dx.setValue((long) targetX * SCREEN_SCALE_FACTOR_X);
            input.input.mi.dy.setValue((long) targetY * SCREEN_SCALE_FACTOR_Y);
            input.input.mi.dwFlags.setValue(absolute ? MOUSEEVENTF_ABS | MOUSEEVENTF_MOVE : MOUSEEVENTF_MOVE);

            WinUser.INPUT[] inputs = {input};
            User32.INSTANCE.SendInput(nInput, inputs, input.size());

        } else {
            Main.getConsoleBuffer().append("Invalid targetX: ").append(targetX).append(" and targetY ").append(targetY).append(", Must be within 1 - 65535");
            Main.pushConsoleMessage();
        }
    }

    public static void getMousePosition()
    {
        PointerInfo info = MouseInfo.getPointerInfo();
        Main.getConsoleBuffer().append("mousePosX: ").append(info.getLocation().getX()).append(" mousePosY: ").append(info.getLocation().getY());
        Main.pushConsoleMessage();
    }
}