package macro.threading;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import macro.Main;
import macro.instruction.InstructionSet;
import macro.Keys;

import java.util.concurrent.ExecutorService;

/**
 * ScriptDispatcher.java.
 * <p>
 *     Reads key & mouse inputs from the synchronization class and prepares them for execution.
 *     The ScriptDispatcher Thread should never be interrupted.
 *     This class is now complete and should not be modified.
 * </p>
 */
public class ScriptDispatcher {

	/**
	 * ScriptDispatcher reads key & mouse inputs from the synchronization class and prepares them for execution
	 *
	 * @param synchronization The synchronization object for coordinating script execution.
	 * @param executorService The executor service for running script execution tasks.
	 */
	public ScriptDispatcher(final Synchronization synchronization, final ExecutorService executorService) {

		executorService.execute(() -> {

			final StringBuilder keyBuilder = new StringBuilder();

			while (synchronization.isRunning()) {

				if (synchronization.getKeyPresses() > 0 || synchronization.getMouseClicks() > 0 || synchronization.getKeyReleases() > 0) {

					keyBuilder.setLength(0);

					if (synchronization.getKeyPresses() > 0) {
						keyBuilder.append(Keys.ONDOWN).append(NativeKeyEvent.getKeyText(synchronization.getNextKeyPress()));
					} else if (synchronization.getKeyReleases() > 0) {
						keyBuilder.append(Keys.ONRELEASE).append(NativeKeyEvent.getKeyText(synchronization.getNextKeyRelease()));
					} else {
						keyBuilder.append(Keys.MOUSE).append(synchronization.getNextMouseClicked());
					}

					if (keyBuilder.toString().equals(Keys.ESC)) {
						for (InstructionSet instructionSet : Main.getInstructionSetContainer().getInstructionSets()) {
							instructionSet.lock.set(false);
						}
						continue;
					}

					for (InstructionSet instructionSet : Main.getInstructionSetContainer().getInstructionSets()) {
						if (instructionSet.key.equalsIgnoreCase(keyBuilder.toString()) && !instructionSet.lock.get()) {
							instructionSet.lock.set(true);
							ScriptExecutor.executeScript(instructionSet, executorService);
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
