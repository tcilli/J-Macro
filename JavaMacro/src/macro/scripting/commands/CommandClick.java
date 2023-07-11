package macro.scripting.commands;

import macro.instruction.Instruction;
import macro.jnative.NativeInput;
import macro.scripting.Command;

public class CommandClick implements Command {
    @Override
    public void execute(Instruction instruction) throws Exception {
        NativeInput.click((int) instruction.get(0).getValue());
    }
}
