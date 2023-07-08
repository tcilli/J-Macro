package macro.threading;

import macro.Main;
import macro.MemoryUtil;
import macro.instruction.Data;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;
import macro.io.MacroFileReader;
import macro.jnative.NativeInput;
import macro.jnative.Window;

import java.io.IOException;
import java.lang.management.MemoryUsage;
import java.util.concurrent.*;

public class ScriptExecutor {

    private final Synchronization synchronization;
    private final ExecutorService executor;

    public ScriptExecutor(final Synchronization synchronization, final ExecutorService executorService) {
        this.synchronization = synchronization;
        this.executor = executorService;
    }

    public void executeScript(final InstructionSet instructionSet) {

        Future<?> future = executor.submit(() ->
        {
            while(true)
            {
                long start = System.currentTimeMillis();
                try
                {
                    for (Instruction instruction : instructionSet.getInstructions())
                    {
                        if (instructionSet.windowTitle.length() > 0) {
                            if (!instructionSet.windowTitle.toLowerCase().contains(Window.getActive().toLowerCase())) {
                                Main.console.append("Script key: ").append(instructionSet.key).append(" requires window to be active: ").append(instructionSet.windowTitle.toLowerCase());
                                Main.pushConsoleMessage();
                                return;
                            }
                        }
                        switch(instruction.getFlag()) {
                            case 0: break;
                            case 1: Thread.sleep((long) instruction.get(0).getValue());break;
                            case 2:
                                for (Data<?> d : instruction.getData()) {
                                    NativeInput.pressKey((int) d.getValue());
                                }
                                break;
                            case 3:  NativeInput.pressKeyDown((Integer) instruction.get(0).getValue()); break;
                            case 4:  NativeInput.pressKeyUp((Integer) instruction.get(0).getValue()); break;

                            case 5:  NativeInput.click(1); break;
                            case 6:  NativeInput.click(2); break;
                            case 7:  NativeInput.click(3); break;

                            case 8:  NativeInput.clickDown(1); break;
                            case 9:  NativeInput.clickUp(1); break;

                            case 10: NativeInput.clickDown(2); break;
                            case 11: NativeInput.clickUp(2); break;

                            case 12: NativeInput.clickDown(3); break;
                            case 13: NativeInput.clickUp(3); break;
                            case 14: NativeInput.mouseMove((int) instruction.get(0).getValue(),(int)instruction.get(1).getValue(), true); break;

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
                if (System.currentTimeMillis() - start < 100) {
                    Main.console.append("Script detected as running too quickly with looping enabled, min run time is 100ms. \n")
                            .append("Consider adding a wait time EG wait 500 or remove the loop command. \n")
                            .append("File: ").append(instructionSet.scriptPath);
                    Main.pushConsoleMessage();
                    return;
                }
            }
        });
        synchronization.addScriptFuture(future);

        executor.execute(() -> {
            try {
                future.get(); //TODO work on something more elegant than this.
                synchronization.removeScriptFuture(future);
                synchronization.removeKey(instructionSet.key);
            } catch (InterruptedException | CancellationException expectedExceptions) {
                synchronization.removeScriptFuture(future);
                synchronization.removeKey(instructionSet.key);
            } catch (ExecutionException unexpectedException) {
                unexpectedException.printStackTrace();
            }
        });
    }

    public void stopAllScripts() {
        for (Future<?> future : synchronization.getScriptFutures()) {
            future.cancel(true);
        }
        synchronization.clearScriptFutures();
        synchronization.clearKeys();
    }
}
