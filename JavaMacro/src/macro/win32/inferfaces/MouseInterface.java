package macro.win32.inferfaces;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;

public interface MouseInterface extends Library {

    MouseInterface winUser32 = (MouseInterface) Native.load("user32", User32.class);

    // Mouse event simulation function
    void mouse_event(DWORD dwFlags, DWORD dx, DWORD dy, DWORD dwData, BaseTSD.ULONG_PTR dwExtraInfo);

    // allow mouse to move to absolute position -includes other monitors
    WinDef.BOOL SetCursorPos(int X, int Y);
}