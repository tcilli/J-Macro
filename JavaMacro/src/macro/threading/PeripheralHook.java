package macro.threading;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import macro.Main;

import java.util.concurrent.ExecutorService;

public class PeripheralHook {

    public PeripheralHook(final Synchronization synchronization, final ExecutorService executorService) throws NativeHookException
    {
        GlobalScreen.registerNativeHook();

        executorService.execute(() ->
        {
            GlobalScreen.addNativeKeyListener(new NativeKeyListener()
            {
                @Override
                public void nativeKeyPressed(NativeKeyEvent e) {
                    Main.start = System.currentTimeMillis();
                    synchronization.addKeyPress(e.getKeyCode());
                }
            });
            GlobalScreen.addNativeMouseListener(new NativeMouseListener()
            {
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