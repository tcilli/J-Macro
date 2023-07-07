package macro.event;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import macro.Main;

import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;
public class InputEvent {

    public static void handleMouse(int keycode) {
        InstructionSetContainer container = InstructionSetContainer.getInstance();

        if (container.getSize() == 0) {
            return;
        }
        if (keycode <= 0 || keycode > Integer.MAX_VALUE) {
            return;
        }
        String key = "mouse" + keycode;
        for (InstructionSet instructionSet : container.getInstructionSets()) {
            if (instructionSet.getInstruction(0).getData().get(0).getValue().toString().equalsIgnoreCase(key)) {
                System.out.println("Key pressed: matches instruction key: "+ key +", keycode: "+ keycode);
            }
        }
    }
}
