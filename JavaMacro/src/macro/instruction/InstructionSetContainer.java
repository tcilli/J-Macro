package macro.instruction;

import macro.Main;

import java.util.ArrayList;
import java.util.List;

public class InstructionSetContainer {

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

    public void listInstructions() {
        int count = 0;
        for (InstructionSet sets : getInstructionSets()) {
            Main.getConsoleBuffer().append(count).append("-> bind: ").append(sets.getInstruction(0).get(0).value().toString()).append("\n")
                    .append(count++).append("-> path: ").append(sets.scriptPath).append("\n");
        }
        Main.getConsoleBuffer().append("Total of ").append(count).append(" scripts");
        Main.pushConsoleMessage();
    }

}
