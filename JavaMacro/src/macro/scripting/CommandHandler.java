package macro.scripting;

import macro.*;
import macro.jnative.NativeInput;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    // Define constants for command numbers
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

    // Define a map of command numbers to command functions
    public Map<Integer, Command> commandMap = new HashMap<>();

	/*
	 * Please note only some commands require a window check, such as mouse clicks and sending strings
	 */
	public CommandHandler()
    {
        commandMap.put(COMMAND_SLEEP,  (data, set) -> {
            try {
                Thread.sleep(data.get(0).toLong());
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        commandMap.put(COMMAND_SEND_STRING, (data, set) -> {
			if (failedWindowCheck(set.windowTitle)) {
				return;
			}
			Keys.sendString(data.get(0).toString());
		});

        commandMap.put(COMMAND_CLICK,  (data, set) -> {
			if (failedWindowCheck(set.windowTitle)) {
				return;
			}
			NativeInput.click(data.get(0).toInt());
		});

        commandMap.put(COMMAND_CLICK_DOWN, (data, set) -> {
			if (failedWindowCheck(set.windowTitle)) {
				return;
			}
			NativeInput.clickDown(data.get(0).toInt());
		});

        commandMap.put(COMMAND_CLICK_UP, (data, set) -> {
			if (failedWindowCheck(set.windowTitle)) {
				return;
			}
			NativeInput.clickUp(data.get(0).toInt());
		});

        commandMap.put(COMMAND_MOUSE_MOVE, (data, set) -> {
			if (failedWindowCheck(set.windowTitle)) {
				return;
			}
			NativeInput.mouseMove(
					data.get(0).toInt(),
					data.get(1).toInt(),
					data.get(2).toInt(), true);
		});

        commandMap.put(COMMAND_READ_MACRO_FILE,
		        (data, set) -> new MacroFileReader());

        commandMap.put(COMMAND_PRINT_MEMORY,
		        (data, set) -> MemoryUtil.printHeapMemoryUsage());

        commandMap.put(COMMAND_LIST_INSTRUCTIONS,
		        (data, set) -> Main.getInstructionSetContainer().listInstructions());

        commandMap.put(COMMAND_GET_MOUSE_POSITION,
		        (data, set) -> NativeInput.getMousePosition());

        commandMap.put(COMMAND_PRINT_ACTIVE_WINDOW,
		        (data, set) -> Window.printActive());

		commandMap.put(COMMAND_END,
				(data, set) -> set.lock.set(false));
    }

	private boolean failedWindowCheck(String windowTitle) {
		if (windowTitle.length() > 0) {
			return !Window.getActive().contains(windowTitle);
		}
		return false;
	}
}
