package macro;

import macro.instruction.ScriptContainer;
import macro.scripting.CommandHandler;
import macro.util.MacroFileReader;
import macro.win32.callbacks.KeyboardCallback;
import macro.win32.callbacks.MouseCallback;
import macro.win32.hooks.KeyboardHook;
import macro.win32.hooks.MouseHook;

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
	private static ExecutorService executor;

	public static void main(String[] args) {

		scriptContainer = new ScriptContainer();
		commandHandler = new CommandHandler();

		new MacroFileReader();

		executor = Executors.newCachedThreadPool();

		final KeyboardCallback KeyboardCallback = new KeyboardCallback();
		final MouseCallback mouseCallback = new MouseCallback();

		final KeyboardHook KeyboardHook = new KeyboardHook(KeyboardCallback);
		final MouseHook mouseHook = new MouseHook(mouseCallback);

		executor.submit(KeyboardHook);
		executor.submit(mouseHook);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			KeyboardHook.unhook();
			mouseHook.unhook();
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

	public static ExecutorService getExecutor() {
		return executor;
	}
}