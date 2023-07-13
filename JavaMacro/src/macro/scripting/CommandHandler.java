package macro.scripting;

import macro.*;
import macro.jnative.NativeInput;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

	public static CommandHandler instance = null;

	public static CommandHandler getInstance() {
		if (instance == null) {
			instance = new CommandHandler();
		}
		return instance;
	}

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

    // Contains Lambda functions for each command
    public CommandHandler()
    {
        commandMap.put(COMMAND_SLEEP, (instruction, set)-> {
            try {
                Thread.sleep(instruction.get(0).toLong());
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        commandMap.put(COMMAND_SEND_STRING,
		        (instruction, set)-> Keys.sendString(instruction.get(0).toString()));

        commandMap.put(COMMAND_CLICK,
		        (instruction, set)-> NativeInput.click(instruction.get(0).toInt()));

        commandMap.put(COMMAND_CLICK_DOWN,
		        (instruction, set)-> NativeInput.clickDown(instruction.get(0).toInt()));

        commandMap.put(COMMAND_CLICK_UP,
		        (instruction, set)-> NativeInput.clickUp(instruction.get(0).toInt()));

        commandMap.put(COMMAND_MOUSE_MOVE,
		        (instruction, set)-> NativeInput.mouseMove(
                        instruction.get(0).toInt(),
                        instruction.get(1).toInt(),
                        instruction.get(2).toInt(), true));

        commandMap.put(COMMAND_READ_MACRO_FILE,
		        (instruction, set)-> new MacroFileReader());

        commandMap.put(COMMAND_PRINT_MEMORY,
		        (instruction, set)-> MemoryUtil.printHeapMemoryUsage());

        commandMap.put(COMMAND_LIST_INSTRUCTIONS,
		        (instruction, set)-> Main.getInstructionSetContainer().listInstructions(set));

        commandMap.put(COMMAND_GET_MOUSE_POSITION,
		        (instruction, set)-> NativeInput.getMousePosition());

        commandMap.put(COMMAND_PRINT_ACTIVE_WINDOW,
		        (instruction, set)-> Window.printActive());

		commandMap.put(COMMAND_END,
				(instruction, set) -> set.lock.set(false));
    }
}
