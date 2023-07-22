package macro.scripting;

import macro.*;
import macro.instruction.Data;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.jnative.NativeInput;
import macro.win32.KbEvent;
import macro.win32.MouseEvent;

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
			MouseEvent.click_mouse(data.get(0).toShort());
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
			MouseEvent.move_mouse(data.get(0).toLong());
		});

		commandMap.put(COMMAND_MOVE_MOUSE_RETURN, (data, set) -> {
			if (failedWindowCheck(set)) {
				return;
			}
			//NativeInput.moveMouseReturn(data.get(0).toLong(), true);
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
		 * Prints the current mouse position
		 */
		commandMap.put(COMMAND_GET_MOUSE_POSITION,
			(data, set) -> NativeInput.getMousePosition());

		/*
		 * Prints the active window title
		 */
		commandMap.put(COMMAND_PRINT_ACTIVE_WINDOW,
			(data, set) -> Window.printActive());


		//COMMAND_END Clears the lock bit from the InstructionSet.FLAGS
		//Essentially this unlocks the Instruction, so it can be used again.
		commandMap.put(COMMAND_END,
			(data, set) -> set.bFlags &= ~0x08);

	}

	/**
	 * Compares the {@link String} windowTitle to {@link Window#getActive()}.
	 * This is only used for commands that require a window check.
	 * This only applies to an {@link InstructionSet} where the user has specified a {@link InstructionSet#windowTitle}.
	 * If not specified, this will always return false
	 * @param set The {@link InstructionSet} being checked
	 * @return True if the window title is not found and was specified.
	 */
	private boolean failedWindowCheck(final InstructionSet set) {

		//check if the window title was specified
		//this flag is only set when the title passed a regex check
		//and the window title was considered valid
		if ((set.bFlags & 0x10) == 0) {
			return false;
		}

		return !Window.getActive().contains(set.windowTitle);
	}

	// Command Flags
	public static final int COMMAND_SLEEP = 1;
	public static final int COMMAND_SEND_STRING = 2;
	public static final int COMMAND_CLICK = 3;
	public static final int COMMAND_MOUSE_MOVE = 6;
	public static final int COMMAND_READ_MACRO_FILE = 7;
	public static final int COMMAND_PRINT_MEMORY = 8;
	public static final int COMMAND_GET_MOUSE_POSITION = 10;
	public static final int COMMAND_PRINT_ACTIVE_WINDOW = 11;
	public static final int COMMAND_END = 12;
	public static final int COMMAND_MOVE_MOUSE_RETURN = 13;
}
