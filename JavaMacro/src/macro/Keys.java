/**
 * Keys.java.
 * <p>
 *    A class that converts characters to keycodes.
 * </p>
 *
 */
package macro;

import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;


import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.sun.jna.platform.win32.User32;
import macro.win32.KbInterface;

import static macro.jnative.NativeInput.pressKey;
import static macro.jnative.NativeInput.pressKeyDown;
import static macro.jnative.NativeInput.pressKeyUp;

public final class Keys {

    private Keys() {

    }

    /**
     * A map of characters to keycodes.
     */
    private static final Map<Character, Integer> VK_KEYMAP = new HashMap<>();

    /**
     * Returns true if the given character requires the shift key to be pressed.
     * @param c The character to check.
     * @return  True if the character requires shift to be pressed,
     * false otherwise.
     */
    public static boolean keyRequiresShift(final char c) {
        return Character.isUpperCase(c)
            || "~!@#$%^&*()_+{}|:\"<>?".indexOf(c) >= 0;
    }

    /**
     * Sends a string of characters to the active window.
     * The string is converted into a virtualKeyCode
     * note the VK_
     * @param s The string to send.
     */
    public static void sendString(final String s) {
        // Send each character in the string
        for (char c : s.toCharArray()) {
            int keycode = VK_KEYMAP.getOrDefault(c, 0);
            boolean shiftNeeded = keyRequiresShift(c);
            if (shiftNeeded) {
                pressKeyDown(KeyEvent.VK_SHIFT);
            }
            pressKey(keycode);
            if (shiftNeeded) {
                pressKeyUp(KeyEvent.VK_SHIFT);
            }
            if (keycode == 0) {
                System.err.println("Unknown character: " + c);
            }
        }
    }

    /**
     *
     * Loads the keymap with keycodes for common characters.
     */
    static  {
        // Load keycodes for lowercase and uppercase characters
        for (char c = 'a'; c <= 'z'; ++c) {
            VK_KEYMAP.put(c, KeyEvent.VK_A + (c - 'a'));
            VK_KEYMAP.put(Character.toUpperCase(c), KeyEvent.VK_A + (c - 'a'));
        }

        // Load keycodes for digits
        for (char c = '0'; c <= '9'; ++c) {
            VK_KEYMAP.put(c, KeyEvent.VK_0 + (c - '0'));
        }

        // Load keycodes for common punctuation marks and symbols
        VK_KEYMAP.put('`', KeyEvent.VK_BACK_QUOTE);
        VK_KEYMAP.put('~', KeyEvent.VK_BACK_QUOTE); // Requires Shift
        VK_KEYMAP.put('-', KeyEvent.VK_MINUS);
        VK_KEYMAP.put('_', KeyEvent.VK_MINUS); // Requires Shift
        VK_KEYMAP.put('=', KeyEvent.VK_EQUALS);
        VK_KEYMAP.put('+', KeyEvent.VK_EQUALS); // Requires Shift
        VK_KEYMAP.put('[', KeyEvent.VK_OPEN_BRACKET);
        VK_KEYMAP.put('{', KeyEvent.VK_OPEN_BRACKET); // Requires Shift
        VK_KEYMAP.put(']', KeyEvent.VK_CLOSE_BRACKET);
        VK_KEYMAP.put('}', KeyEvent.VK_CLOSE_BRACKET); // Requires Shift
        VK_KEYMAP.put('\\', KeyEvent.VK_BACK_SLASH);
        VK_KEYMAP.put('|', KeyEvent.VK_BACK_SLASH); // Requires Shift
        VK_KEYMAP.put(';', KeyEvent.VK_SEMICOLON);
        VK_KEYMAP.put(':', KeyEvent.VK_SEMICOLON); // Requires Shift
        VK_KEYMAP.put('\'', KeyEvent.VK_QUOTE);
        VK_KEYMAP.put('\"', KeyEvent.VK_QUOTE); // Requires Shift
        VK_KEYMAP.put(',', KeyEvent.VK_COMMA);
        VK_KEYMAP.put('<', KeyEvent.VK_COMMA); // Requires Shift
        VK_KEYMAP.put('.', KeyEvent.VK_PERIOD);
        VK_KEYMAP.put('>', KeyEvent.VK_PERIOD); // Requires Shift
        VK_KEYMAP.put('/', KeyEvent.VK_SLASH);
        VK_KEYMAP.put('?', KeyEvent.VK_SLASH); // Requires Shift
        VK_KEYMAP.put(' ', KeyEvent.VK_SPACE);
        VK_KEYMAP.put('\t', KeyEvent.VK_TAB);
        VK_KEYMAP.put('\n', KeyEvent.VK_ENTER);

        // Other keys
        VK_KEYMAP.put('!', KeyEvent.VK_1); // Requires Shift
        VK_KEYMAP.put('@', KeyEvent.VK_2); // Requires Shift
        VK_KEYMAP.put('#', KeyEvent.VK_3); // Requires Shift
        VK_KEYMAP.put('$', KeyEvent.VK_4); // Requires Shift
        VK_KEYMAP.put('%', KeyEvent.VK_5); // Requires Shift
        VK_KEYMAP.put('^', KeyEvent.VK_6); // Requires Shift
        VK_KEYMAP.put('&', KeyEvent.VK_7); // Requires Shift
        VK_KEYMAP.put('*', KeyEvent.VK_8); // Requires Shift
        VK_KEYMAP.put('(', KeyEvent.VK_9); // Requires Shift
        VK_KEYMAP.put(')', KeyEvent.VK_0); // Requires Shift
    }

