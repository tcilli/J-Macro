package macro.instruction;

import java.util.HashMap;
import java.util.Map;

public class ScriptContainer {

	private final Map<Short, InstructionSet> instructionSetMap = new HashMap<>();

	public void insert(final InstructionSet instructionSet) {
		instructionSetMap.put(instructionSet.key, instructionSet);
	}

	public final Map<Short, InstructionSet> getInstructionSetMap() {
		return this.instructionSetMap;
	}

	public void clearInstructions() {
	   instructionSetMap.clear();
   }

	//resets all locks back to false
	public void clearLocks() {
		for (Map.Entry<Short, InstructionSet> entry : instructionSetMap.entrySet()) {
			InstructionSet set = entry.getValue();
			set.bFlags &= ~0x08;
		}
	}
}
