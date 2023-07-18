package macro.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;

import com.sun.jna.win32.W32APIOptions;

/**
 * Provides access to the Windows User32 library.
 * Specifically, the keyboard functions.
 */
public interface KbInterface extends User32, StdCallLibrary {

    KbInterface winUser32 = (KbInterface) Native.load("user32", KbInterface.class, W32APIOptions.DEFAULT_OPTIONS);

    int MapVirtualKeyExA(int uCode, int uMapType, WinDef.HKL dwhkl);

    int ToAscii(int uVirtKey, int uScanCode, byte[] lpKeyState, char[] lpChar, int uFlags);

    void keybd_event(byte bVk, byte bScan, int dwFlags, int dwExtraInfo);

    int VkKeyScanEx(char ch, WinDef.HKL dwhkl);

    boolean SetKeyboardState(byte[] lpKeyState);

    int VkKeyScan(char ch);

    short GetAsyncKeyState( int i);

    short GetKeyState( int i);
}