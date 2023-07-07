package macro.jnative;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;

import java.util.Arrays;

public class Window {

    public static final char[] buffer = new char[128];
    /**
     *
     * @return window with spaces removed and to lowercase
     */
    public static String getActive() {
        Arrays.fill(buffer, '\0');
        User32.INSTANCE.GetWindowText(User32.INSTANCE.GetForegroundWindow(), buffer, 128);
        String s = Native.toString(buffer);
        s = s.replaceAll(" ", "");
        s = s.toLowerCase();
        return s;
    }
}
