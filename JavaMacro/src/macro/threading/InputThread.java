package macro.threading;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import macro.Main;
import macro.event.InputEvent;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;

public class InputThread implements Runnable {

    private final Synchronization synchronization;

    public InputThread(Synchronization s) {
        synchronization = s;
    }

    private final String ESC = "Escape";

    @Override
    public void run() {
        synchronized (synchronization) {
            while (true)
            {
                if (synchronization.getKeyPresses() > 0) {
                    String key = NativeKeyEvent.getKeyText(synchronization.getNextKeyPress());
                    if (key.equals(ESC)) {
                        Main.getScriptExecutor().stopAllScripts();
                        continue;
                    }
                    for (InstructionSet instructionSet : InstructionSetContainer.getInstance().getInstructionSets()) {
                        if (instructionSet.getInstruction(0).getData().get(0).getValue().toString().equalsIgnoreCase(key)) {
                            Main.getScriptExecutor().executeScript(instructionSet);
                        }
                    }
                } else if (synchronization.getMouseClicks() > 0) {
                    InputEvent.handleMouse(synchronization.getNextMouseClicked());
                } else {
                    try {
                        synchronization.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}