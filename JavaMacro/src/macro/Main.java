package macro;

import com.github.kwhat.jnativehook.NativeHookException;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;
import macro.threading.PeripheralHook;
import macro.threading.ScriptDispatcher;
import macro.threading.Synchronization;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final StringBuffer console = new StringBuffer();

    public static StringBuffer getConsoleBuffer() {
        return console;
    }

    public synchronized static void pushConsoleMessage() {
        System.out.println(console);
        console.setLength(0);
    }

		private static InstructionSetContainer instructionSetContainer;

		public static InstructionSetContainer getInstructionSetContainer() {
			return instructionSetContainer;
		}

    public static void main(String[] args) throws NativeHookException {

	      instructionSetContainer = new InstructionSetContainer();

        Keys.loadKeyMap();
        new MacroFileReader();


        final ExecutorService executor = Executors.newCachedThreadPool();
        final Synchronization synchronization = new Synchronization();

        new PeripheralHook(synchronization, executor);
        new ScriptDispatcher(synchronization, executor);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronization.stop();
            executor.shutdown();
        }));
    }
}