package macro.scripting;

import macro.*;
import macro.instruction.Data;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.jnative.NativeInput;
import macro.win32.KbEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link CommandHandler} class is responsible for
 * linking of the {@link Instruction#getFlag()} to {@link Command} interface.
 * The {@link Command} interface is used to execute the stored action of an {@link Instruction}.
 * <p>See {@link Command#execute(List, InstructionSet)}</p>
 */
public class CommandHandler {

	/**
	 * The commandMap
	 */
	public final Map<Integer, Command> commandMap = new HashMap<>();

	/**
	 * Populates the {@link #commandMap} Each {@link Command} is an interface.
	 * Note: only some commands require a window check, such as mouse clicks and sending strings.
	 * This prevents an {@link Instruction} from executing in the wrong window (if the user has specified a window).
	 * Each {@link Instruction} consists of {@link Instruction#getFlag()} used as the Key for the {@link #commandMap}
	 * and a list of {@link Data} {@link Record}s. The {@link #commandMap} stores the action of a specific {@link Instruction}.
	 */
	public CommandHandler() {
		/*
		 * The sleep command requires 1 data argument: (long) duration
		 */
		commandMap.put(COMMAND_SLEEP,  (data, set) -> {
			try {
				Thread.sleep(data.get(0).toLong());
			} catch(InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		});

		/*
		 * The send string command requires 1 data argument: (String) message
		 */
		commandMap.put(COMMAND_SEND_STRING, (data, set) -> {
			if (failedWindowCheck(set)) {
				return;
			}
			KbEvent.send_characters(data.get(0).toCharArray());
		});

		/*
		 * The mouse click command requires 1 data argument: (Integer) mouseButton
		 */
		commandMap.put(COMMAND_CLICK,  (data, set) -> {
			if (failedWindowCheck(set)) {
				return;
			}
			NativeInput.click(data.get(0).toInt());
		});

		/*
		 * The mouse click down command requires 1 data argument: (Integer) mouseButton
		 */
		commandMap.put(COMMAND_CLICK_DOWN, (data, set) -> {
			if (failedWindowCheck(set)) {
				return;
			}
			NativeInput.clickDown(data.get(0).toInt());
		});

		/*
		 * The mouse click up command requires 1 data argument: (Integer) mouseButton
		 */
		commandMap.put(COMMAND_CLICK_UP, (data, set) -> {
			if (failedWindowCheck(set)) {
				return;
			}
			NativeInput.clickUp(data.get(0).toInt());
		});

		/*
		 * The mouse move command requires 3 data arguments:
		 * (Integer) x position
		 * (Integer) y position
		 * (Integer) duration
		 * Also Requires 1 boolean argument:
		 * (Boolean) relative
		 */
		commandMap.put(COMMAND_MOUSE_MOVE, (data, set) -> {
			if (failedWindowCheck(set)) {
				return;
			}
			NativeInput.mouseMove(
				data.get(0).toInt(),
				data.get(1).toInt(),
				data.get(2).toInt(), true);
		});

		commandMap.put(COMMAND_MOVE_MOUSE_RETURN, (data, set) -> {
			if (failedWindowCheck(set)) {
				return;
			}
			NativeInput.moveMouseReturn(
				data.get(0).toInt(),
				data.get(1).toInt(),
				data.get(2).toInt(), true);
		});

		/*
		 * Reloads the macro file
		 */
		commandMap.put(COMMAND_READ_MACRO_FILE,
			(data, set) -> new MacroFileReader());

		/*
		 * Prints the current heap memory usage
		 */
		commandMap.put(COMMAND_PRINT_MEMORY,
			(data, set) -> MemoryUtil.printHeapMemoryUsage());

		/*
		 * Prints the all the InstructionSets
		 */
		commandMap.put(COMMAND_LIST_INSTRUCTIONS,
			(data, set) -> Main.getScriptContainer().listInstructions());

		/*
		 * Prints the current mouse position
		 */
		commandMap.put(COMMAND_GET_MOUSE_POSITION,
			(data, set) -> NativeInput.getMousePosition());

		/*
		 * Prints the active window title
		 */
		commandMap.put(COMMAND_PRINT_ACTIVE_WINDOW,
			(data, set) -> Window.printActive());

		/*
		 * Sets the lock to false, which will end the effective macro/script
		 */
		commandMap.put(COMMAND_END,
			(data, set) -> set.FLAGS |= 0x08);
					//set.lock.set(false));
	}

	/**
	 * Compares the {@link String} windowTitle to {@link Window#getActive()}.
	 * This is only used for commands that require a window check.
	 * This only applies to an {@link InstructionSet} where the user has specified a {@link InstructionSet#windowTitle}.
	 * If not specified, this will always return false
	 * @param windowTitle the window title to look for
	 * @return True if the window title is not found and was specified.
	 */
	private boolean failedWindowCheck(InstructionSet set) {
		if ((set.FLAGS & 0x04) == 0) {
			return false;
		}
		if (set.windowTitle.length() > 0) {
			return !Window.getActive().contains(set.windowTitle);
		}
		return false;
	}

	// Command Flags
	public static final int COMMAND_SLEEP = 1;
	public static final int COMMAND_SEND_STRING = 2;
	public static final int COMMAND_CLICK = 3;
	public static final int COMMAND_CLICK_DOWN = 4;
	public static final int COMMAND_CLICK_UP = 5;
	public static final int COMMAND_MOUSE_MOVE = 6;
	public static final int COMMAND_READ_MACRO_FILE = 7;
	public static final int COMMAND_PRINT_MEMORY = 8;
	public static final int COMMAND_LIST_INSTRUCTIONS = 9;
	public static final int COMMAND_GET_MOUSE_POSITION = 10;
	public static final int COMMAND_PRINT_ACTIVE_WINDOW = 11;
	public static final int COMMAND_END = 12;
	public static final int COMMAND_MOVE_MOUSE_RETURN = 13;
}

