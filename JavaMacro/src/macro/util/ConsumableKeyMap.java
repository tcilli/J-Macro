package macro.util;

import java.util.HashMap;
import java.util.Map;

public class ConsumableKeyMap {

    /**
     * If a user has added the flag "consume" in the macro file,
     * Then the triggering key for that macro will be added to this list.
     * This list is checked before sending a key event to the OS.
     * If the key is in this list, then the key event will not be sent.
     */
    private static final Map<Short, Short> map = new HashMap<>();

    public static void addKey(final short keyCode) {
        map.put(keyCode, keyCode);
    }

    public static void clear() {
        map.clear();
    }

    public static boolean containsKey(final short keyCode) {
        return map.containsKey(keyCode);
    }

}
