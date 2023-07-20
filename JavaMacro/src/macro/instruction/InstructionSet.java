package macro.instruction;

import macro.Main;

import java.util.ArrayList;
import java.util.List;

public class InstructionSet {

    private final List<Instruction> instructions;

    public String windowTitle;

    /**
     * Contains the flags for the instruction set
     * bit 0: 0000 0001 threaded (0x01)
     * bit 1: 0000 0010 consume key (0x02)
     * bit 2: 0000 0100 loop (0x04)
     * bit 3: 0000 1000 lock (0x08)
     * bit 4: 0001 0000 requires active window (0x10)
     * bit 5 - 15 -> unused (0x20)
     * bits 16 - 31 -> key (0xFFFF0000)
     */
    public int bFlags = 0;

    public InstructionSet() {
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void insert(Instruction instruction) {
        instructions.add(instruction);
    }

    public void execute() {
        for (Instruction ins : getInstructions()) {
            Main.getCommandHandler().commandMap.get(ins.getFlag()).execute(ins.getData(), this);
        }
    }
}