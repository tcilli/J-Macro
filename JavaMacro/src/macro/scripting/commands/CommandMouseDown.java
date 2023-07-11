package macro.scripting.commands;

import macro.instruction.Instruction;
import macro.jnative.NativeInput;
import macro.scripting.Command;

public class CommandMouseDown implements Command {

    @Override
    public void execute(Instruction instruction) {
        NativeInput.clickDown((int) instruction.get(0).getValue());
    }
}
