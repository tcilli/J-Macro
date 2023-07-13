package macro;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;

import java.util.Arrays;

public class Window {

	public static final int BUFFER_SIZE = 128;
	public static final char[] BUFFER = new char[BUFFER_SIZE];

	public static String getActive() {
		Arrays.fill(BUFFER, '\0');
		User32.INSTANCE.GetWindowText(User32.INSTANCE.GetForegroundWindow(), BUFFER, BUFFER.length);
		return Native.toString(BUFFER);
	}

	public static void printActive() {
		Main.getConsoleBuffer().append("Active window: ").append(Window.getActive()).append("\n");
		Main.pushConsoleMessage();
	}
}
