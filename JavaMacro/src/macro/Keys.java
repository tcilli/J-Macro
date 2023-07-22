/**
 * Keys.java.
 * <p>
 *    A class that handles key conversions and key blocking.
 * </p>
 *
 */
package macro;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public final class Keys {

    /**
     * If a user has added the flag "consume" in the macro file,
     * Then the triggering key for that macro will be added to this list.
     * This list is checked before sending a key event to the OS.
     * If the key is in this list, then the key event will not be sent.
     */
    private static final Map<Short, Short> consumableKeys = new HashMap<>();

    private static final Map<String, Short> keyMap = new HashMap<>();

    public static final int SPECIAL_KEY_OFFSET = 1000;

    static {
        keyMap.put("shift", (short) KeyEvent.VK_SHIFT);
        keyMap.put("control", (short) KeyEvent.VK_CONTROL);
        keyMap.put("ctrl", (short) KeyEvent.VK_CONTROL);
        keyMap.put("capslock", (short) KeyEvent.VK_CAPS_LOCK);
        keyMap.put("alt", (short) KeyEvent.VK_ALT);
        keyMap.put("meta", (short) KeyEvent.VK_META);
        keyMap.put("tab", (short) KeyEvent.VK_TAB);
        keyMap.put("space", (short) KeyEvent.VK_SPACE);
        keyMap.put("enter", (short) KeyEvent.VK_ENTER);
        keyMap.put("backspace", (short) KeyEvent.VK_BACK_SPACE);
        keyMap.put("escape", (short) KeyEvent.VK_ESCAPE);
        keyMap.put("delete", (short) KeyEvent.VK_DELETE);
        keyMap.put("home", (short) KeyEvent.VK_HOME);
        keyMap.put("end", (short) KeyEvent.VK_END);
        keyMap.put("pageup", (short) KeyEvent.VK_PAGE_UP);
        keyMap.put("pagedown", (short) KeyEvent.VK_PAGE_DOWN);
        keyMap.put("up", (short) (KeyEvent.VK_UP + SPECIAL_KEY_OFFSET));
        keyMap.put("down", (short) (KeyEvent.VK_DOWN + SPECIAL_KEY_OFFSET));
        keyMap.put("left", (short) (KeyEvent.VK_LEFT + SPECIAL_KEY_OFFSET));
        keyMap.put("right", (short) (KeyEvent.VK_RIGHT + SPECIAL_KEY_OFFSET));

        // F Keys
        for (int i = 1; i < 25; i++) {
            if (i < 13) {
                keyMap.put("f" + i, (short) ((KeyEvent.VK_F1 + (i - 1)) + SPECIAL_KEY_OFFSET));
            } else {
                keyMap.put("f" + i, (short) ((KeyEvent.VK_F13 + (i - 13)) + SPECIAL_KEY_OFFSET));
            }
        }
    }

    public static short getKeyCode(final String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Key string cannot be null or empty");
        }
        if (s.length() == 1) {
            return (short) s.charAt(0);
        }
        final short keyCode = keyMap.getOrDefault(s.toLowerCase(), (short) 0);

        if (keyCode == 0) {
            throw new IllegalArgumentException("Unknown key: " + s);
        }
        return keyCode;
    }

    public static void addKeyToConsumableMap(final short keyCode) {
        consumableKeys.put(keyCode, keyCode);
    }
    public static void clearConsumableKeys() {
        consumableKeys.clear();
    }
    public static boolean containsConsumableKey(final short keyCode) {
        return consumableKeys.containsKey(keyCode);
    }
}