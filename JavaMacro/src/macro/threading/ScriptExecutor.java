package macro.threading;

import macro.Main;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;

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
	 * @param insSet  The instruction set containing the script to be executed.
	 * @param executorService The executor service for running the script in a separate thread.
	 */
	public static void executeScript(final InstructionSet insSet, final ExecutorService executorService) {

		/*
		 *  Executes the script in a separate thread.
		 */
		executorService.execute(() -> {

			while (insSet.lock.get()) {

				/*
				 *  Loop through each instruction in this instruction set.
				 */
				for (Instruction ins : insSet.getInstructions()) {


					/*
					 * Execute the command associated with this instruction. This should never be null!.
					 * The InstructionSet lock is passed in as a parameter to the command because the
					 * command may need to release the lock on the InstructionSet.
					 */
					Main.getCommandHandler().commandMap.get(ins.getFlag())
							.execute
									(ins.getData(), insSet);

				}
			}
		});
	}
}
