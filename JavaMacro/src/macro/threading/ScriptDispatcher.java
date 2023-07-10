package macro.threading;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import macro.Main;
import macro.MemoryUtil;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;
import macro.io.Keys;
import macro.io.MacroFileReader;
import macro.jnative.NativeInput;
import macro.jnative.Window;

import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ScriptDispatcher {

    /**
     * Constructs a ScriptDispatcher object.
     *
     * @param synchronization The synchronization object for coordinating script execution.
     * @param executorService The executor service for running script execution tasks.
     */
    public ScriptDispatcher(final Synchronization synchronization, final ExecutorService executorService) {

        executorService.execute(() -> {

            StringBuilder keyBuilder = new StringBuilder();

            while (true) {

                if (synchronization.getKeyPresses() > 0 || synchronization.getMouseClicks() > 0)
                {
                    keyBuilder.setLength(0);

                    if (synchronization.getKeyPresses() > 0)  {
                       keyBuilder.append(NativeKeyEvent.getKeyText(synchronization.getNextKeyPress()));
                    } else {
                        keyBuilder.append(Keys.MOUSE).append(synchronization.getNextMouseClicked());
                    }

                    if (keyBuilder.toString().equals(Keys.ESC)) {
                        stopAllScripts(synchronization);
                        continue;
                    }

                    if (synchronization.lock.get()) {
                        continue;
                    }

                    for (InstructionSet instructionSet : InstructionSetContainer.getInstance().getInstructionSets()) {
                       if (instructionSet.key.equalsIgnoreCase(keyBuilder.toString())) {
                           synchronization.lock.set(true);
                           executeScript(synchronization, instructionSet, executorService);
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

    /**
     * Executes the specified script in a separate thread.
     *
     * @param synchronization The synchronization object for coordinating script execution.
     * @param instructionSet  The instruction set containing the script to be executed.
     * @param executorService The executor service for running the script in a separate thread.
     */
    private void executeScript(final Synchronization synchronization, final InstructionSet instructionSet, final ExecutorService executorService) {

        Future<?> future = executorService.submit(() -> {

            while(true) {

                try {

                    instructionSet.lastRan = System.currentTimeMillis();

                    for (Instruction instruction : instructionSet.getInstructions()) {
                        if (instructionSet.windowTitle.length() > 0) {
                            if (!instructionSet.windowTitle.toLowerCase().contains(Window.getActive().toLowerCase())) {
                                Main.console.append("Script key: ").append(instructionSet.key)
                                        .append(" requires window to be active: ").append(instructionSet.windowTitle.toLowerCase());
                                Main.pushConsoleMessage();
                                return;
                            }
                        }
                        switch(instruction.getFlag()) {
                            case 0:  break;
                            case 1:  Thread.sleep((long) instruction.get(0).getValue()); break;
                            case 2:  Keys.sendString((String) instruction.get(0).getValue());  break;
                            case 5:  NativeInput.click(1); break;
                            case 6:  NativeInput.click(2); break;
                            case 7:  NativeInput.click(3); break;
                            case 8:  NativeInput.clickDown(1); break;
                            case 9:  NativeInput.clickUp(1); break;
                            case 10: NativeInput.clickDown(2); break;
                            case 11: NativeInput.clickUp(2); break;
                            case 12: NativeInput.clickDown(3); break;
                            case 13: NativeInput.clickUp(3); break;
                            case 14:  NativeInput.mouseMove((int) instruction.get(0).getValue(),(int)instruction.get(1).getValue(), (int) instruction.get(2).getValue(), true); break;
                            case 15: new MacroFileReader(); break;
                            case 16:
                                int count = 0;
                                for (InstructionSet sets : InstructionSetContainer.getInstance().getInstructionSets()) {
                                    Main.console.append(count).append("-> bind: ").append(sets.getInstruction(0).get(0).getValue().toString()).append("\n")
                                            .append(count++).append("-> path: ").append(sets.scriptPath).append("\n");
                                }
                                MemoryUsage heapMemoryUsage = MemoryUtil.getHeapMemoryUsage();
                                Main.console.append("Total of ").append(count).append(" scripts, Memory Heap: ").append(heapMemoryUsage).append("\n");
                                Main.pushConsoleMessage();
                                break;
                            case 17: NativeInput.getMousePosition(); break;
                            case 18:
                                Main.console.append("Current window: ").append(Window.getActive()).append("\n");
                                Main.pushConsoleMessage();
                                break;
                        }
                    }
                } catch (InterruptedException expectedException) {
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!instructionSet.loop) {
                    return;
                }
                if (System.currentTimeMillis() - instructionSet.lastRan < 100) {
                    Main.console.append("Script detected as running too quickly with looping enabled, min run time is 100ms. \n")
                            .append("Consider adding a wait time EG wait 500 or remove the loop command. \n")
                            .append("File: ").append(instructionSet.scriptPath);
                    Main.pushConsoleMessage();
                    return;
                }
            }
        });
        synchronization.addScriptFuture(future);
        executorService.execute(() -> {
            try {
                future.get(); //TODO work on something more elegant than this.
                synchronization.removeScriptFuture(future);
                synchronization.lock.set(false);
            } catch (InterruptedException | CancellationException expectedExceptions) {
                synchronization.removeScriptFuture(future);
                synchronization.lock.set(false);
            } catch (ExecutionException unexpectedException) {
                unexpectedException.printStackTrace();
            }
        });
    }

    /**
     * Stops all running scripts.
     *
     * @param synchronization The synchronization object for coordinating script execution.
     */
    private void stopAllScripts(Synchronization synchronization) {
        for (Future<?> future : synchronization.getScriptFutures()) {
            future.cancel(true);
        }
        synchronization.clearScriptFutures();
        synchronization.lock.set(false);
    }
}