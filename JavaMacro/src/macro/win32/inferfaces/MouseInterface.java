package macro.win32.inferfaces;
import com.sun.jna.Native;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.StdCallLibrary;

public interface MouseInterface extends StdCallLibrary, User32 {

    MouseInterface INSTANCE = Native.load("user32", MouseInterface.class);
    
    void mouse_event(int dwFlags, int dx, int dy, int dwData, int dwExtraInfo);

    LRESULT mouseProc(int code, WPARAM wParam, LPARAM lParam);
}