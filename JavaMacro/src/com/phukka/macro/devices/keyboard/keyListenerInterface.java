package com.phukka.macro.devices.keyboard;

public interface keyListenerInterface {

    void onKeyPressed(short characterCode);

    void onKeyReleased(short characterCode);
}