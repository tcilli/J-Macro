package macro.jnative;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import macro.Synchronization;

public class PeripheralHook implements Runnable {

    public PeripheralHook(Synchronization synchronization) throws NativeHookException
    {
        GlobalScreen.registerNativeHook();
        GlobalScreen.addNativeKeyListener(new NativeKeyListener() {
            @Override
            public void nativeKeyPressed(NativeKeyEvent e) {
                synchronization.addKeyPress(e.getKeyCode());
            }
        });
        GlobalScreen.addNativeMouseListener(new NativeMouseListener() {
            @Override
            public void nativeMouseClicked(NativeMouseEvent e) {
                synchronization.addMouseClicked(e.getButton());
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e) {
                e.printStackTrace();
            }
        }));
    }
    @Override
    public void run() {
    }
}