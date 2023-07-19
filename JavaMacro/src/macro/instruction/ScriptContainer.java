package macro.instruction;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import macro.Main;

import java.util.*;

public class ScriptContainer {

	private final Map<Integer, InstructionSet> instructionSetMap = new HashMap<>();

	public void insert(final InstructionSet instructionSet) {
		instructionSetMap.put(instructionSet.key, instructionSet);
	}

	public final Map<Integer, InstructionSet> getInstructionSetMap() {
		return this.instructionSetMap;
	}

	public void clearInstructions() {
	   instructionSetMap.clear();
   }

	//resets all locks back to false
	public void clearLocks() {
		for (Map.Entry<Integer, InstructionSet> entry : instructionSetMap.entrySet()) {
			InstructionSet set = entry.getValue();
			set.bFlags &= ~0x08;
		}
	}
}
