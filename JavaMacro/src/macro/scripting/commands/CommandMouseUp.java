package macro.scripting.commands;

import macro.instruction.Instruction;
import macro.jnative.NativeInput;
import macro.scripting.Command;

public class CommandMouseUp implements Command {

        @Override
        public void execute(Instruction instruction) {
            NativeInput.clickUp((int) instruction.get(0).getValue());
        }
}
