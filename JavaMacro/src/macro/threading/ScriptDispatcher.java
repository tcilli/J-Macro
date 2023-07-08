package macro.threading;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;
import macro.io.KeyMap;

import java.util.concurrent.ExecutorService;

public class ScriptDispatcher {

    public ScriptDispatcher(final Synchronization synchronization, final ExecutorService executorService, final ScriptExecutor scriptExecutor) {

        executorService.execute(() ->
        {
            StringBuilder keyBuilder = new StringBuilder();

            while (true)
            {
                if (synchronization.getKeyPresses() > 0 || synchronization.getMouseClicks() > 0)
                {
                    keyBuilder.setLength(0);

                    if (synchronization.getKeyPresses() > 0) {
                       keyBuilder.append(NativeKeyEvent.getKeyText(synchronization.getNextKeyPress()));
                    } else {
                        keyBuilder.append(KeyMap.MOUSE).append(synchronization.getNextMouseClicked());
                    }
                    if (keyBuilder.toString().equals(KeyMap.ESC)) {
                        scriptExecutor.stopAllScripts();
                        continue;
                    }
                    for (InstructionSet instructionSet : InstructionSetContainer.getInstance().getInstructionSets()) {
                       if (instructionSet.key.equalsIgnoreCase(keyBuilder.toString())) {
                           if (synchronization.containsKey(instructionSet.key)) {
                               return;
                           }
                           if (synchronization.addKey(instructionSet.key)) {
                               scriptExecutor.executeScript(instructionSet);
                           }
                       }
                    }
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