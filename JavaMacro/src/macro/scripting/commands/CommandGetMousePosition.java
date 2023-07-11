package macro.scripting.commands;

import macro.scripting.Command;
import macro.instruction.Instruction;
import macro.jnative.NativeInput;

public class CommandGetMousePosition implements Command {

    @Override
    public void execute(Instruction instruction) {
        NativeInput.getMousePosition();
    }

}
