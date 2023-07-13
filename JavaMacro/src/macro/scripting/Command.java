package macro.scripting;

public interface Command {

    void execute(macro.instruction.Instruction instruction, macro.instruction.InstructionSet set);

}
