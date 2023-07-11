package macro.scripting.commands;

import macro.instruction.Instruction;
import macro.scripting.Command;

public class CommandSleep implements Command {

    @Override
    public void execute(Instruction instruction) throws InterruptedException {
        Thread.sleep((long) instruction.get(0).getValue());
    }
}
