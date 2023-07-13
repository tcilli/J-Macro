package macro.instruction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InstructionSet {

    private final List<Instruction> instructions;
    public boolean loop = false;
    public String windowTitle = "";
    public String scriptPath = "";
    public int key = 0;

    //prevents multiple instances of the same script from running at the same time
    public AtomicBoolean lock = new AtomicBoolean(false);

    public InstructionSet() {
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void insert(Instruction instruction) {
        instructions.add(instruction);
    }
}