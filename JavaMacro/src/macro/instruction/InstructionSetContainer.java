package macro.instruction;

import java.util.ArrayList;
import java.util.List;

public class InstructionSetContainer {

    private static InstructionSetContainer instance = null;

    public static InstructionSetContainer getInstance() {
        if (instance == null) {
            instance = new InstructionSetContainer();
        }
        return instance;
    }

    private List<InstructionSet> instructionSets = new ArrayList<>();
    public List<InstructionSet> getInstructionSets() {
        return instructionSets;
    }
    public void insert(InstructionSet instructionSet) {
        instructionSets.add(instructionSet);
    }
    public void clearInstructions() {
        instructionSets = new ArrayList<>();
    }
    public int getSize() {
        return instructionSets.size();
    }

    public InstructionSet getInstructionSet(int index) {
        if (index < 0 || index >= instructionSets.size()) {
            throw new IndexOutOfBoundsException("Invalid index");
        }
        return instructionSets.get(index);
    }
}
