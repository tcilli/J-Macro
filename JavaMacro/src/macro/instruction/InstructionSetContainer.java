package macro.instruction;

import macro.Main;

import java.util.*;

public class InstructionSetContainer {

	private final Map<Integer, InstructionSet> instructionSetMap = new HashMap<>();

	public void insert1(final InstructionSet instructionSet) {
		instructionSetMap.put(instructionSet.key, instructionSet);
	}

	public InstructionSet getInstructionSet(int key) {
		return instructionSetMap.get(key);
	}

	public void clearInstructions() {
	   instructionSetMap.clear();
   }

	//resets all locks back to false
	public void clearLocks() {
		for (Map.Entry<Integer, InstructionSet> entry : instructionSetMap.entrySet()) {
			InstructionSet set = entry.getValue();
			set.lock.set(false);
		}
	}

    public void listInstructions(InstructionSet set) {
        int count = 0;
        for (Instruction i : set.getInstructions()) {
            Main.getConsoleBuffer().append(count).append("-> bind: ").append(i.get(0).value().toString()).append("\n")
                    .append(count++).append("-> path: ").append(set.scriptPath).append("\n");
        }
        Main.getConsoleBuffer().append("Total of ").append(count).append(" instructions");
        Main.pushConsoleMessage();
    }

}
