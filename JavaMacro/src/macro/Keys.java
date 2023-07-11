package macro;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import static macro.jnative.NativeInput.pressKey;
import static macro.jnative.NativeInput.pressKeyDown;
import static macro.jnative.NativeInput.pressKeyUp;

public class Keys {

    public static final String ESC = "Escape";
    public static final String MOUSE = "mouse";

    /**
     * A map of characters to keycodes.
     */
    private static final Map<Character, Integer> keymap = new HashMap<>();

    /**
     * Returns true if the given character requires the shift key to be pressed
     * @param c The character to check
     * @return  True if the character requires shift, false otherwise
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
            int keycode = keymap.get(c);
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
            keymap.put(c, KeyEvent.VK_A + (c - 'a'));
            keymap.put(Character.toUpperCase(c), KeyEvent.VK_A + (c - 'a'));
        }

        // Load keycodes for digits
        for (char c = '0'; c <= '9'; ++c) {
            keymap.put(c, KeyEvent.VK_0 + (c - '0'));
        }

        // Load keycodes for common punctuation marks and symbols
        keymap.put('`', KeyEvent.VK_BACK_QUOTE);
        keymap.put('~', KeyEvent.VK_BACK_QUOTE); // Requires Shift
        keymap.put('-', KeyEvent.VK_MINUS);
        keymap.put('_', KeyEvent.VK_MINUS); // Requires Shift
        keymap.put('=', KeyEvent.VK_EQUALS);
        keymap.put('+', KeyEvent.VK_EQUALS); // Requires Shift
        keymap.put('[', KeyEvent.VK_OPEN_BRACKET);
        keymap.put('{', KeyEvent.VK_OPEN_BRACKET); // Requires Shift
        keymap.put(']', KeyEvent.VK_CLOSE_BRACKET);
        keymap.put('}', KeyEvent.VK_CLOSE_BRACKET); // Requires Shift
        keymap.put('\\', KeyEvent.VK_BACK_SLASH);
        keymap.put('|', KeyEvent.VK_BACK_SLASH); // Requires Shift
        keymap.put(';', KeyEvent.VK_SEMICOLON);
        keymap.put(':', KeyEvent.VK_SEMICOLON); // Requires Shift
        keymap.put('\'', KeyEvent.VK_QUOTE);
        keymap.put('\"', KeyEvent.VK_QUOTE); // Requires Shift
        keymap.put(',', KeyEvent.VK_COMMA);
        keymap.put('<', KeyEvent.VK_COMMA); // Requires Shift
        keymap.put('.', KeyEvent.VK_PERIOD);
        keymap.put('>', KeyEvent.VK_PERIOD); // Requires Shift
        keymap.put('/', KeyEvent.VK_SLASH);
        keymap.put('?', KeyEvent.VK_SLASH); // Requires Shift
        keymap.put(' ', KeyEvent.VK_SPACE);
        keymap.put('\t', KeyEvent.VK_TAB);
        keymap.put('\n', KeyEvent.VK_ENTER);

        // Other keys
        keymap.put('!', KeyEvent.VK_1); // Requires Shift
        keymap.put('@', KeyEvent.VK_2); // Requires Shift
        keymap.put('#', KeyEvent.VK_3); // Requires Shift
        keymap.put('$', KeyEvent.VK_4); // Requires Shift
        keymap.put('%', KeyEvent.VK_5); // Requires Shift
        keymap.put('^', KeyEvent.VK_6); // Requires Shift
        keymap.put('&', KeyEvent.VK_7); // Requires Shift
        keymap.put('*', KeyEvent.VK_8); // Requires Shift
        keymap.put('(', KeyEvent.VK_9); // Requires Shift
        keymap.put(')', KeyEvent.VK_0); // Requires Shift
    }
}
