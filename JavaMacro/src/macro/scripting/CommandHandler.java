package macro.scripting;

import macro.scripting.commands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {

    public Map<Integer, Command> commandMap = new HashMap<>();

    public CommandHandler() {
        commandMap.put(0, new CommandInstructionKey());
        commandMap.put(1, new CommandSleep());
        commandMap.put(2, new CommandSendMessage());
        commandMap.put(3, new CommandClick());
        commandMap.put(4, new CommandMouseDown());
        commandMap.put(5, new CommandMouseUp());
        commandMap.put(6, new CommandMouseMove());
        commandMap.put(7, new CommandReload());
        commandMap.put(8, new CommandGetMemory());
        commandMap.put(9, new CommandGetScripts());
        commandMap.put(10, new CommandGetMousePosition());
        commandMap.put(11, new CommandGetWindow());
    }

}
