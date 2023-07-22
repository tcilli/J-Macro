package macro.win32.hooks;

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

/**
 * Hook the keyboard device and takeover the callback
 *  @see <a href="https://learn.microsoft.com/en-us/windows/win32/winmsg/lowlevelkeyboardproc">Keyboard Hook Procedure</a>
 *  @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-setwindowshookexa">SetWindowsHookEx</a>
 *  @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-callnexthookex">CallNextHookEx</a>
 *  @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-unhookwindowshookex">UnhookWindowsHookEx</a>
 *  @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/ns-winuser-kbdllhookstruct">KBDLLHOOKSTRUCT</a>
 *  @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/ns-winuser-msllhookstruct">MSLLHOOKSTRUCT</a>
 *  @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/winuser/ns-winuser-tagkbdllhookstruct">KBDLLHOOKSTRUCT</a>
 *
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

        hook();

        /*
          MSG creates a new MSG structure that will receive
          the details of the next message retrieved by the GetMessage function.

          GetMessage calls the Windows API function GetMessage,
          which retrieves a message from the calling thread's message queue.
          The function waits for the arrival of a message, then places the message details in the MSG structure.

          If GetMessage returns 0, the loop ends. A return value of 0 indicates that a WM_QUIT message was received,
          signifying that the application should terminate.

          TranslateMessage translates virtual-key messages into character messages.
          This is used for keyboard input handling - essentially it takes key-down events and,
          where appropriate, adds corresponding character translation messages to the message queue.

          DispatchMessage dispatches the message to the window procedure that it was intended for, to be handled.
          The message is dispatched based on the window handle and message ID within the MSG structure
         */
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
     * Handles the event of a key press by executing the associated {@link InstructionSet}.
     * If the key pressed is ESCAPE, it clears all locks. If there's no {@link InstructionSet}
     * associated with the key, the method simply returns.
     *
     * <p>The method retrieves the {@link InstructionSet} associated with the provided
     * virtualKeyCode. If the {@link InstructionSet} isn't already locked, it locks it
     * and proceeds to execution. If the {@link InstructionSet} is marked as 'threadless',
     * the method runs it directly; otherwise, it passes the {@link InstructionSet}
     * to an {@link ExecutorService} for execution in a new thread.</p>
     *
     * <p>For the {@link InstructionSet} passed to the {@link ExecutorService},
     * it is executed repeatedly as long as the lock remains true.</p>
     *
     * @param virtualKeyCode The keycode of the pressed key. If the key has been released,
     *                       its unary negation is applied to the keycode.
     */
    public void handleKey(final short virtualKeyCode) {

        if (virtualKeyCode == KeyEvent.VK_ESCAPE) {
            Main.getScriptContainer().clearLocks();
            return;
        }
        InstructionSet instructionSet = Main.getScriptContainer().getInstructionSetMap().getOrDefault(virtualKeyCode, null);

        if (instructionSet == null) {
            return;
        }

        //if the instructionSet lock flag 0x08 is not set
        if ((instructionSet.bFlags & 0x08) == 0) {

            //set the instructionSet lock flag 0x08
            instructionSet.bFlags |= 0x08;

            //if threaded flag 0x01 wasn't set
            if ((instructionSet.bFlags & 0x01) == 0) {

                //execute the instructionSet directly in the current thread 1 time
                instructionSet.execute();

            } else {

                //execute the instructionSet in a new thread
                executorService.execute(() -> {

                    //loop while the lock flag 0x08 is set
                    while((instructionSet.bFlags & 0x08) != 0) {
                        instructionSet.execute();
                    }
                });
            }
        }
    }

    public void hook() {

        /**
          This class implements the HOOKPROC interface, providing a callback
          method that is triggered when a keyboard event is captured by the hook procedure set up
          using the SetWindowsHookEx function.

          @see WinUser.HOOKPROC
         */
        WinUser.HOOKPROC keyboardHook = new WinUser.HOOKPROC() {

            /**
             * This is the callback that is triggered when a keyboard event is captured.
             * It processes the event, updates the state of keys being pressed, and determines
             * the actions to be taken based on the key press and release events.
             *
             * @param nCode    The hook code. If this parameter is less than 0, the hook procedure must pass
             *                 the message to the next hook procedure in the current hook chain. If this parameter
             *                 is greater than or equal to 0, the hook procedure must process the message.<p></p>
             * @param wParam   The identifier of the keyboard message. This parameter can be one of the following messages:
             *                 WM_KEYDOWN, WM_KEYUP, WM_SYSKEYDOWN, or WM_SYSKEYUP.<p></p>
             *
             * @param lParam   A pointer to a <a href="https://learn.microsoft.com/en-us/windows/win32/api/winuser/ns-winuser-kbdllhookstruct">KBDLLHOOKSTRUCT</a> structure containing information about the key event.
             *                 This includes the virtual-key code, scan code, extended-key flag, context code, and transition-state flag. <p></p>
             *
             * @return If nCode is less than zero, the hook procedure must return the value returned by CallNextHookEx.
             *         If the key event was processed, a LRESULT is returned based on whether the key was consumed or not.
             */
             public WinDef.LRESULT callback(int nCode, WinDef.WPARAM wParam, WinUser.KBDLLHOOKSTRUCT lParam) {

                if (nCode >= 0 && lParam.vkCode > 0) {

                    // create a byte array for storing the state of keyboard
                    byte[] keyboardState = new byte[256];

                    // store the state of the keyboard in the byte array keyboardState
                    User32.INSTANCE.GetKeyboardState(keyboardState);

                    //this basically allows shift + another key to be combined
                    if ((User32.INSTANCE.GetAsyncKeyState(KeyEvent.VK_SHIFT) & 0x8000) != 0) {
                        keyboardState[KeyEvent.VK_SHIFT] |= 0x80;
                    } else {
                        keyboardState[KeyEvent.VK_SHIFT] &= ~0x80;
                    }

                    //check if caps lock is toggled on. If it is, set the low bit to 1
                    //this allows us to ignore the state of the caps lock. Until I implement
                    //shift + key, instead of lowercase or uppercase keys, this is the best.
                    //A script can be triggered by "a" or "A" and each of these are different
                    //scripts. This is not ideal, but it is the best solution for now
                    //I may change this to ignore the case of the key pressed and denote a "A" as shift+a for
                    //the command line. This will allow for a more consistent experience for the user
                    //as the user may have caps lock on, but still want to trigger lowercase commands
                    //such as "a" instead of being forced to ("Shift" + "a") to get a lower case "a"
                    if ((User32.INSTANCE.GetAsyncKeyState(KeyEvent.VK_CAPS_LOCK) & 0x01) != 0) {
                        keyboardState[KeyEvent.VK_CAPS_LOCK] |= 0x01;
                    } else {
                        keyboardState[KeyEvent.VK_CAPS_LOCK] &= ~0x01;
                    }

                    /*
                     this will determine the character code of the key pressed
                     if the key is a special key such as the arrow keys, function keys etc
                     then the character code will be the virtualKeycode + 1000
                     Otherwise it will be the unicode character code for the key pressed
                     if the unicode character doesn't exist then the character code is set
                     to the virtual keycode of the key pressed
                    */
                    short characterCode;

                    if (lParam.vkCode == KeyEvent.VK_LEFT ||
                        lParam.vkCode == KeyEvent.VK_RIGHT ||
                        lParam.vkCode == KeyEvent.VK_UP ||
                        lParam.vkCode == KeyEvent.VK_DOWN ||
                        lParam.vkCode >= KeyEvent.VK_F1  && lParam.vkCode <= KeyEvent.VK_F12 ||
                        lParam.vkCode >= KeyEvent.VK_F13 && lParam.vkCode <= KeyEvent.VK_F24) {
                        // For special keys, use the vkCode directly + a magic number offset.
                        // this solves a conflict where a character unicode matches a virtual key code
                        // and the character is sent instead of the key code
                        characterCode = (short) (lParam.vkCode + Keys.SPECIAL_KEY_OFFSET);
                    } else {
                        char[] buffer = new char[2];
                        int toUnicodeExResult = User32.INSTANCE.ToUnicodeEx(lParam.vkCode, lParam.scanCode, keyboardState, buffer, 2, 0, null);
                        // For normal keys, use the translated character if possible.
                        characterCode = (short) (toUnicodeExResult > 0 ? buffer[0] : lParam.vkCode);
                    }

                    // Key Down MSG
                    if (wParam.intValue() == WinUser.WM_KEYDOWN || wParam.intValue() == WinUser.WM_SYSKEYDOWN) {

                        /*
                          Only add the key to the pressedKeys set if it is not already pressed.
                          This prevents the key from being pressed multiple times.
                          we use the virtualKey code and not the character code because
                          we need to remove these keys from the pressedKeys set when the key is released.
                          and the character code can be different on release
                          Example: pressing shift + 1 = !, but releasing shift + 1 release = 1 released not ! released
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

                        //Remove the key from the pressedKeys set when the key is released.
                        pressedKeys.remove(lParam.vkCode);

                        //convert the characterCode to its unary negation value
                        characterCode = (short) -characterCode;
                        handleKey(characterCode);

                        // If the key is a consumable key, return 1 to prevent the key from being sent to the application
                        if (Keys.containsConsumableKey(characterCode)) {
                            return new WinDef.LRESULT(1);
                        }
                    }
                }
                //passes the message to the next hook procedure in the current hook chain
                return User32.INSTANCE.CallNextHookEx(null, nCode, wParam, new WinDef.LPARAM(Pointer.nativeValue(lParam.getPointer())));
            }
        };

        /*
          Sets a global low-level keyboard hook, enabling the interception and potential modification
          of keyboard events across all threads of the current process. This uses the keyboardHook
          defined above as the procedure to be triggered on each keyboard event.
          WH_KEYBOARD_LL (13) Installs a hook procedure that monitors low-level keyboard input events
         */
        this.hHookKb = User32.INSTANCE.SetWindowsHookEx(WinUser.WH_KEYBOARD_LL, keyboardHook, hModule, 0);

        if (hHookKb == null) {
            int errorCode = Kernel32.INSTANCE.GetLastError();
            Main.getConsoleBuffer().append("Failed to hook. Error code: ").append(errorCode);
            Main.pushConsoleMessage();
            System.exit(1);
        } else {
            Main.getConsoleBuffer().append("Keyboard successfully hooked");
            Main.pushConsoleMessage();
        }
    }

    /**
     * Unhook from keyboard device
     * The hook procedure can be in the state of being called
     * by another thread even after UnhookWindowsHookEx returns.
     * If the hook procedure is not being called concurrently,
     * the hook procedure is removed immediately before UnhookWindowsHookEx returns.
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