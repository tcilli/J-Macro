package macro.win32.events;

import com.sun.jna.platform.win32.User32;
import macro.Main;

import java.awt.event.KeyEvent;

public class KeyboardEvent {

    public static void sendKeyboardEvent(char[] charArray) {

        boolean userHoldingShiftKey = (User32.INSTANCE.GetAsyncKeyState(KeyEvent.VK_SHIFT) & 0x8000) != 0;

        if (userHoldingShiftKey) {
            Main.getRobot().keyRelease(KeyEvent.VK_SHIFT);
        }

        for (char c : charArray) {
            short result = User32.INSTANCE.VkKeyScanExW(c, null);

            byte virtualKeyCode = (byte) (result & 0xFF);
            byte shiftState = (byte) (result >> 8);

            boolean shiftKey = (shiftState & 0x01) != 0;

            if (shiftKey) {
                Main.getRobot().keyPress(KeyEvent.VK_SHIFT);
            }

            Main.getRobot().keyPress(virtualKeyCode);
            Main.getRobot().keyRelease(virtualKeyCode);

            if (shiftKey) {
                Main.getRobot().keyRelease(KeyEvent.VK_SHIFT);
            }
        }

        if (userHoldingShiftKey) { 
            Main.getRobot().keyPress(KeyEvent.VK_SHIFT);
        }
    }
}