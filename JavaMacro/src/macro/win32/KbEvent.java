package macro.win32;

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
public class KbEvent {

    // The 'dwFlag' for signaling a key down event.
    public static final int KEYDOWN = 0x0000;

    // The 'dwFlag' for signaling a shift key event.
    public static final int SHIFT_DOWN = 0x0001;

    //The 'dwFlag' for signaling a key up event.
    public static final int KEYUP = 0x0002;

    //The virtual code of the shift key (Windows mapping).
    public static final int VK_SHIFT = 0x10;

    //The shift key flag.
    public static final int SHIFT_KEY = 0x01;

    /**
     * Simulates keystrokes by sending virtual keycode to the active window.
     * characters that require the shift key to be pressed are sent with the shift key pressed.
     * @param charArray The array of characters to send.
     */
    public static void send_characters(char[] charArray) {

        for (char c : charArray) {
            int result = KbInterface.winUser32.VkKeyScan(c);
            int virtualKeyCode = result & 0xFF;
            int shiftState = result >> 8;

            boolean shiftRequired = (shiftState & SHIFT_KEY) != 0;

            int dwFlags = 0;

            /*
             * Pressing the modifier keys.
             */
            if (shiftRequired) {
                dwFlags |= SHIFT_DOWN;
                KbInterface.winUser32.keybd_event((byte) VK_SHIFT, (byte) 0, KEYDOWN, 0);
            }

            /*
             * Pressing the key down
             */
            KbInterface.winUser32.keybd_event((byte) virtualKeyCode, (byte) 0, dwFlags, 0);

            /*
             * Releasing the key up
             */
            KbInterface.winUser32.keybd_event((byte) virtualKeyCode, (byte) 0, dwFlags | KEYUP, 0);

            /*
             * Releasing the modifier keys.
             */
            if (shiftRequired) {
                KbInterface.winUser32.keybd_event((byte) VK_SHIFT, (byte) 0, KEYUP, 0);
            }
        }
    }
}
