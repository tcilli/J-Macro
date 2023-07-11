package macro.scripting;

import macro.MacroFileReader;
import macro.MemoryUtil;
import macro.Window;
import macro.instruction.InstructionSetContainer;
import macro.jnative.NativeInput;

import macro.Keys;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    public Map<Integer, Command> commandMap = new HashMap<>();

    public CommandHandler()
    {
        commandMap.put(0,  __ -> {});
        commandMap.put(1,  instruction -> { try {Thread.sleep(instruction.get(0).toLong()); } catch(InterruptedException e) {} });
        commandMap.put(2,  instruction -> Keys.sendString(instruction.get(0).toString()));
        commandMap.put(3,  instruction -> NativeInput.click(instruction.get(0).toInt()));
        commandMap.put(4,  instruction -> NativeInput.clickDown(instruction.get(0).toInt()));
        commandMap.put(5,  instruction -> NativeInput.clickUp(instruction.get(0).toInt()));
        commandMap.put(6,  instruction -> NativeInput.mouseMove(instruction.get(0).toInt(), instruction.get(1).toInt(), instruction.get(2).toInt(), true));
        commandMap.put(7,  __ -> new MacroFileReader());
        commandMap.put(8,  __ -> MemoryUtil.printHeapMemoryUsage());
        commandMap.put(9,  __ -> InstructionSetContainer.getInstance().listInstructions());
        commandMap.put(10, __ -> NativeInput.getMousePosition());
        commandMap.put(11, __ -> Window.printActive());
    }
}
