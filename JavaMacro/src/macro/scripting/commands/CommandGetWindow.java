package macro.scripting.commands;

import macro.Main;
import macro.instruction.Instruction;
import macro.jnative.Window;
import macro.scripting.Command;

public class CommandGetWindow implements Command {

    @Override
    public void execute(Instruction instruction) {
        Main.console.append("Current window: ").append(Window.getActive()).append("\n");
        Main.pushConsoleMessage();
    }
}
