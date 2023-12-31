package com.phukka.macro;

import com.phukka.macro.devices.screen.ImageRepository;
import com.phukka.macro.devices.screen.Screen;
import com.phukka.macro.instruction.InstructionSetContainer;
import com.phukka.macro.command.CommandHandler;
import com.phukka.macro.scripting.Scripts;
import com.phukka.macro.scripting.runescape.abilities.Abilties;
import com.phukka.macro.scripting.runescape.grandexchange.GrandExchange;
import com.phukka.macro.util.ConsoleReader;
import com.phukka.macro.util.MacroFileReader;
import com.phukka.macro.devices.keyboard.KeyboardCallback;
import com.phukka.macro.devices.mouse.MouseCallback;
import com.phukka.macro.devices.keyboard.KeyboardHook;
import com.phukka.macro.devices.mouse.MouseHook;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import java.awt.Robot;
import java.awt.AWTException;

/**
 * Main.java.
 * <p>
 *     The main class for the com.phukka.macro program.
 *     Author: Sick Phukka
 *     Date: 08/07/2023
 * </p>
 */
public class Main  {

	private static final StringBuffer console = new StringBuffer();
	private static final Robot robot;
	private static InstructionSetContainer instructionSetContainer;
	private static CommandHandler commandHandler;
	private static ExecutorService executor;

	private static final Screen SCREEN = new Screen();
	private static final ImageRepository imageRepository = new ImageRepository();
	private static final Scripts scripts = new Scripts();
	private static final KeyboardCallback KeyboardCallback = new KeyboardCallback();
	private static final Abilties abilities = new Abilties();
	private static ConsoleReader consoleReader = new ConsoleReader();

	public static void main(String[] args) {

		GrandExchange.downloadGEPrices();
		GrandExchange.downloadGETradeVolumes();

		instructionSetContainer = new InstructionSetContainer();
		commandHandler = new CommandHandler();
		//abilities.simulate_ability(1);
		new MacroFileReader();

		executor = Executors.newCachedThreadPool();

		final MouseCallback mouseCallback = new MouseCallback();

		final KeyboardHook KeyboardHook = new KeyboardHook(KeyboardCallback);
		final MouseHook mouseHook = new MouseHook(mouseCallback);

		executor.submit(KeyboardHook);
		executor.submit(mouseHook);

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			KeyboardHook.unhook();
			mouseHook.unhook();
			executor.shutdown();
			consoleReader.shutdown();
			Main.getConsoleBuffer().append("Shutdown hook triggered.");
			Main.pushConsoleMessageAlways();
		}));

		scripts.run("Runescape");

		consoleReader.setup();



	}

	public static StringBuffer getConsoleBuffer() {
		return console;
	}


	public static boolean debug = false;

	public synchronized static void pushConsoleMessage() {
		if (debug) {
			System.out.println(console);
		}
		console.setLength(0);
	}

	public synchronized static void pushConsoleMessageAlways() {
		System.out.println(console);
		console.setLength(0);
	}

	public static InstructionSetContainer getScriptContainer() {
		return instructionSetContainer;
	}

	public static CommandHandler getCommandHandler() {
		if (commandHandler == null) {
			commandHandler = new CommandHandler();
		}
		return commandHandler;
	}

	public static Screen getScreen() {
		return SCREEN;
	}
	public static ImageRepository getImageRepository() {
		return imageRepository;
	}
	public static KeyboardCallback getKeyboardCallback() {
		return KeyboardCallback;
	}
	public static Scripts getScript() {
		return scripts;
	}
	public static ExecutorService getExecutor() {
		return executor;
	}
	public static Robot getRobot() {
		return robot;
	}

	static {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			throw new RuntimeException(e);
		}
	}
}