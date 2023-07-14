package macro;

import com.github.kwhat.jnativehook.NativeHookException;
import macro.instruction.InstructionSetContainer;
import macro.scripting.CommandHandler;
import macro.threading.PeripheralHook;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main.java.
 * <p>
 *     The main class for the macro program.
 *     Author: Sick Phukka
 *     Date: 08/07/2023
 * </p>
 */
public class Main  {

    private static final StringBuffer console = new StringBuffer();
	private static InstructionSetContainer instructionSetContainer;
	private static CommandHandler commandHandler;

    public static void main(String[] args) throws NativeHookException {

		instructionSetContainer = new InstructionSetContainer();
		commandHandler = new CommandHandler();

        new MacroFileReader();

        final ExecutorService executor = Executors.newCachedThreadPool();
		final PeripheralHook peripheralHook = new PeripheralHook(executor);

        executor.submit(peripheralHook);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			peripheralHook.unregisterNativeHook();
            executor.shutdown();
        }));
    }

	public static StringBuffer getConsoleBuffer() {
		return console;
	}

	public synchronized static void pushConsoleMessage() {
		System.out.println(console);
		console.setLength(0);
	}

	public static InstructionSetContainer getInstructionSetContainer() {
		return instructionSetContainer;
	}

	public static CommandHandler getCommandHandler() {
		if (commandHandler == null) {
			commandHandler = new CommandHandler();
		}
		return commandHandler;
	}
}