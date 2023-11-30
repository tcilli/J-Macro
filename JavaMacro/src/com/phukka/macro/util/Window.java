package com.phukka.macro.util;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.phukka.macro.Main;

import java.util.Arrays;

/**
 * The {@link Window} class is responsible for
 * getting the active window see {@link #getActive()}
 */
public class Window {

	/**
	 * The buffer to hold the character of the windows title
	 */
	public static final char[] BUFFER = new char[128];

	/**
	 * Gets the ForegroundWindow title
	 * @return lowercase {@link String}.
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
		Main.getConsoleBuffer().append("Active window: ").append(BUFFER).append("\n");
		Main.pushConsoleMessage();
	}
}
