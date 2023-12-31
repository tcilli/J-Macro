package com.phukka.macro.util;

import java.util.HashMap;
import java.util.Map;

public class ConsumableKeyMap {

    /**
     * If a user has added the flag "consume" in the com.phukka.macro file,
     * Then the triggering key for that com.phukka.macro will be added to this list.
     * This list is checked before sending a key event to the OS.
     * If the key is in this list, then the key event will not be sent.
     */
    private static final Map<Short, Short> map = new HashMap<>();

    public static void addKey(final short keyCode) {
        map.put(keyCode, keyCode);
    }
    public static void addKey(final int keyCode) {
       addKey((short)keyCode);
    }

    public static void clear() {
        map.clear();
    }

    public static boolean containsKey(final short keyCode) {
        return map.containsKey(keyCode);
    }

    public static void removeKey(final short keyCode) {
        map.remove(keyCode);
    }
    public static void removeKey(final int keyCode) {
       removeKey((short)keyCode);
    }
}
