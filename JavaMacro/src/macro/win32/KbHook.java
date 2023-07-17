package macro.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import macro.Main;
import macro.instruction.InstructionSet;
import macro.Keys;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static macro.Keys.SPECIAL_KEY_OFFSET;

/**
 * Hook the keyboard device and takeover the callback
 *  @see <a href="https://learn.microsoft.com/en-us/windows/win32/winmsg/lowlevelkeyboardproc">Keyboard Hook Procedure</a>
 */
public class KbHook implements Runnable {

    private final Set<Integer> pressedKeys = ConcurrentHashMap.newKeySet();
    private final ExecutorService executorService;
    private final WinDef.HMODULE hModule;
    private WinUser.HHOOK hHookKb;

    /**
     * The KeyboardHook class.
     * Hooks into keyboard device to intercept the callback
     */
    public KbHook(final ExecutorService executorService) {
        this.executorService = executorService;
        this.hModule = Kernel32.INSTANCE.GetModuleHandle(null);
    }

    @Override
    public void run() {

        WinUser.HOOKPROC keyboardHook = new WinUser.HOOKPROC() {

            /**
             * @param nCode 0 if the hook procedure must process the message. -1 if the hook procedure should pass the message to the next hook procedure in the current hook chain.
             * @param wParam The identifier of the keyboard message. This parameter can be one of the following messages: WM_KEYDOWN, WM_KEYUP, WM_SYSKEYDOWN, or WM_SYSKEYUP.
             * @param lParam A pointer to a KBDLLHOOKSTRUCT structure.
             * @return If nCode is less than zero, the hook procedure must return the value returned by CallNextHookEx.
             */
            public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT lParam) {

                if (nCode >= 0 && lParam.vkCode > 0) {

                    // create a byte array for storing the state of keyboard
                    byte[] keyboardState = new byte[256];

                    // store the state of the keyboard in the byte array keyboardState
                    User32.INSTANCE.GetKeyboardState(keyboardState);

                    //0000 0000 0000 0000 check if the first bit is not a 0 (GetAsyncKeyState returns a 16 bit short)
                    if ((User32.INSTANCE.GetAsyncKeyState(KeyEvent.VK_SHIFT) & 0x8000) != 0) {
                        //1000 0000 (8 bit) setting high bit to indicate key is down
                        keyboardState[KeyEvent.VK_SHIFT] |= 0x80;
                    } else {
                        //0000 0000 (8 bit) clear high bit to indicate key is up
                        keyboardState[KeyEvent.VK_SHIFT] &= ~0x80;
                    }

                    /*
                    * this will determine the character code of the key pressed
                    * if the key is a special key such as the arrow keys, function keys etc
                    * then the character code will be the virtualKeycode + 1000
                    * Otherwise it will be the unicode character code for the key pressed
                    * if the unicode character doesn't exist then the character code is set
                    * to the virtual keycode of the key pressed
                    */
                    int characterCode;

                    if (lParam.vkCode == KeyEvent.VK_LEFT || lParam.vkCode == KeyEvent.VK_RIGHT || lParam.vkCode == KeyEvent.VK_UP || lParam.vkCode == KeyEvent.VK_DOWN ||
                        (lParam.vkCode >= KeyEvent.VK_F1 && lParam.vkCode <= KeyEvent.VK_F24)) {
                        // For special keys, use the vkCode directly + a magic number offset.
                        // this solves a conflict where a character unicode matches a virtual key code
                        // and the character is sent instead of the key code
                        characterCode = lParam.vkCode + SPECIAL_KEY_OFFSET;
                    } else {
                        char[] buffer = new char[2];
                        int toUnicodeExResult = User32.INSTANCE.ToUnicodeEx(lParam.vkCode, lParam.scanCode, keyboardState, buffer, 2, 0, null);
                        // For normal keys, use the translated character if possible.
                        characterCode = toUnicodeExResult > 0 ? buffer[0] : lParam.vkCode;
                    }

                    // Key Down MSG
                    if (wParam.intValue() == WinUser.WM_KEYDOWN || wParam.intValue() == WinUser.WM_SYSKEYDOWN) {

                        /*
                         * Only add the key to the pressedKeys set if it is not already pressed.
                         * This prevents the key from being pressed multiple times.
                         * we use the virtualKey code and not the character code because
                         * we need to remove these keys from the pressedKeys set when the key is released.
                         * and the character code can be different on release
                         * Example: pressing shift + 1 = !, but releasing shift + 1 release = 1 released not ! released
                         */
                        if (pressedKeys.add(lParam.vkCode)) {
                            handleKey(characterCode);

                            // If the key is a consumable key, return 1 to prevent the key from being sent to the application
                            if (Keys.containsConsumableKey(characterCode)) {
                                return new WinDef.LRESULT(1);
                            }
                        }

                    // Key Up MSG
                    } else if (wParam.intValue() == WinUser.WM_KEYUP || wParam.intValue() == WinUser.WM_SYSKEYUP) {

                        /*
                         * Remove the key from the pressedKeys set when the key is released.
                         */
                        pressedKeys.remove(lParam.vkCode);

                        handleKey(characterCode * -1);

                        // If the key is a consumable key, return 1 to prevent the key from being sent to the application
                        if (Keys.containsConsumableKey(characterCode * -1)) {
                            return new WinDef.LRESULT(1);
                        }
                    }
                }
                //passes the message to the next hook procedure in the current hook chain
                return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
            }
        };
        this.hHookKb = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hModule, 0);

        WinUser.MSG msg = new WinUser.MSG();
        while (true) {
            if (User32.INSTANCE.GetMessage(msg, null, 0, 0) != 0) {
                User32.INSTANCE.TranslateMessage(msg);
                User32.INSTANCE.DispatchMessage(msg);
            } else {
                break;
            }
        }
    }

    /**
     * Handle key press by executing the {@link InstructionSet} associated with the keycode.
     * If the {@link InstructionSet} is flagged as {@link InstructionSet#threadless},
     * execute without creating a new {@link Thread}. Other-wise pass it to {@link ExecutorService}.
     * @param virtualKeyCode The keycode, a released key has a unary negation applied to the keycode.
     */
    public void handleKey(final int virtualKeyCode) {

        //If escape key is pressed, clear all locks
        if (virtualKeyCode == KeyEvent.VK_ESCAPE) {
            Main.getScriptContainer().clearLocks();
            return;
        }
        InstructionSet instructionSet = Main.getScriptContainer().getInstructionSetMap().getOrDefault(virtualKeyCode, null);

        if (instructionSet == null) {
            return;
        }
        if (!instructionSet.lock.get()) {
            instructionSet.lock.set(true);
            if (instructionSet.threadless) {
                instructionSet.run();
            } else {
                executorService.execute(() -> { while (instructionSet.lock.get()) { instructionSet.run(); }});
            }
        }
    }

    /**
     * Unhook from keyboard device
     */
    public void unhook() {
        if (User32.INSTANCE.UnhookWindowsHookEx(hHookKb)) {
            Main.getConsoleBuffer().append("Keyboard hook successfully unhooked");
            Main.pushConsoleMessage();
        } else {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            Main.getConsoleBuffer().append("Failed to unhook. Error code: ").append(errorCode);
            Main.pushConsoleMessage();
        }
    }
}