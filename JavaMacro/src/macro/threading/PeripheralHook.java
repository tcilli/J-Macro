package macro.threading;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Hook into peripheral devices and listen for events.
 */
public class PeripheralHook {

    /**
     * A Set of currently pressed keys.
     */
    private final Set<Integer> pressedKeys = ConcurrentHashMap.newKeySet();

    /**
     * The PeripheralHook class.
     * Hooks into peripheral devices to listen for key and mouse events.
     *
     * @param synchronization A class for thread safe data sharing between threads.
     * @param executorService An ExecutorService to asynchronously handle the event listeners.
     * @throws NativeHookException If the native hook could not be created.
     */
    public PeripheralHook(final Synchronization synchronization, final ExecutorService executorService) throws NativeHookException
    {
        GlobalScreen.registerNativeHook();

        executorService.execute(() -> {
            GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    if (pressedKeys.add(e.getKeyCode())) {
                        synchronization.addKeyPress(e.getKeyCode());
                    }
                }
                @Override
                public void nativeKeyReleased(NativeKeyEvent e) {
                    pressedKeys.remove(e.getKeyCode());
                    synchronization.addKeyReleased(e.getKeyCode());
                }
            });
            GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
                @Override
                public void nativeMouseClicked(NativeMouseEvent e) {
                    synchronization.addMouseClicked(e.getButton());
                }
            });
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
        }));
    }
}