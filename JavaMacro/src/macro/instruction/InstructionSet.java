package macro.instruction;

import java.util.ArrayList;
import java.util.List;

public class InstructionSet {

    private final List<Instruction> instructions;
    public boolean loop = false;
    public String windowTitle = "";
    public String scriptPath = "";

    public InstructionSet() {
        this.instructions = new ArrayList<>();
    }

    public List<Instruction> getInstructions() {
        return instructions;
    }

    public void insert(Instruction instruction) {
        instructions.add(instruction);
    }

    public Instruction getInstruction(int index) {
        if (index < 0 || index >= instructions.size()) {
            throw new IndexOutOfBoundsException("Invalid index, Check your commands have an attached action. EG:  wait 500  = (command=wait action=500)");
        }
        return instructions.get(index);
    }
}
