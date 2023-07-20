package macro.instruction;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to store all the {@link InstructionSet}s
 * that are created when parsing a macro file. Everytime the macro parser
 * is run the script container is cleared and new instructions are built.
 */
public class ScriptContainer {

	private final Map<Short, InstructionSet> instructionSetMap = new HashMap<>();

	public void insert(final InstructionSet instructionSet) {
		instructionSetMap.put((short) ((instructionSet.bFlags >> 16) & 0xFFFF), instructionSet);
	}

	public final Map<Short, InstructionSet> getInstructionSetMap() {
		return this.instructionSetMap;
	}

	public void clearInstructions() {
		instructionSetMap.clear();
	}

	public void clearLocks() {
		for (Map.Entry<Short, InstructionSet> entry : instructionSetMap.entrySet()) {
			InstructionSet set = entry.getValue();
			set.bFlags &= ~0x08;
		}
	}
}
