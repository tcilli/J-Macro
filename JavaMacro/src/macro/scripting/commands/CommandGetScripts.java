package macro.scripting.commands;

import macro.Main;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;
import macro.scripting.Command;

import java.io.IOException;

public class CommandGetScripts implements Command {

        @Override
        public void execute(Instruction instruction) throws IOException {
            int count = 0;
            for (InstructionSet sets : InstructionSetContainer.getInstance().getInstructionSets()) {
                Main.console.append(count).append("-> bind: ").append(sets.getInstruction(0).get(0).getValue().toString()).append("\n")
                        .append(count++).append("-> path: ").append(sets.scriptPath).append("\n");
            }
            Main.console.append("Total of ").append(count).append(" scripts");
            Main.pushConsoleMessage();
        }
}
