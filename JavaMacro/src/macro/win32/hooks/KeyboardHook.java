package macro.win32.hooks;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import macro.Main;
import macro.win32.callbacks.KeyboardCallback;

public class KeyboardHook implements Runnable {

    private final WinDef.HMODULE hModule;
    private final KeyboardCallback keyboardCallback;
    private WinUser.HHOOK hHookKb;

    public KeyboardHook(final KeyboardCallback keyboardCallback) {
        this.hModule = Kernel32.INSTANCE.GetModuleHandle(null);
        this.keyboardCallback = keyboardCallback;
    }

    @Override
    public void run() {
        try {

            WinUser.LowLevelKeyboardProc keyboardProc = keyboardCallback::callback;

            hHookKb = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardProc, hModule, 0);

            if (hHookKb == null) {
                int errorCode = Kernel32.INSTANCE.GetLastError();
                Main.getConsoleBuffer().append("Failed to set up keyboard hook. Error code: ").append(errorCode);
                Main.pushConsoleMessage();
                return;
            }

            WinUser.MSG msg = new WinUser.MSG();

            int result;

            while ((result = User32.INSTANCE.GetMessage(msg, null, 0, 0)) != 0) {
                if (result == -1) {
                    Main.getConsoleBuffer().append("Error in GetMessage");
                    Main.pushConsoleMessage();
                    break;
                } else {
                    User32.INSTANCE.TranslateMessage(msg);
                    User32.INSTANCE.DispatchMessage(msg);
                }
            }
        } catch (Exception e) {
            Main.getConsoleBuffer().append("An error occurred: ").append(e.getMessage());
            Main.pushConsoleMessage();
        } finally {
            unhook();
        }
    }

    public void unhook() {
        if (User32.INSTANCE.UnhookWindowsHookEx(hHookKb)) {
            Main.getConsoleBuffer().append("Keyboard hook successfully unhooked");
            Main.pushConsoleMessage();
        } else {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            Main.getConsoleBuffer().append("Failed to unhook. Error code: ").append(errorCode);
            Main.pushConsoleMessage();
        }
    }
}