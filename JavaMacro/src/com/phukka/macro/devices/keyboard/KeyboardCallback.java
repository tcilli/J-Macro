package com.phukka.macro.devices.keyboard;

import com.phukka.macro.scripting.Scripts;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import com.phukka.macro.Main;
import com.phukka.macro.instruction.InstructionSet;
import com.phukka.macro.util.ConsumableKeyMap;
import com.phukka.macro.util.KeyMapper;

import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class KeyboardCallback {

    public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT lParam) {

        if (nCode >= 0 && lParam.vkCode > 0) {

            byte[] keyboardState = new byte[256];
            User32.INSTANCE.GetKeyboardState(keyboardState);

            if ((User32.INSTANCE.GetAsyncKeyState(KeyEvent.VK_SHIFT) & 0x8000) != 0) {

                keyboardState[KeyEvent.VK_SHIFT] |= 0x80;
            }
            else {
                keyboardState[KeyEvent.VK_SHIFT] &= ~0x80;
            }
            if ((User32.INSTANCE.GetAsyncKeyState(KeyEvent.VK_CAPS_LOCK) & 0x01) != 0) {

                keyboardState[KeyEvent.VK_CAPS_LOCK] |= 0x01;
            }
            else {
                keyboardState[KeyEvent.VK_CAPS_LOCK] &= ~0x01;
            }

            short characterCode;

            if (lParam.vkCode == KeyEvent.VK_LEFT ||
                lParam.vkCode == KeyEvent.VK_RIGHT ||
                lParam.vkCode == KeyEvent.VK_UP ||
                lParam.vkCode == KeyEvent.VK_DOWN ||
                lParam.vkCode >= KeyEvent.VK_F1  && lParam.vkCode <= KeyEvent.VK_F12 ||
                lParam.vkCode >= KeyEvent.VK_F13 && lParam.vkCode <= KeyEvent.VK_F24) {

                characterCode = (short) (lParam.vkCode + KeyMapper.SPECIAL_KEY_OFFSET);
            }
            else {

                char[] buffer = new char[2];
                int toUnicodeExResult = User32.INSTANCE.ToUnicodeEx(lParam.vkCode, lParam.scanCode, keyboardState, buffer, 2, 0, null);

                characterCode = (short) (toUnicodeExResult > 0 ? buffer[0] : lParam.vkCode);
            }

            if (wParam.intValue() == WinUser.WM_KEYDOWN || wParam.intValue() == WinUser.WM_SYSKEYDOWN) {


                if (pressedKeys.add(lParam.vkCode)) {

                    KeyListener.notifyListenersKeyPressed(characterCode);

                    Main.getScriptContainer().handleKey(characterCode);

                    if (ConsumableKeyMap.containsKey(characterCode)) {

                        InstructionSet instructionSet = Main.getScriptContainer().getInstructionSetMap().getOrDefault(characterCode, null);

                        if (!Main.getCommandHandler().failedWindowCheck(instructionSet)) {
                            return new WinDef.LRESULT(1);
                        }
                    }
                }
            }
            else if (wParam.intValue() == WinUser.WM_KEYUP || wParam.intValue() == WinUser.WM_SYSKEYUP) {

                KeyListener.notifyListenersKeyReleased(characterCode);

                pressedKeys.remove(lParam.vkCode);


                characterCode = (short) -characterCode;

                Main.getScriptContainer().handleKey(characterCode);

                if (ConsumableKeyMap.containsKey(characterCode)) {

                    InstructionSet instructionSet = Main.getScriptContainer().getInstructionSetMap().getOrDefault(characterCode, null);

                    if (!Main.getCommandHandler().failedWindowCheck(instructionSet)) {
                        return new WinDef.LRESULT(1);
                    }
                }
            }
        }
        return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
    }

    private final Set<Integer> pressedKeys = ConcurrentHashMap.newKeySet();
}
