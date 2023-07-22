package macro.win32.inferfaces;
import com.sun.jna.Native;

import com.sun.jna.win32.StdCallLibrary;

public interface MouseInterface extends StdCallLibrary {

    MouseInterface INSTANCE = Native.load("user32", MouseInterface.class);
    
    void mouse_event(int dwFlags, int dx, int dy, int dwData, int dwExtraInfo);
}