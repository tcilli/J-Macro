package macro.threading;

import macro.Main;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;

import java.util.concurrent.ExecutorService;

/**
 * The {@link ScriptExecutor} class.
 * <p>
 *  Using {@link #execute(InstructionSet, ExecutorService)}
 *  this will execute an {@link InstructionSet} on a thread pool {@link ExecutorService}.
 *  The {@link InstructionSet} contains a list of {@link Instruction}s. which are executed in order.
 * </p>
 */
public class ScriptExecutor {

	/**
	 * Executes an {@link InstructionSet} using a thread pool provided by the {@link ExecutorService}
	 * allows multiple scripts to be executed simultaneously.
	 * This is however, limited to one instance per {@link InstructionSet}.
	 * @param instructionSet The {@link InstructionSet}.
	 * @param threadPool The {@link ExecutorService}.
	 */
	public static void execute(final InstructionSet instructionSet, final ExecutorService threadPool) {
		threadPool.execute(() -> {
			while (instructionSet.lock.get()) {
				for (Instruction ins : instructionSet.getInstructions()) {
					Main.getCommandHandler().commandMap.get(ins.getFlag()).execute(ins.getData(), instructionSet);
				}
			}
		});
	}
}