package macro;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;

import java.util.Arrays;

public class Window {

    public static final char[] buffer = new char[128];

    public static String getActive() {
        Arrays.fill(buffer, '\0');
        User32.INSTANCE.GetWindowText(User32.INSTANCE.GetForegroundWindow(), buffer, buffer.length);
        return Native.toString(buffer);
    }

    public static void printActive() {
        Main.console.append("Active window: ").append(Window.getActive()).append("\n");
        Main.pushConsoleMessage();
    }
}
