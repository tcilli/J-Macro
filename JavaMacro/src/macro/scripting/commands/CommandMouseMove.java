package macro.scripting.commands;

import macro.instruction.Instruction;
import macro.jnative.NativeInput;
import macro.scripting.Command;

public class CommandMouseMove implements Command {

    @Override
    public void execute(Instruction instruction) {
        NativeInput.mouseMove((int) instruction.get(0).getValue(),(int)instruction.get(1).getValue(), (int) instruction.get(2).getValue(), true);
    }
}
