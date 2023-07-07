package macro.threading;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import macro.event.InputEvent;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;

import java.util.concurrent.ExecutorService;

public class InputThread  {

    private final String ESC = "Escape";

    public InputThread(Synchronization synchronization, ExecutorService executorService, ScriptExecutor scriptExecutor) {

        executorService.execute(() ->
        {
            while (true)
            {
                if (synchronization.getKeyPresses() > 0) {
                    String key = NativeKeyEvent.getKeyText(synchronization.getNextKeyPress());
                    if (key.equals(ESC)) {
                        scriptExecutor.stopAllScripts();
                        continue;
                    }
                    for (InstructionSet instructionSet : InstructionSetContainer.getInstance().getInstructionSets()) {
                        if (instructionSet.getInstruction(0).getData().get(0).getValue().toString().equalsIgnoreCase(key)) {
                            scriptExecutor.executeScript(instructionSet);
                        }
                    }
                } else if (synchronization.getMouseClicks() > 0) {
                    InputEvent.handleMouse(synchronization.getNextMouseClicked());
                } else {
                    try {
                        synchronized (synchronization) {
                            synchronization.wait();
                        }
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}