/**
 * Keys.java.
 * <p>
 *    A class that converts characters to keycodes.
 * </p>
 *
 */
package macro;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import static macro.jnative.NativeInput.pressKey;
import static macro.jnative.NativeInput.pressKeyDown;
import static macro.jnative.NativeInput.pressKeyUp;

public class Keys {

    // Prevent instantiation
    private Keys() {}

    /**
     * A map of characters to keycodes.
     */
    private static final Map<Character, Integer> KEYMAP = new HashMap<>();

    /**
     * Returns true if the given character requires the shift key to be pressed.
     * @param c The character to check.
     * @return  True if the character requires shift to be pressed, false otherwise.
     */
    public static boolean keyRequiresShift(final char c) {
        return Character.isUpperCase(c)
                || "~!@#$%^&*()_+{}|:\"<>?".indexOf(c) >= 0;
    }

    /**
     * Sends a string of characters to the active window.
     * @param s The string to send.
     */
    public static void sendString(final String s) {
        // Send each character in the string
        for (char c : s.toCharArray()) {
            int keycode = KEYMAP.get(c);
            boolean shiftNeeded = keyRequiresShift(c);
            if (shiftNeeded) {
                pressKeyDown(KeyEvent.VK_SHIFT);
            }
            pressKey(keycode);
            if (shiftNeeded) {
                pressKeyUp(KeyEvent.VK_SHIFT);
            }
        }
    }

    /**
     *
     * Loads the keymap with keycodes for common characters.
     */
    public static void loadKeyMap() {
        // Load keycodes for lowercase and uppercase characters
        for (char c = 'a'; c <= 'z'; ++c) {
            KEYMAP.put(c, KeyEvent.VK_A + (c - 'a'));
            KEYMAP.put(Character.toUpperCase(c), KeyEvent.VK_A + (c - 'a'));
        }

        // Load keycodes for digits
        for (char c = '0'; c <= '9'; ++c) {
            KEYMAP.put(c, KeyEvent.VK_0 + (c - '0'));
        }

        // Load keycodes for common punctuation marks and symbols
        KEYMAP.put('`', KeyEvent.VK_BACK_QUOTE);
        KEYMAP.put('~', KeyEvent.VK_BACK_QUOTE); // Requires Shift
        KEYMAP.put('-', KeyEvent.VK_MINUS);
        KEYMAP.put('_', KeyEvent.VK_MINUS); // Requires Shift
        KEYMAP.put('=', KeyEvent.VK_EQUALS);
        KEYMAP.put('+', KeyEvent.VK_EQUALS); // Requires Shift
        KEYMAP.put('[', KeyEvent.VK_OPEN_BRACKET);
        KEYMAP.put('{', KeyEvent.VK_OPEN_BRACKET); // Requires Shift
        KEYMAP.put(']', KeyEvent.VK_CLOSE_BRACKET);
        KEYMAP.put('}', KeyEvent.VK_CLOSE_BRACKET); // Requires Shift
        KEYMAP.put('\\', KeyEvent.VK_BACK_SLASH);
        KEYMAP.put('|', KeyEvent.VK_BACK_SLASH); // Requires Shift
        KEYMAP.put(';', KeyEvent.VK_SEMICOLON);
        KEYMAP.put(':', KeyEvent.VK_SEMICOLON); // Requires Shift
        KEYMAP.put('\'', KeyEvent.VK_QUOTE);
        KEYMAP.put('\"', KeyEvent.VK_QUOTE); // Requires Shift
        KEYMAP.put(',', KeyEvent.VK_COMMA);
        KEYMAP.put('<', KeyEvent.VK_COMMA); // Requires Shift
        KEYMAP.put('.', KeyEvent.VK_PERIOD);
        KEYMAP.put('>', KeyEvent.VK_PERIOD); // Requires Shift
        KEYMAP.put('/', KeyEvent.VK_SLASH);
        KEYMAP.put('?', KeyEvent.VK_SLASH); // Requires Shift
        KEYMAP.put(' ', KeyEvent.VK_SPACE);
        KEYMAP.put('\t', KeyEvent.VK_TAB);
        KEYMAP.put('\n', KeyEvent.VK_ENTER);

        // Other keys
        KEYMAP.put('!', KeyEvent.VK_1); // Requires Shift
        KEYMAP.put('@', KeyEvent.VK_2); // Requires Shift
        KEYMAP.put('#', KeyEvent.VK_3); // Requires Shift
        KEYMAP.put('$', KeyEvent.VK_4); // Requires Shift
        KEYMAP.put('%', KeyEvent.VK_5); // Requires Shift
        KEYMAP.put('^', KeyEvent.VK_6); // Requires Shift
        KEYMAP.put('&', KeyEvent.VK_7); // Requires Shift
        KEYMAP.put('*', KeyEvent.VK_8); // Requires Shift
        KEYMAP.put('(', KeyEvent.VK_9); // Requires Shift
        KEYMAP.put(')', KeyEvent.VK_0); // Requires Shift
    }

    // Just a few common keys used in the macro
    public static final String ESC = "ondown-Escape";
    public static final String MOUSE = "mouse";
}
