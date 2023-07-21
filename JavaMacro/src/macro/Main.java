package macro;

import macro.instruction.ScriptContainer;
import macro.scripting.CommandHandler;
import macro.win32.KbHook;

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
	private static ScriptContainer scriptContainer;
	private static CommandHandler commandHandler;

	public static void main(String[] args) {

		scriptContainer = new ScriptContainer();
		commandHandler = new CommandHandler();

		new MacroFileReader();

		final ExecutorService executor = Executors.newCachedThreadPool();
		final KbHook KBHook = new KbHook(executor);

		executor.submit(KBHook);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			KBHook.unhook();
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

	public static ScriptContainer getScriptContainer() {
		return scriptContainer;
	}

	public static CommandHandler getCommandHandler() {
		if (commandHandler == null) {
			commandHandler = new CommandHandler();
		}
		return commandHandler;
	}
}