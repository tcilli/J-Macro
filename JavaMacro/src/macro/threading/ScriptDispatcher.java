package macro.threading;

import macro.Main;
import macro.instruction.InstructionSet;

import java.util.concurrent.ExecutorService;

import static com.github.kwhat.jnativehook.keyboard.NativeKeyEvent.VC_ESCAPE;

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

			int keycode = 0;

			while (synchronization.isRunning()) {

				if (synchronization.getKeyPresses() > 0 || synchronization.getMouseClicks() > 0 || synchronization.getKeyReleases() > 0) {

					if (synchronization.getKeyPresses() > 0) {
						keycode = synchronization.getNextKeyPress();
					} else if (synchronization.getKeyReleases() > 0) {
						keycode = synchronization.getNextKeyRelease();
					} else {
						keycode = synchronization.getNextMouseClicked();
					}
					if (keycode == VC_ESCAPE) {
						Main.getInstructionSetContainer().clearLocks();
						continue;
					}
					InstructionSet instructionSet = Main.getInstructionSetContainer().getInstructionSet(keycode);

					if (instructionSet != null && !instructionSet.lock.get()) {
						instructionSet.lock.set(true);
						ScriptExecutor.executeScript(instructionSet, executorService);
					}
				} else {
					try {
						synchronized (synchronization) {
							synchronization.wait();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}
}
