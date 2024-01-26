package com.phukka.macro.devices;

import com.phukka.macro.Main;
import com.phukka.macro.util.Window;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.win32.W32APIOptions;

import java.util.*;

public class Keyboard implements Runnable
{

    WinUser.HHOOK hHookKb;

    final WinUser.LowLevelKeyboardProc LowLevelKeyboardProc = this::callback;
    final KeyboardInterface INSTANCE;

    public Keyboard()
    {
        INSTANCE = Native.load("user32", KeyboardInterface.class, W32APIOptions.DEFAULT_OPTIONS);
    }

    public void sendKeycode(int... vkCode)
    {
        for (int code : vkCode)
        {
            INSTANCE.keybd_event((byte) code, (byte) 0, 0, 1);
            INSTANCE.keybd_event((byte) code, (byte) 0, 2, 1);
        }
    }

    interface KeyboardInterface extends User32
    {
        void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);
    }

    WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT lParam)
    {
        if (nCode >= 0 && lParam.vkCode > 0 && lParam.dwExtraInfo.longValue() == 0)
        {
            switch (wParam.intValue())
            {
                case WinUser.WM_KEYDOWN, WinUser.WM_SYSKEYDOWN ->
                    notifyListenersKeyPressed((byte) lParam.vkCode);
                case WinUser.WM_KEYUP, WinUser.WM_SYSKEYUP ->
                    notifyListenersKeyReleased((byte) lParam.vkCode);
            }
        }
        return User32.INSTANCE.CallNextHookEx(hHookKb, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
    }

    @Override
    public void run()
    {
        try
        {
            WinUser.HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
            hHookKb = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, LowLevelKeyboardProc, hMod, 0);

            WinUser.MSG msg = new WinUser.MSG();
            while (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
                User32.INSTANCE.TranslateMessage(msg);
                User32.INSTANCE.DispatchMessage(msg);
            }
        } catch (Exception e) {
            Main.getConsoleBuffer().append("An error occurred: ").append(e.getMessage());
            Main.pushConsoleMessage();
        } finally {
            unhook();
        }
    }

    public void unhook()
    {
        if (User32.INSTANCE.UnhookWindowsHookEx(hHookKb))
        {
            Main.getConsoleBuffer().append("Keyboard hook successfully unhooked");
            Main.pushConsoleMessage();
        } else
        {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            Main.getConsoleBuffer().append("Failed to unhook. Error code: ").append(errorCode);
            Main.pushConsoleMessage();
        }
    }

    final HashMap<String, Listener> listeners = new HashMap<>();

    public void addListener(String windowTitle, Listener listener)
    {
        listeners.put(windowTitle, listener);
    }

    public void removeListener(String windowTitle) {
        listeners.remove(windowTitle);
    }

    void notifyListenersKeyPressed(int characterCode) {
        String windowTitle = Window.getActive();
        for (Iterator<String> it = listeners.keySet().iterator(); it.hasNext(); ) {
            String title = it.next();
            if (title.equals(windowTitle) || title.equals("")) {
                listeners.get(title).onKeyPressed(characterCode);
            }
        }
    }

    void notifyListenersKeyReleased(int characterCode) {
        String windowTitle = Window.getActive();
        for (Iterator<String> it = listeners.keySet().iterator(); it.hasNext(); ) {
            String title = it.next();
            if (title.equals(windowTitle) || title.equals("")) {
                listeners.get(title).onKeyReleased(characterCode);
                return;
            }
        }
    }

    public void clear() {
        listeners.clear();
    }

    public interface Listener
    {
        void onKeyPressed(int vkCode);

        void onKeyReleased(int vkCode);
    }
}
