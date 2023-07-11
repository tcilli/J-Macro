package macro;

import com.github.kwhat.jnativehook.NativeHookException;
import macro.threading.PeripheralHook;
import macro.threading.ScriptDispatcher;
import macro.threading.Synchronization;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static StringBuffer console = new StringBuffer();

    public synchronized static void pushConsoleMessage() {
        System.out.println(console.toString());
        console.setLength(0);
    }

    public static void main(String[] args) throws NativeHookException, IOException {
        Keys.loadKeyMap();
        new MacroFileReader();
        ExecutorService executor = Executors.newCachedThreadPool();
        Synchronization synchronization = new Synchronization();
        new PeripheralHook(synchronization, executor);
        new ScriptDispatcher(synchronization, executor);

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }
}