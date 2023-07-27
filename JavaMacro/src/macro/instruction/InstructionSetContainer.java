package macro.instruction;

import macro.Main;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to store all the {@link InstructionSet}s
 * that are created when parsing a macro file. Everytime the macro parser
 * is run the script container is cleared and new instructions are built.
 */
public class InstructionSetContainer {

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

	public void handleKey(final short virtualKeyCode) {
		InstructionSet instructionSet = instructionSetMap.getOrDefault(virtualKeyCode, null);
		if (instructionSet == null) {
			return;
		}
		if ((instructionSet.bFlags & 0x08) == 0) {
			instructionSet.bFlags |= 0x08;
			if ((instructionSet.bFlags & 0x01) == 0) {
				instructionSet.execute();
			}
			else {
				Main.getExecutor().execute(() -> {
					while((instructionSet.bFlags & 0x08) != 0) {
						instructionSet.execute();
					}
				});
			}
		}
	}
}
