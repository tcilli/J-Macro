package macro.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import macro.Main;

import java.util.Arrays;

/**
 * The {@link Window} class is responsible for
 * getting the active window see {@link #getActive()}
 */
public class Window {

	/**
	 * The size allocated for the window title {@link #BUFFER}
	 */
	public static final int BUFFER_SIZE = 128;

	/**
	 * The buffer for the window title of size {@link #BUFFER_SIZE}
	 */
	public static final char[] BUFFER = new char[BUFFER_SIZE];

	/**
	 * Gets the active window's title
	 * @return lowercase {@link String} of the active window.
	 */
	public static String getActive() {

		// Clear the window title buffer
		Arrays.fill(BUFFER, '\0');

		// get the window title
		User32.INSTANCE.GetWindowText(User32.INSTANCE.GetForegroundWindow(), BUFFER, BUFFER.length);

		// return the window title as a String
		return Native.toString(BUFFER).toLowerCase();
	}

	/**
	 * Prints the active window to the console
	 */
	public static void printActive() {
		Main.getConsoleBuffer().append("Active window: ").append(Window.getActive()).append("\n");
		Main.pushConsoleMessage();
	}
}
