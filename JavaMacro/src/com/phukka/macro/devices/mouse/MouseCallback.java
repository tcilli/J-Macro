package com.phukka.macro.devices.mouse;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.phukka.macro.Main;

public class MouseCallback {

    public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.MSLLHOOKSTRUCT lParam) {

        if (nCode >= 0) {
            x = lParam.pt.x;
            y = lParam.pt.y;

            if (wParam.intValue() == 0x0200 && disableUserMovement && lParam.dwExtraInfo.longValue() == 0) {
                return new WinDef.LRESULT(1);
            }

            switch (wParam.intValue()) {
                case 0x0201 -> Main.getScriptContainer().handleKey((short) 2000); //left click
                case 0x0202 -> Main.getScriptContainer().handleKey((short) -2000); //right click
                case 0x0204 -> Main.getScriptContainer().handleKey((short) 2001);
                case 0x0205 -> Main.getScriptContainer().handleKey((short) -2001);
                case 0x0207 -> Main.getScriptContainer().handleKey((short) 2002);
                case 0x0208 -> Main.getScriptContainer().handleKey((short) -2002);

                case 0x020B, 0x020C -> {
                    int whichButton = (wParam.intValue() >> 16) & 0xFFFF;
                    int type = (wParam.intValue() == 0x020B) ? 1 : 2;
                    int button = (whichButton == 0x0001) ? (type == 1 ? 2003 : -2003) : (type == 1 ? 2004 : -2004);
                    System.out.println("Button: " + button +" Type: " + type + " Which: " + whichButton + " x " + x + " y " + y);
                    Main.getScriptContainer().handleKey((short) button);
                }
            }
        }
        return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
    }

    private static int x = 0;
    private static int y = 0;

    public static int getX() {
        return x;
    }
    public static int getY() {
        return y;
    }

    private static final Object lock = new Object();

    private static boolean disableUserMovement = false;

    public static void disableUserMovement() {
        synchronized (lock) {
            disableUserMovement = true;
        }
    }
    public static void enableUserMovement() {
        synchronized (lock) {
            disableUserMovement = false;
        }
    }
}