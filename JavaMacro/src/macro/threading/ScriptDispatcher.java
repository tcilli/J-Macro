package macro.threading;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import macro.Main;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;
import macro.Keys;
import macro.Window;
import macro.scripting.Command;
import macro.scripting.CommandHandler;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ScriptDispatcher {

    /**
     * ScriptDispatcher reads key & mouse inputs from the synchronization class and prepares them for execution
     *
     * @param synchronization The synchronization object for coordinating script execution.
     * @param executorService The executor service for running script execution tasks.
     */
    public ScriptDispatcher(final Synchronization synchronization, final ExecutorService executorService, final CommandHandler commandHandler) {

       executorService.execute(() ->
        {
            StringBuilder keyBuilder = new StringBuilder();
            while (synchronization.isRunning())
            {
                if (synchronization.getKeyPresses() > 0 || synchronization.getMouseClicks() > 0 || synchronization.getKeyReleases() > 0)
                {
                    keyBuilder.setLength(0);

                    if (synchronization.getKeyPresses() > 0)  {
                        keyBuilder.append("ondown-").append(NativeKeyEvent.getKeyText(synchronization.getNextKeyPress()));
                    } else if (synchronization.getKeyReleases() > 0) {
                        keyBuilder.append("onrelease-").append(NativeKeyEvent.getKeyText(synchronization.getNextKeyRelease()));
                    } else {
                        keyBuilder.append(Keys.MOUSE).append(synchronization.getNextMouseClicked());
                    }
                    if (keyBuilder.toString().equals(Keys.ESC)) {
                        stopAllScripts(synchronization);
                        continue;
                    }

                    for (InstructionSet instructionSet : InstructionSetContainer.getInstance().getInstructionSets()) {
                       if (instructionSet.key.equalsIgnoreCase(keyBuilder.toString()) && !instructionSet.lock.get()) {
                            instructionSet.lock.set(true);
                           executeScript(synchronization, instructionSet, executorService, commandHandler);
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
     * @param synchronization A class for thread safe data sharing between threads.
     * @param instructionSet  The instruction set containing the script to be executed.
     * @param executorService The executor service for running the script in a separate thread.
     */
    private void executeScript(final Synchronization synchronization, final InstructionSet instructionSet, final ExecutorService executorService, final CommandHandler commandHandler) {

        Future<?> future = executorService.submit(() -> {
            try {
                while(instructionSet.lock.get() && !Thread.currentThread().isInterrupted()) {

                    instructionSet.lastRan = System.currentTimeMillis();

                    for (Instruction instruction : instructionSet.getInstructions()) {

                        if (instructionSet.windowTitle.length() > 0) {
                            if (!instructionSet.windowTitle.toLowerCase().contains(Window.getActive().toLowerCase())) {
                                Main.getConsoleBuffer().append("Script key: ").append(instructionSet.key)
                                        .append(" requires window to be active: ").append(instructionSet.windowTitle.toLowerCase());
                                Main.pushConsoleMessage();
                                return;
                            }
                        }
                        Command command = commandHandler.commandMap.get(instruction.getFlag());
                        if (command != null) {
                            command.execute(instruction);
                        }
                    }
                    if (!instructionSet.loop) {
                        return;
                    }
                    if (System.currentTimeMillis() - instructionSet.lastRan < 100) {
                        Main.getConsoleBuffer().append("Script ended. \n")
                                .append("File: ").append(instructionSet.scriptPath);
                        Main.pushConsoleMessage();
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println("Script interrupted.");
                }
            }
        });
        synchronization.addScriptFuture(future);

        // this thread will wait for the script to finish executing before unlocking the synchronization object
        executorService.execute(() ->
        {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException | CancellationException e) {
                e.printStackTrace();
            } finally {
                synchronization.removeScriptFuture(future);
                instructionSet.lock.set(false);
            }
        });
    }

    /**
     * Stops all running scripts.
     * @param synchronization A class for thread safe data sharing between threads.
     */
    private void stopAllScripts(Synchronization synchronization) {
        for (InstructionSet instructionSet : InstructionSetContainer.getInstance().getInstructionSets()) {
            instructionSet.lock.set(false);
        }
        for (Future<?> future : synchronization.getScriptFutures()) {
            future.cancel(true);
        }
        synchronization.clearScriptFutures();
    }
}