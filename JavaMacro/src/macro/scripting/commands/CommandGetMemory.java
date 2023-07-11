package macro.scripting.commands;

import macro.Main;
import macro.MemoryUtil;
import macro.instruction.Instruction;
import macro.scripting.Command;

import java.lang.management.MemoryUsage;

public class CommandGetMemory implements Command {

    @Override
    public void execute(Instruction instruction) {
        MemoryUsage heapMemoryUsage = MemoryUtil.getHeapMemoryUsage();
        Main.console.append("Memory Heap: ").append(heapMemoryUsage).append("\n");
        Main.pushConsoleMessage();
    }
}
