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
    private static final int MOUSEEVENTF_ABS = 0x8000;

    public static final int MOUSE_BUTTON_LEFT = 1;
    public static final int MOUSE_BUTTON_RIGHT = 2;
    public static final int MOUSE_BUTTON_MIDDLE = 3;
    
    private static final WinUser.INPUT input = new WinUser.INPUT();
    private static final WinDef.DWORD nInput = new WinDef.DWORD(1);

    private static final int SCREEN_SCALE_FACTOR_X = (65535 / User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN));
    private static final int SCREEN_SCALE_FACTOR_Y = (65535 / User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN));


    public static void click(int mouseButton) {
        mouseClick(
                mouseButton == 1 ? MOUSEEVENTF_LEFTDOWN   | MOUSEEVENTF_LEFTUP :
                mouseButton == 2 ? MOUSEEVENTF_RIGHTDOWN  | MOUSEEVENTF_RIGHTUP :
                                   MOUSEEVENTF_MIDDLEDOWN | MOUSEEVENTF_MIDDLEUP);
    }

    public static void clickDown(int mouseButton) {
        mouseClick(
                mouseButton == 1 ? MOUSEEVENTF_LEFTDOWN :
                mouseButton == 2 ? MOUSEEVENTF_RIGHTDOWN :
                                   MOUSEEVENTF_MIDDLEDOWN);
    }

    public static void clickUp(int mouseButton) {
        mouseClick(
                mouseButton == 1 ? MOUSEEVENTF_LEFTUP :
                mouseButton == 2 ? MOUSEEVENTF_RIGHTUP :
                                   MOUSEEVENTF_MIDDLEUP);
    }

    private static void mouseClick(int flags) {
        input.input.setType("mi");
        input.type.setValue(WinUser.INPUT.INPUT_MOUSE);
        input.input.mi.dwFlags.setValue(flags);
        WinUser.INPUT[] inputs = { input };
        User32.INSTANCE.SendInput(nInput, inputs, input.size());
    }

    public static void mouseMove(long mouseData, boolean absolute)
    {
        if ((mouseData & 0xFFFF) != 0) {
            mouseMoveStraight(mouseData, absolute);
            return;
        }
        input.input.setType("mi");
        input.type.setValue(WinUser.INPUT.INPUT_MOUSE);
        input.input.mi.dx.setValue(((mouseData >> 48) & 0xFFFF) * SCREEN_SCALE_FACTOR_X);
        input.input.mi.dy.setValue(((mouseData >> 32) & 0xFFFF) * SCREEN_SCALE_FACTOR_Y);
        input.input.mi.dwFlags.setValue(absolute ? MOUSEEVENTF_ABS | MOUSEEVENTF_MOVE : MOUSEEVENTF_MOVE);
        WinUser.INPUT[] inputs = { input };
        User32.INSTANCE.SendInput(nInput, inputs, input.size());
    }

    public static void moveMouseReturn(long mouseData, boolean absolute) {
        mouseMove(mouseData, absolute);
        click(1);
        mouseMove(mouseData, absolute);
    }


    public static void mouseMoveStraight(long mouseData, boolean absolute) {

        PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        Point currentPosition = pointerInfo.getLocation();

        input.input.setType("mi");
        input.type.setValue(WinUser.INPUT.INPUT_MOUSE);

        int deltaX = (int) (((mouseData >> 48) & 0xFFFF) - currentPosition.getX());
        int deltaY = (int) (((mouseData >> 32) & 0xFFFF) - currentPosition.getY());

        long startTime = System.currentTimeMillis();
        long elapsedTime = 0;
        double nextX;
        double nextY;
        double progress;

        while (elapsedTime < (mouseData & 0xFFFF)) {
            progress = Math.min(1.0, (double) elapsedTime / (mouseData & 0xFFFF));
            nextX = (currentPosition.getX() + (progress * deltaX));
            nextY = (currentPosition.getY() + (progress * deltaY));

            input.input.mi.dx.setValue((long) nextX * SCREEN_SCALE_FACTOR_X);
            input.input.mi.dy.setValue((long) nextY * SCREEN_SCALE_FACTOR_Y);
            input.input.mi.dwFlags.setValue(absolute ? MOUSEEVENTF_ABS | MOUSEEVENTF_MOVE : MOUSEEVENTF_MOVE);

            try {
                Thread.sleep(5); // Sleep for a short duration to prevent excessive CPU usage
                WinUser.INPUT[] inputs = {input};
                User32.INSTANCE.SendInput(nInput, inputs, input.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            elapsedTime = System.currentTimeMillis() - startTime;
        }
    }

    public static void getMousePosition() {
        PointerInfo info = MouseInfo.getPointerInfo();
        Main.getConsoleBuffer().append("mousePosX: ").append(info.getLocation().getX()).append(" mousePosY: ").append(info.getLocation().getY());
        Main.pushConsoleMessage();
    }
}