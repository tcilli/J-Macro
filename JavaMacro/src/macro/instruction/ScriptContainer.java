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

    public void listInstructions() {
	    for (Map.Entry<Integer, InstructionSet> entry : instructionSetMap.entrySet()) {
		    InstructionSet set = entry.getValue();
			int key = entry.getKey();
			int usableKey = key;
			if (key < 0) {
				usableKey = key * -1;
			}
		    Main.getConsoleBuffer().append("Instruction Set, keycode: ").append(set.key).append(" key: ").append(NativeKeyEvent.getKeyText(usableKey)).append(", path: ").append(set.scriptPath).append("\n");
		    for (Instruction i : set.getInstructions()) {
			    for (Data<?> data : i.getData()) {
				    Main.getConsoleBuffer().append("id: ").append(i.getFlag()).append(", contents: ").append(data.print()).append("\n");
			    }
		    }
	    }
        Main.pushConsoleMessage();
    }



}
