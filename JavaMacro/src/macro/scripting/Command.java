package macro.scripting;

import macro.instruction.Data;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;

/**
 * The {@link Command} interface is used to execute an {@link Instruction}.
 * The Instruction is contained in list within {@link InstructionSet}.
 * Iterate through the list of instructions and execute each one.
 * The {@link InstructionSet} is passed in with the data from the Instruction so specific details can be used in the execution.
 */
public interface Command {

    /**
     * Executes a single {@link Instruction} with the given {@link Instruction#data()} and {@link InstructionSet} which contained this Instruction.
     * @param data The data from the {@link Instruction}
     * @param instructionSet The {@link InstructionSet} which contained this Instruction
     */
    void execute(Data<?> data, InstructionSet instructionSet);

}
