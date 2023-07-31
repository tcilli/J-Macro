package com.phukka.macro.devices.keyboard;

import java.util.HashSet;
import java.util.Set;

public class KeyListener {

    public static keyListenerInterface newKeyListener() {
        keyListenerInterface listener = new KeyListenerImpl();
        addListener(listener);
        System.out.println(listeners.size() +" total listeners active.");
        return listener;
    }

    public static class KeyListenerImpl implements keyListenerInterface {
        private int pressed = 0;
        private int released = 0;

        @Override
        public void onKeyPressed(short characterCode) {
            pressed = characterCode;
        }

        @Override
        public void onKeyReleased(short characterCode) {
            released = characterCode;
        }

        public int getPressed() {
            int value = pressed;
            pressed = 0;
            return value;
        }

        public int getReleased() {
            int value = released;
            released = 0;
            return value;
        }
    }

    private static final Set<keyListenerInterface> listeners = new HashSet<>();

    public static void addListener(keyListenerInterface listener) {
        listeners.add(listener);
    }

    public static void removeListener(keyListenerInterface listener) {
        listeners.remove(listener);
    }

    public static void notifyListenersKeyPressed(short characterCode) {
        for (keyListenerInterface listener : listeners) {
            listener.onKeyPressed(characterCode);
        }
    }

    public static void notifyListenersKeyReleased(short characterCode) {
        for (keyListenerInterface listener : listeners) {
            listener.onKeyReleased(characterCode);
        }
    }
}

