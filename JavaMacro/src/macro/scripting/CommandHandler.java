package macro.scripting;

import macro.*;
import macro.instruction.InstructionSetContainer;
import macro.jnative.NativeInput;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

	public static CommandHandler instance = null;

	public static CommandHandler getInstance() {
		if(instance == null) {
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

    // Define a map of command numbers to command functions
    public Map<Integer, Command> commandMap = new HashMap<>();

    // Contains Lambda functions for each command since
    // Command is a single instruction interface.
    public CommandHandler()
    {
        commandMap.put(COMMAND_SLEEP,  instruction -> {
            try {
                Thread.sleep(instruction.get(0).toLong());
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        commandMap.put(COMMAND_SEND_STRING,
                instruction -> Keys.sendString(instruction.get(0).toString()));

        commandMap.put(COMMAND_CLICK,
                instruction -> NativeInput.click(instruction.get(0).toInt()));

        commandMap.put(COMMAND_CLICK_DOWN,
                instruction -> NativeInput.clickDown(instruction.get(0).toInt()));

        commandMap.put(COMMAND_CLICK_UP,
                instruction -> NativeInput.clickUp(instruction.get(0).toInt()));

        commandMap.put(COMMAND_MOUSE_MOVE,
                instruction -> NativeInput.mouseMove(
                        instruction.get(0).toInt(),
                        instruction.get(1).toInt(),
                        instruction.get(2).toInt(), true));

        commandMap.put(COMMAND_READ_MACRO_FILE,
                __ -> new MacroFileReader());

        commandMap.put(COMMAND_PRINT_MEMORY,
                __ -> MemoryUtil.printHeapMemoryUsage());

        commandMap.put(COMMAND_LIST_INSTRUCTIONS,
                __ -> Main.getInstructionSetContainer().listInstructions());

        commandMap.put(COMMAND_GET_MOUSE_POSITION,
                __ -> NativeInput.getMousePosition());

        commandMap.put(COMMAND_PRINT_ACTIVE_WINDOW,
                __ -> Window.printActive());
    }
}
