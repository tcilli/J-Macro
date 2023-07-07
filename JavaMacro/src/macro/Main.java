package macro;

import com.github.kwhat.jnativehook.NativeHookException;
import macro.io.KeyMap;
import macro.io.MacroFileReader;
import macro.threading.PeripheralHook;
import macro.threading.InputThread;
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

    // Declare the ScriptExecutor as a field
    private static ScriptExecutor scriptExecutor;

    // Add a getter method for the ScriptExecutor
    public static ScriptExecutor getScriptExecutor() {
        return scriptExecutor;
    }


    private static KeyMap map;

    public static KeyMap getKeyMap() {
        return map;
    }

    public static void main(String[] args) throws NativeHookException, IOException {
        map = new KeyMap();
        new MacroFileReader();
        Synchronization synchronization = new Synchronization();
        PeripheralHook peripheral = new PeripheralHook(synchronization);
        InputThread inputThread = new InputThread(synchronization);
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(peripheral);
        executor.execute(inputThread);
        scriptExecutor = new ScriptExecutor(synchronization);








    }
}