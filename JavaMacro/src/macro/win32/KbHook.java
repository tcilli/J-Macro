package macro.win32;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import macro.Keys;
import macro.Main;
import macro.instruction.InstructionSet;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

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

            public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT lParam) {

                if (nCode >= 0 && lParam.scanCode > 0) {
                    if (wParam.intValue() == WinUser.WM_KEYDOWN || wParam.intValue() == WinUser.WM_SYSKEYDOWN) {
                        if (pressedKeys.add(lParam.scanCode)) {
                            handleKey(lParam.scanCode);
                            if (Keys.containsConsumableKey(lParam.scanCode)) {
                                return new WinDef.LRESULT(1);
                            }
                        }
                    } else if (wParam.intValue() == WinUser.WM_KEYUP || wParam.intValue() == WinUser.WM_SYSKEYUP) {
                        pressedKeys.remove(lParam.scanCode);
                        handleKey(lParam.scanCode * -1);
                        if (Keys.containsConsumableKey(lParam.scanCode * -1)) {
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
     * @param scanCode The keycode of the {@link NativeKeyEvent}, a released key has a unary negation applied to the keycode.
     */
    public void handleKey(final int scanCode) {

        //If escape key is pressed, clear all locks
        if (scanCode == NativeKeyEvent.VC_ESCAPE) {
            Main.getScriptContainer().clearLocks();
            return;
        }
        InstructionSet instructionSet = Main.getScriptContainer().getInstructionSetMap().getOrDefault(scanCode, null);

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