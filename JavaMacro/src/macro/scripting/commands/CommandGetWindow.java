package macro.scripting.commands;

import macro.instruction.Instruction;
import macro.jnative.Window;
import macro.scripting.Command;

public class CommandGetWindow implements Command {

    @Override
    public void execute(Instruction instruction) {
        Window.printActive();
    }
}