    // Map of key names to keycodes
    private static final Map<String, Integer> SC_KEYMAP = new HashMap<>();

    static {
        // yoink those keycodes, ty
        for (Field field : NativeKeyEvent.class.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers()) && Modifier.isStatic(field.getModifiers()) && field.getType() == int.class && field.getName().startsWith("VC_")) {
                try {
                    int keyCode = field.getInt(null);
                    String keyName = field.getName().substring(3);
                    SC_KEYMAP.put(keyName, keyCode);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static int getKeyCode(String keyText) {
        keyText = keyText.toUpperCase();

        // Get the corresponding keycode from the map
        Integer keyCode = SC_KEYMAP.get(keyText);

        if (keyCode == null) {
            System.out.println("No keycode found for '" + keyText + "'");
            return NativeKeyEvent.VC_UNDEFINED;
        }
        return keyCode;
    }

    /**
     * If a user has instructed this key to not be sent to the game.
     * it's added to this list to be blocked from sending.
     */
    private static final Map<Integer, Integer> consumableKeys = new HashMap<>();

    public static void addKeyToConsumableList(final int keyCode) {
        consumableKeys.put(keyCode, keyCode);
    }

    public static boolean containsConsumableKey(final int keyCode) {
        return consumableKeys.containsKey(keyCode);
    }

    public static int virtualKeyCodeToScanCode(final int virtualKeyCode) {
        return KbInterface.winUser32.MapVirtualKeyExA(virtualKeyCode, User32.MAPVK_VK_TO_VSC, null);
    }

    public static int scanCodeToVirtualKeyCode(final int scanCode) {
       return KbInterface.winUser32.MapVirtualKeyExA(scanCode, User32.MAPVK_VSC_TO_VK, null);
    }

    public static char virtualKeyCodeToChar(final int virtualKeyCode) {
        return (char) KbInterface.winUser32.MapVirtualKeyExA(virtualKeyCode, User32.MAPVK_VK_TO_CHAR, null);
    }

    public static char scanCodeToChar(final int scanCode) {
        int virtualKeyCode = scanCodeToVirtualKeyCode(scanCode);
        return (char) virtualKeyCode;
    }
}