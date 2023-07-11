package macro.scripting.commands;

import macro.instruction.Instruction;
import macro.io.MacroFileReader;
import macro.scripting.Command;

import java.io.IOException;

public class CommandReload implements Command {

    @Override
    public void execute(Instruction instruction) throws IOException {
        new MacroFileReader();
    }
}
