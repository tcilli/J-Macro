package macro.win32.inferfaces;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.StdCallLibrary;

import com.sun.jna.win32.W32APIOptions;

/**
 * Provides access to the Windows User32 library.
 * Specifically, the keyboard functions.
 */
public interface KeyboardInterface extends User32, StdCallLibrary {

    KeyboardInterface winUser32 = Native.load("user32", KeyboardInterface.class, W32APIOptions.DEFAULT_OPTIONS);

    void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);

    short VkKeyScan(char ch);

    short GetAsyncKeyState( int i);

}