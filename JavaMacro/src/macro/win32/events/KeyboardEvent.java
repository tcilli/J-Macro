package macro.win32.events;

import com.sun.jna.platform.win32.User32;
import macro.win32.inferfaces.KeyboardInterface;

public class KeyboardEvent {

    public static void sendKeyboardEvent(char[] charArray) {

        boolean shiftActive = (User32.INSTANCE.GetAsyncKeyState(VK_SHIFT) & 0x8000) != 0;

        for (char c : charArray) {

            if (shiftActive) {
                KeyboardInterface.winUser32.keybd_event(VK_SHIFT, (byte) 0, KEY_UP, 0);
            }

            short result = KeyboardInterface.winUser32.VkKeyScan(c);
            byte virtualKeyCode = (byte) (result & 0xFF);
            byte shiftState = (byte) (result >> 8);
            boolean shiftKey = (shiftState & 0x01) != 0;

            int dwFlags = 0;

            if (shiftKey) {
                dwFlags |= SHIFT_DOWN;
                KeyboardInterface.winUser32.keybd_event(VK_SHIFT, (byte) 0, KEY_DOWN, 0);
            }

            KeyboardInterface.winUser32.keybd_event(virtualKeyCode, (byte) 0, dwFlags, 0);
            KeyboardInterface.winUser32.keybd_event(virtualKeyCode, (byte) 0, dwFlags | KEY_UP, 0);

            if (shiftKey) {
                KeyboardInterface.winUser32.keybd_event(VK_SHIFT, (byte) 0, KEY_UP, 0);
            }
            if (shiftActive) {
                KeyboardInterface.winUser32.keybd_event(VK_SHIFT, (byte) 0, KEY_DOWN, 0);
            }
        }
    }

    private static final int KEY_DOWN = 0x0000;
    private static final int SHIFT_DOWN = 0x0001;
    private static final int KEY_UP = 0x0002;
    private static final byte VK_SHIFT = 0x10;
}