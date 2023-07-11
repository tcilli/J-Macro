package macro.scripting.commands;

import macro.MemoryUtil;
import macro.instruction.Instruction;
import macro.scripting.Command;

public class CommandGetMemory implements Command {

    @Override
    public void execute(Instruction instruction) {
        MemoryUtil.printHeapMemoryUsage();
    }
}
