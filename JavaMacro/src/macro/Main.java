package macro;

import com.github.kwhat.jnativehook.NativeHookException;
import macro.io.MacroFileReader;
import macro.threading.PeripheralHook;
import macro.threading.ScriptDispatcher;
import macro.threading.ScriptExecutor;
import macro.threading.Synchronization;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static StringBuilder console = new StringBuilder();

    public static void pushConsoleMessage() {
        System.out.println(console.toString());
        console.setLength(0);
    }

    public static void main(String[] args) throws NativeHookException, IOException {
        new MacroFileReader();
        ExecutorService executor = Executors.newCachedThreadPool();
        Synchronization synchronization = new Synchronization();
        PeripheralHook peripheral = new PeripheralHook(synchronization, executor);
        ScriptExecutor scriptExecutor = new ScriptExecutor(synchronization, executor);
        ScriptDispatcher scriptDispatcher = new ScriptDispatcher(synchronization, executor, scriptExecutor);
    }
}