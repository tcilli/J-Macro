package macro.scripting;

import macro.instruction.Data;
import macro.instruction.InstructionSet;

import java.util.List;

public interface Command {

    void execute(List<Data<?>> data, InstructionSet insSet);

}
