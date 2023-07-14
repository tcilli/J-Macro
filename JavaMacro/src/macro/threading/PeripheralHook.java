package macro.threading;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import macro.Main;
import macro.instruction.InstructionSet;

import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Hook into peripheral devices and listen for events.
 */
public class PeripheralHook implements Runnable {

    /**
     * A Set of currently pressed keys.
     */
    private final Set<Integer> pressedKeys = ConcurrentHashMap.newKeySet();
    private final ExecutorService executorService;

    /**
     * The PeripheralHook class.
     * Hooks into peripheral devices to listen for key and mouse events.
     *
     * @param executorService An ExecutorService to asynchronously handle the event listeners.
     * @throws NativeHookException If the native hook could not be created.
     */
    public PeripheralHook(final ExecutorService executorService) throws NativeHookException
    {
        this.executorService = executorService;
        GlobalScreen.registerNativeHook();
    }

    @Override
    public void run() {
        executorService.execute(() -> {
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {

                    if (pressedKeys.add(e.getKeyCode())) {
                        handleKey(e.getKeyCode());
                    }
                }
                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                    pressedKeys.remove(e.getKeyCode());
                    handleKey(e.getKeyCode() * -1);
                }
            });
        });
    }

    /**
     * Handle key press by executing the {@link InstructionSet} associated with the keycode.
     * If the {@link InstructionSet} is flagged as {@link InstructionSet#threadless},
     * execute without creating a new {@link Thread}. Other-wise pass it to {@link ExecutorService}.
     * @param keycode The keycode of the {@link NativeKeyEvent}, a released key has a unary negation applied to the keycode.
     */
    public void handleKey(final int keycode) {

        if (keycode == KeyEvent.VK_ESCAPE) {
            Main.getInstructionSetContainer().clearLocks();
            return;
        }
        InstructionSet instructionSet = Main.getInstructionSetContainer().getInstructionSet(keycode);

        if (instructionSet != null) {
            if (!instructionSet.lock.get()) {
                instructionSet.lock.set(true);
                if (instructionSet.threadless) {
                    ScriptExecutor.executeWithoutThread(instructionSet);
                } else {
                    ScriptExecutor.executeWithThread(instructionSet, executorService);
                }
            }
        }
    }

    public void unregisterNativeHook() {
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            throw new RuntimeException(e);
        }
    }
}