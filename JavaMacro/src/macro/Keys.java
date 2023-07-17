/**
 * Keys.java.
 * <p>
 *    A class that handles key conversions and key blocking.
 * </p>
 *
 */
package macro;

import macro.win32.KbInterface;

import java.awt.event.KeyEvent;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Keys {

    /**
     * If a user has added the flag "consume" in the macro file,
     * Then the triggering key for that macro will be added to this list.
     * This list is checked before sending a key event to the OS.
     * If the key is in this list, then the key event will not be sent.
     */
    private static final Map<Integer, Integer> consumableKeys = new HashMap<>();

    public static void addKeyToConsumableList(final int keyCode) {
        consumableKeys.put(keyCode, keyCode);
    }
    public static void clearConsumableKeys() {
        consumableKeys.clear();
    }

    public static boolean containsConsumableKey(final int keyCode) {
        return consumableKeys.containsKey(keyCode);
    }

    /**
     * some character unicode's are the same as other virtual key codes
     * We need to offset the virtual keycode to avoid conflicts
     */
    public static final int SPECIAL_KEY_OFFSET = 1000;

    public static int isFunctionKey(String s) {
        s = s.toLowerCase();
        for (int i = 1; i < 24; i++) {
            if (s.equals("f" + i)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Converts a string to a unicode if possible.
     * If no unicode value exists, then the virtual Key code is returned.
     * If the virtual key code conflicts with a unicode then (virtualKeyCode + 1000) is returned.
     * @param s The string to converted
     * @return The unicode value of the string or the virtual key code with conditions, if no unicode exists.
     */
    public static int getKeyCode2(final String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }
        if (s.length() == 1) {
            return s.charAt(0);
        }

        int functionalKey = isFunctionKey(s);

        if (functionalKey > 0) {
            if (functionalKey < 13) {
                return (KeyEvent.VK_F1 + (functionalKey - 1)) + SPECIAL_KEY_OFFSET;
            }
            if (functionalKey < 25) {
                return (KeyEvent.VK_F13 + functionalKey - 1) + SPECIAL_KEY_OFFSET;
            }
        } else if (s.equalsIgnoreCase("shift")) {
            return KeyEvent.VK_SHIFT;
        } else if (s.equalsIgnoreCase("control")) {
            return KeyEvent.VK_CONTROL;
        } else if (s.equalsIgnoreCase("alt")) {
            return KeyEvent.VK_ALT;
        } else if (s.equalsIgnoreCase("meta")) {
            return KeyEvent.VK_META;
        } else if (s.equalsIgnoreCase("tab")) {
            return KeyEvent.VK_TAB;
        } else if (s.equalsIgnoreCase("space")) {
            return KeyEvent.VK_SPACE;
        } else if (s.equalsIgnoreCase("enter")) {
            return KeyEvent.VK_ENTER;
        } else if (s.equalsIgnoreCase("backspace")) {
            return KeyEvent.VK_BACK_SPACE;
        } else if (s.equalsIgnoreCase("escape")) {
            return KeyEvent.VK_ESCAPE;
        } else if (s.equalsIgnoreCase("delete")) {
            return KeyEvent.VK_DELETE;
        } else if (s.equalsIgnoreCase("home")) {
            return KeyEvent.VK_HOME;
        } else if (s.equalsIgnoreCase("end")) {
            return KeyEvent.VK_END;
        } else if (s.equalsIgnoreCase("pageup")) {
            return KeyEvent.VK_PAGE_UP;
        } else if (s.equalsIgnoreCase("pagedown")) {
            return KeyEvent.VK_PAGE_DOWN;
        } else if (s.equalsIgnoreCase("up")) {
            return KeyEvent.VK_UP + SPECIAL_KEY_OFFSET;
        } else if (s.equalsIgnoreCase("down")) {
            return KeyEvent.VK_DOWN + SPECIAL_KEY_OFFSET;
        } else if (s.equalsIgnoreCase("left")) {
            return KeyEvent.VK_LEFT + SPECIAL_KEY_OFFSET;
        } else if (s.equalsIgnoreCase("right")) {
            return KeyEvent.VK_RIGHT + SPECIAL_KEY_OFFSET;
        }
        return 0;
    }

    private static final Map<String, Integer> keyMap;

    static {
        Map<String, Integer> tempMap = new HashMap<>();
        tempMap.put("shift", KeyEvent.VK_SHIFT);
        tempMap.put("control", KeyEvent.VK_CONTROL);
        tempMap.put("alt", KeyEvent.VK_ALT);
        tempMap.put("meta", KeyEvent.VK_META);
        tempMap.put("tab", KeyEvent.VK_TAB);
        tempMap.put("space", KeyEvent.VK_SPACE);
        tempMap.put("enter", KeyEvent.VK_ENTER);
        tempMap.put("backspace", KeyEvent.VK_BACK_SPACE);
        tempMap.put("escape", KeyEvent.VK_ESCAPE);
        tempMap.put("delete", KeyEvent.VK_DELETE);
        tempMap.put("home", KeyEvent.VK_HOME);
        tempMap.put("end", KeyEvent.VK_END);
        tempMap.put("pageup", KeyEvent.VK_PAGE_UP);
        tempMap.put("pagedown", KeyEvent.VK_PAGE_DOWN);
        tempMap.put("up", KeyEvent.VK_UP + SPECIAL_KEY_OFFSET);
        tempMap.put("down", KeyEvent.VK_DOWN + SPECIAL_KEY_OFFSET);
        tempMap.put("left", KeyEvent.VK_LEFT + SPECIAL_KEY_OFFSET);
        tempMap.put("right", KeyEvent.VK_RIGHT + SPECIAL_KEY_OFFSET);
        // You may want to adjust this for the function keys:
        for (int i = 1; i < 25; i++) {
            if (i < 13) {
                tempMap.put("f" + i, (KeyEvent.VK_F1 + (i - 1)) + SPECIAL_KEY_OFFSET);
            } else {
                tempMap.put("f" + i, (KeyEvent.VK_F13 + (i - 13)) + SPECIAL_KEY_OFFSET);
            }
        }
        keyMap = Collections.unmodifiableMap(tempMap);
    }

    public static int getKeyCode(final String s) {
        if (s == null || s.isEmpty()) {
            throw new IllegalArgumentException("Key string cannot be null or empty");
        }
        if (s.length() == 1) {
            return s.charAt(0);
        }
        Integer keyCode = keyMap.get(s.toLowerCase());
        if (keyCode == null) {
            throw new IllegalArgumentException("Unknown key: " + s);
        }
        return keyCode;
    }



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
     * Converts a string to a unicode character.
     * if the string cannot be converted into a unicode character
     * that means it is a special key such as F1, F2, home, tab, etc.
     * If that's the case return that keys virtual key code but add 1000 to it,
     * so we can differentiate between a unicode character and a virtual key code.
     * @param s The string to convert. Note this is used for keys Such as F12, F11, etc.
     * @return The virtual key code. 0 if the string could not be converted to a unicode character.
     */
    //public static int toKeyCode(String s) {
     //   return getKeyCode(s);
    //}
}