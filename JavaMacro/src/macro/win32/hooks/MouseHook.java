package macro.win32.hooks;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import macro.Main;
import macro.win32.callbacks.MouseCallback;

public class MouseHook implements Runnable {

    private final WinDef.HMODULE hModule;
    private final MouseCallback mouseCallback;
    private WinUser.HHOOK hHookM;

    public MouseHook(final MouseCallback mouseCallback) {
        this.hModule = Kernel32.INSTANCE.GetModuleHandle(null);
        this.mouseCallback = mouseCallback;
    }

    @Override
    public void run() {
        try {
            WinUser.LowLevelMouseProc mouseHook = mouseCallback::callback;

            this.hHookM = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_MOUSE_LL, mouseHook, hModule, 0);

            if (this.hHookM == null) {
                int errorCode = Kernel32.INSTANCE.GetLastError();
                Main.getConsoleBuffer().append("Failed to set up mouse hook. Error code: ").append(errorCode);
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
        if (User32.INSTANCE.UnhookWindowsHookEx(hHookM)) {
            Main.getConsoleBuffer().append("Mouse hook successfully unhooked");
            Main.pushConsoleMessage();
        } else {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            Main.getConsoleBuffer().append("Failed to unhook. Error code: ").append(errorCode);
            Main.pushConsoleMessage();
        }
    }
}
