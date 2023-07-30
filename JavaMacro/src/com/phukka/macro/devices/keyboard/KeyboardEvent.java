package com.phukka.macro.devices.keyboard;

import com.sun.jna.platform.win32.User32;


/**
 * A class for sending virtual keys to the active window.
 * A virtual key, also known as a virtual keycode or VK code,
 * is a numeric value that represents a specific key on a computer keyboard or
 * other input device. It is a software abstraction used by operating
 * systems and applications to identify and handle keyboard input.
 * <p>
 * Virtual keys are used to provide a consistent way of referring to keys across
 * different keyboard layouts and hardware configurations. Rather than relying
 * on the physical location or appearance of a key,
 * software can use virtual keycodes to recognize and interpret user input
 * regardless of the actual keyboard being used.
 * </p>
 * <p>
 * Each key on a standard keyboard is assigned a unique virtual key value.
 * For example, the virtual key code for the letter 'A' is different from
 * the virtual key code for the 'Enter' key. These codes are typically
 * represented as numerical constants in programming languages,
 * making it easier for developers to handle keyboard input in their applications.
 * <p>
 * Virtual keys are useful for implementing keyboard shortcuts,
 * capturing user input, and handling various keyboard-related events.
 * They allow software to respond to key presses, releases, and combinations,
 * enabling users to interact with applications effectively.
 * <p>
 * It's important to note that virtual keys are distinct from scan codes,
 * which represent the electrical signals sent by physical keys.
 * Scan codes are specific to the hardware and may differ across different keyboard models,
 * while virtual keys provide a consistent and platform-independent way of referring to keys.
 * </p>
 */
public class KeyboardEvent {

    // The 'dwFlag' for signaling a key down event.
    public static final int KEY_DOWN = 0x0000;

    // The 'dwFlag' for signaling a shift key event.
    public static final int SHIFT_DOWN = 0x0001;

    //The 'dwFlag' for signaling a key up event.
    public static final int KEY_UP = 0x0002;

    //The virtual code of the shift key (Windows mapping).
    public static final byte VK_SHIFT = 0x10;

    //The virtual code of the caps lock key (Windows mapping).
    public static final byte VK_CAPS_LOCK = 0x14;

    public static final byte NULL = 0;

    public static void send(String message) {
        send(message.toCharArray());
    }

    public static void send(char[] charArray) {

        //need to check if shift is already active
        boolean shiftActive = (User32.INSTANCE.GetAsyncKeyState(VK_SHIFT) & 0x8000) != 0;

        //need to check if caps lock is already active
        boolean capslockActive = (User32.INSTANCE.GetAsyncKeyState(VK_CAPS_LOCK) & 0x8000) != 0;

        //if shift is active we need to deactivate it
        if (shiftActive) {
            KeyboardInterface.INSTANCE.keybd_event(VK_SHIFT, NULL, KEY_UP, 0);
        }
        //if caps lock is active we need to deactivate it
        if (capslockActive) {
            KeyboardInterface.INSTANCE.keybd_event(VK_CAPS_LOCK, NULL, KEY_UP, 0);
        }

        for (char c : charArray) {

            //VkKeyScan is a function provided by the Windows API
            // that translates a character to the corresponding virtual-key code
            // and shift state for the current keyboard.
            short result = KeyboardInterface.INSTANCE.VkKeyScan(c);

            // Extract the virtual key code
            byte virtualKeyCode = (byte) (result & 0xFF);

            // Extract the shift state
            byte shiftState = (byte) (result >> 8);

            //1 if the SHIFT key is pressed.
            //2 if the CTRL key is pressed.
            //4 if the ALT key is pressed.
            boolean shiftKey = (shiftState & 0x01) != 0;

            int dwFlags = 0;

            // virtualKeyCode requires shift key to be pressed
            if (shiftKey) {
                dwFlags |= SHIFT_DOWN;
                KeyboardInterface.INSTANCE.keybd_event(VK_SHIFT, NULL, KEY_DOWN, 0);
            }

            //send the key down
            KeyboardInterface.INSTANCE.keybd_event(virtualKeyCode, NULL, dwFlags, 0);

            //release the key
            KeyboardInterface.INSTANCE.keybd_event(virtualKeyCode, NULL, dwFlags | KEY_UP, 0);

            //if the virtual key required shift key to be pressed
            //we need to release the shift key
            if (shiftKey) {
                KeyboardInterface.INSTANCE.keybd_event(VK_SHIFT, NULL, KEY_UP, 0);
            }
        }

        //if shift was active before we need to reactivate it
        if (shiftActive) {
            KeyboardInterface.INSTANCE.keybd_event(VK_SHIFT, NULL, KEY_DOWN, 0);
        }
        //if caps lock was active before we need to reactivate it
        if (capslockActive) {
            KeyboardInterface.INSTANCE.keybd_event(VK_CAPS_LOCK, NULL, KEY_DOWN, 0);
        }
    }
}
