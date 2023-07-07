package macro.io;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyMap {

    public static HashMap<String, Integer> keymap = new HashMap<>();

    public KeyMap() {
        loadKeyMap();
    }

    public int getCode(char c) {
        String s = String.valueOf(c);
        return keymap.getOrDefault(s, 0);
    }

    public int getCode(String c) {
        return keymap.getOrDefault(c, 0);
    }

    public static void loadKeyMap() {
        keymap.put("a", KeyEvent.VK_A);
        keymap.put("A", KeyEvent.VK_A);
        keymap.put("b", KeyEvent.VK_B);
        keymap.put("B", KeyEvent.VK_B);
        keymap.put("c", KeyEvent.VK_C);
        keymap.put("C", KeyEvent.VK_C);
        keymap.put("d", KeyEvent.VK_D);
        keymap.put("D", KeyEvent.VK_D);
        keymap.put("e", KeyEvent.VK_E);
        keymap.put("E", KeyEvent.VK_E);
        keymap.put("f", KeyEvent.VK_F);
        keymap.put("F", KeyEvent.VK_F);
        keymap.put("g", KeyEvent.VK_G);
        keymap.put("G", KeyEvent.VK_G);
        keymap.put("h", KeyEvent.VK_H);
        keymap.put("H", KeyEvent.VK_H);
        keymap.put("i", KeyEvent.VK_I);
        keymap.put("I", KeyEvent.VK_I);
        keymap.put("j", KeyEvent.VK_J);
        keymap.put("J", KeyEvent.VK_J);
        keymap.put("k", KeyEvent.VK_K);
        keymap.put("K", KeyEvent.VK_K);
        keymap.put("l", KeyEvent.VK_L);
        keymap.put("L", KeyEvent.VK_L);
        keymap.put("m", KeyEvent.VK_M);
        keymap.put("M", KeyEvent.VK_M);
        keymap.put("n", KeyEvent.VK_N);
        keymap.put("N", KeyEvent.VK_N);
        keymap.put("o", KeyEvent.VK_O);
        keymap.put("O", KeyEvent.VK_O);
        keymap.put("p", KeyEvent.VK_P);
        keymap.put("P", KeyEvent.VK_P);
        keymap.put("q", KeyEvent.VK_Q);
        keymap.put("Q", KeyEvent.VK_Q);
        keymap.put("r", KeyEvent.VK_R);
        keymap.put("R", KeyEvent.VK_R);
        keymap.put("s", KeyEvent.VK_S);
        keymap.put("S", KeyEvent.VK_S);
        keymap.put("t", KeyEvent.VK_T);
        keymap.put("T", KeyEvent.VK_T);
        keymap.put("u", KeyEvent.VK_U);
        keymap.put("U", KeyEvent.VK_U);
        keymap.put("v", KeyEvent.VK_V);
        keymap.put("V", KeyEvent.VK_V);
        keymap.put("w", KeyEvent.VK_W);
        keymap.put("W", KeyEvent.VK_W);
        keymap.put("x", KeyEvent.VK_X);
        keymap.put("X", KeyEvent.VK_X);
        keymap.put("y", KeyEvent.VK_Y);
        keymap.put("Y", KeyEvent.VK_Y);
        keymap.put("z", KeyEvent.VK_Z);
        keymap.put("Z", KeyEvent.VK_Z);
        keymap.put("shift", KeyEvent.VK_SHIFT);
        keymap.put("SHIFT", KeyEvent.VK_SHIFT);
        keymap.put("alt", KeyEvent.VK_ALT);
        keymap.put("ALT", KeyEvent.VK_ALT);
        keymap.put("f1", KeyEvent.VK_F1);
        keymap.put("F1", KeyEvent.VK_F1);
        keymap.put("f2", KeyEvent.VK_F2);
        keymap.put("F2", KeyEvent.VK_F2);
        keymap.put("f3", KeyEvent.VK_F3);
        keymap.put("F3", KeyEvent.VK_F3);
        keymap.put("f4", KeyEvent.VK_F4);
        keymap.put("F4", KeyEvent.VK_F4);
        keymap.put("f5", KeyEvent.VK_F5);
        keymap.put("F5", KeyEvent.VK_F5);
        keymap.put("f6", KeyEvent.VK_F6);
        keymap.put("F6", KeyEvent.VK_F6);
        keymap.put("f7", KeyEvent.VK_F7);
        keymap.put("F7", KeyEvent.VK_F7);
        keymap.put("f8", KeyEvent.VK_F8);
        keymap.put("F8", KeyEvent.VK_F8);
        keymap.put("f9", KeyEvent.VK_F9);
        keymap.put("F9", KeyEvent.VK_F9);
        keymap.put("f10", KeyEvent.VK_F10);
        keymap.put("F10", KeyEvent.VK_F10);
        keymap.put("f11", KeyEvent.VK_F11);
        keymap.put("F11", KeyEvent.VK_F11);
        keymap.put("f12", KeyEvent.VK_F12);
        keymap.put("F12", KeyEvent.VK_F12);
        keymap.put("leftclick", 1);
        keymap.put("click", 1);
        keymap.put("rightclick", 2);
        keymap.put("spacebar", KeyEvent.VK_SPACE);
        keymap.put("space", KeyEvent.VK_SPACE);
        keymap.put(" ", KeyEvent.VK_SPACE);
        keymap.put("-", KeyEvent.VK_MINUS);
        keymap.put("=", KeyEvent.VK_EQUALS);
        keymap.put("~", KeyEvent.VK_SHIFT | KeyEvent.VK_BACK_QUOTE);
        keymap.put("!", KeyEvent.VK_EXCLAMATION_MARK);
        keymap.put("@", KeyEvent.VK_AT);
        keymap.put("#", KeyEvent.VK_NUMBER_SIGN);
        keymap.put("$", KeyEvent.VK_DOLLAR);
        keymap.put("%", KeyEvent.VK_SHIFT | KeyEvent.VK_5);
        keymap.put("^", KeyEvent.VK_CIRCUMFLEX);
        keymap.put("&", KeyEvent.VK_AMPERSAND);
        keymap.put("*", KeyEvent.VK_ASTERISK);
        keymap.put("(", KeyEvent.VK_LEFT_PARENTHESIS);
        keymap.put(")", KeyEvent.VK_RIGHT_PARENTHESIS);
        keymap.put("_", KeyEvent.VK_UNDERSCORE);
        keymap.put("+", KeyEvent.VK_PLUS);
        keymap.put("\t", KeyEvent.VK_TAB);
        keymap.put("\n", KeyEvent.VK_ENTER);
        keymap.put("[", KeyEvent.VK_OPEN_BRACKET);
        keymap.put("]", KeyEvent.VK_CLOSE_BRACKET);
        keymap.put("\\", KeyEvent.VK_BACK_SLASH);
        keymap.put("{", KeyEvent.VK_SHIFT | KeyEvent.VK_OPEN_BRACKET);
        keymap.put("}", KeyEvent.VK_SHIFT | KeyEvent.VK_CLOSE_BRACKET);
        keymap.put("|", KeyEvent.VK_SHIFT | KeyEvent.VK_BACK_SLASH);
        keymap.put(";", KeyEvent.VK_SEMICOLON);
        keymap.put(":", KeyEvent.VK_COLON);
        keymap.put("'", KeyEvent.VK_QUOTE);
        keymap.put("\"", KeyEvent.VK_QUOTEDBL);
        keymap.put(",", KeyEvent.VK_COMMA);
        keymap.put("<", KeyEvent.VK_LESS);
        keymap.put(".", KeyEvent.VK_PERIOD);
        keymap.put(">", KeyEvent.VK_GREATER);
        keymap.put("/", KeyEvent.VK_SLASH);
        keymap.put("?", KeyEvent.VK_SHIFT | KeyEvent.VK_SLASH);
    }
}
