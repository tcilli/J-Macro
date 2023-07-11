package macro.scripting;

import macro.instruction.Instruction;

public interface Command {

    void execute(Instruction instruction) throws Exception;

}
