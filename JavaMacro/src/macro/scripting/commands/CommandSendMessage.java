package macro.scripting.commands;

import macro.instruction.Instruction;
import macro.io.Keys;
import macro.scripting.Command;

public class CommandSendMessage implements Command {

    @Override
    public void execute(Instruction instruction) {
        Keys.sendString((String) instruction.get(0).getValue());
    }
}