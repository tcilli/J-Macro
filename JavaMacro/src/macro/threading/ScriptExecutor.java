package macro.threading;

import macro.Main;
import macro.Window;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.scripting.Command;
import java.util.concurrent.ExecutorService;

/**
 * ScriptExecutor.java.
 * <p>
 *   Executes the specified script in a separate thread.
 *   This class is now complete and should not be modified.
 * </p>
 */
public class ScriptExecutor {

	/**
	 * Executes the specified script in a separate thread.
	 *
	 * @param instructionSet  The instruction set containing the script to be executed.
	 * @param executorService The executor service for running the script in a separate thread.
	 */
	public static void executeScript(final InstructionSet instructionSet, final ExecutorService executorService) {

		executorService.execute(() -> {
			try {
				while (instructionSet.lock.get() && !Thread.currentThread().isInterrupted()) {

					for (Instruction instruction : instructionSet.getInstructions()) {

						if (instructionSet.windowTitle.length() > 0) {
							if (!instructionSet.windowTitle.toLowerCase().contains(Window.getActive().toLowerCase())) {
								instructionSet.lock.set(false);
								return;
							}
						}
						Command command = Main.getCommandHandler().commandMap.get(instruction.getFlag());

						if (command != null) {
							command.execute(instruction, instructionSet);
						} else {
							instructionSet.lock.set(false);
							return;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				instructionSet.lock.set(false);
			}
		});
	}
}
