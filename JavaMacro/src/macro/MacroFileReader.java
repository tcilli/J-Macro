package macro;

import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.jnative.NativeInput;
import macro.scripting.CommandHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * MacroFileReader.java.
 * <p>
 *     Class may look like ass but it does the job
 *     Eventually ill get around to making this tidy.
 * </p>
 */

public class MacroFileReader {

	public static final String DIR1 = "./data/";
	public static final String DIR2 = "./data/predefined";

	public MacroFileReader() {
		Main.getScriptContainer().clearInstructions();
		Keys.clearConsumableKeys();
		try {
			readFiles(DIR2);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try {
			readFiles(DIR1);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Main.getConsoleBuffer().append("Scripts can be stopped by pressing Esc");
		Main.pushConsoleMessage();
	}

	public void readFiles(final String DIR) throws IOException {
		File directory = new File(DIR);

		if (!directory.isDirectory()) {
			Main.getConsoleBuffer().append("Invalid directory path: ").append(DIR);
			Main.pushConsoleMessage();
			return;
		}
		File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

		if (files == null || files.length == 0) {
			Main.getConsoleBuffer().append("No files found in the directory: ").append(DIR);
			Main.pushConsoleMessage();
			return;
		}
		for (File file : files) {
			readLinesFromFile(file);
		}
	}

	private void readLinesFromFile(File file) {
		InstructionSet instructionSet = new InstructionSet();
		instructionSet.scriptPath = file.getPath();

		boolean hasSleep = false;

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			int cur_line = 0;
			boolean key_found = false;
			String key = "";
			while ((line = reader.readLine()) != null) {
				cur_line++;
				if (line.isEmpty()) {
					continue;
				}
				if (line.length() < 4) {
					Main.getConsoleBuffer()
						.append("File: ").append(file)
						.append(" line(").append(cur_line)
						.append(") is an invalid instruction: ")
						.append(line);
					Main.pushConsoleMessage();
					return;
				}
				String command = line.substring(0, 4);

				Instruction instruction = null;

				if (!key_found && line.length() >= 7 && line.substring(0, 6).equalsIgnoreCase("macro ")) {
					key = line.substring(6);

					String test = key;
					test = test.replace("ondown-", "").replace("onrelease-", "");

					int keyCode = Keys.getKeyCode(test);

					if (keyCode != 0) {
						if (key.contains("onrelease-")) {
							keyCode *= -1;
						}
						instructionSet.key = keyCode;
						key_found = true;
					} else {
						Main.getConsoleBuffer().append("Invalid macro key: ").append(test).append(" , Example 1 (triggered on pressing f1): macro ondown-f1").append("\n");
						Main.getConsoleBuffer().append("Invalid macro key: ").append(test).append(" , Example 2 (triggered on the release of f1): macro onrelease-f1").append("\n");
						Main.getConsoleBuffer().append("Invalid macro key: ").append(test).append(" , Example 3 (triggered on the pressing home key): macro onrelease-home").append("\n");
						Main.getConsoleBuffer().append("Invalid macro key: ").append(test).append(" , Example 4 (triggered on the release of captial J): macro onrelease-J").append("\n");
						Main.pushConsoleMessage();
						return;
					}

				} else if (key_found && command.equalsIgnoreCase("wait")) {
					String wait_command = line.substring(4);
					wait_command = wait_command.replaceAll("\\D", "");
					try {
						long wait_for = Long.parseLong(wait_command);
						if (wait_for > 0 && wait_for < Long.MAX_VALUE) {
							instruction = new Instruction(CommandHandler.COMMAND_SLEEP);
							instruction.insert(wait_for);
							hasSleep = true;
						}
					} catch (NumberFormatException e) {
						Main.getConsoleBuffer().append("Invalid number format for ").append(line).append("\n").append("Syntax: wait 1000 \n").append(e);
						Main.pushConsoleMessage();
						return;
					}
				} else if (key_found && command.equalsIgnoreCase("send")) {
					instruction = new Instruction(CommandHandler.COMMAND_SEND_STRING);
					instruction.insert(line.substring(5));
				} else if (key_found && line.equalsIgnoreCase("click")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK);
					instruction.insert(NativeInput.MOUSE_BUTTON_LEFT);
				} else if (key_found && line.equalsIgnoreCase("rightclick")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK);
					instruction.insert(NativeInput.MOUSE_BUTTON_RIGHT);
				} else if (key_found && line.equalsIgnoreCase("middleclick")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK);
					instruction.insert(NativeInput.MOUSE_BUTTON_MIDDLE);
				} else if (key_found && line.equalsIgnoreCase("mouse1down")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK_DOWN);
					instruction.insert(NativeInput.MOUSE_BUTTON_LEFT);
				} else if (key_found && line.equalsIgnoreCase("mouse1up")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK_UP);
					instruction.insert(NativeInput.MOUSE_BUTTON_LEFT);
				} else if (key_found && line.equalsIgnoreCase("mouse2down")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK_DOWN);
					instruction.insert(NativeInput.MOUSE_BUTTON_RIGHT);
				} else if (key_found && line.equalsIgnoreCase("mouse2up")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK_UP);
					instruction.insert(NativeInput.MOUSE_BUTTON_RIGHT);
				} else if (key_found && line.equalsIgnoreCase("mouse3down")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK_DOWN);
					instruction.insert(NativeInput.MOUSE_BUTTON_MIDDLE);
				} else if (key_found && line.equalsIgnoreCase("mouse3up")) {
					instruction = new Instruction(CommandHandler.COMMAND_CLICK_UP);
					instruction.insert(NativeInput.MOUSE_BUTTON_MIDDLE);
				} else if (key_found && line.equalsIgnoreCase("reload")) {
					instruction = new Instruction(CommandHandler.COMMAND_READ_MACRO_FILE);
				} else if (key_found && line.equalsIgnoreCase("get scripts")) {
					instruction = new Instruction(CommandHandler.COMMAND_LIST_INSTRUCTIONS);
				} else if (key_found && line.equalsIgnoreCase("get mousepos")) {
					instruction = new Instruction(CommandHandler.COMMAND_GET_MOUSE_POSITION);
				} else if (key_found && line.equalsIgnoreCase("get window")) {
					instruction = new Instruction(CommandHandler.COMMAND_PRINT_ACTIVE_WINDOW);
				} else if (key_found && line.equalsIgnoreCase("get memory")) {
					instruction = new Instruction(CommandHandler.COMMAND_PRINT_MEMORY);

				} else if (key_found && command.equalsIgnoreCase("move")) {

					int offset = 4;

					if (line.contains("movereturn")) {
						offset = 10;
					}

					String move_command = line.substring(offset);

					move_command = move_command.replaceAll("[a-zA-Z]", "");
					String[] coordinates = move_command.split(",");

					if (coordinates.length >= 2) {
						for (int i = 0; i < coordinates.length; i++) {
							coordinates[i] = coordinates[i].replaceAll("\\D", "");
						}
						try {
							int x = Integer.parseInt(coordinates[0]);
							int y = Integer.parseInt(coordinates[1]);
							int delay = 0;
							if (coordinates.length == 3) {
								delay = Integer.parseInt(coordinates[2]);
								if (delay > 0) {
									hasSleep = true;
								}
							}
							if (x > 0 && x < 65535 && y > 0 && y < 65535) {
								if (offset == 4) {
									instruction = new Instruction(CommandHandler.COMMAND_MOUSE_MOVE);
									instruction.insert(x);
									instruction.insert(y);
									instruction.insert(delay);
								} else {
									instruction = new Instruction(CommandHandler.COMMAND_MOVE_MOUSE_RETURN);
									instruction.insert(x);
									instruction.insert(y);
									instruction.insert(delay);
								}
							}
						} catch (NumberFormatException e) {
							Main.getConsoleBuffer()
								.append("Invalid number format for ")
								.append(line).append("\n")
								.append("Syntax: move 500,500 \n")
								.append(e);
							Main.pushConsoleMessage();
							return;
						}
					}
				} else if (key_found && command.equalsIgnoreCase("loop")) {
					instructionSet.FLAGS |= 0x04;
				} else if (key_found && line.equalsIgnoreCase("consume")) {
					instructionSet.FLAGS |= 0x02;
					Keys.addKeyToConsumableList(instructionSet.key);
				} else if (key_found && command.equalsIgnoreCase("wind")) {
					String title = line.substring(6);
					title = title.replaceAll(" ", "");
					instructionSet.windowTitle = title.toLowerCase();
					if (title.length() > 0) {
						instructionSet.FLAGS |= 0x10;
					}
				} else {
					Main.getConsoleBuffer().append("File: ").append(file).append(" line:").append(cur_line).append(" is an invalid instruction: ").append(line);
					Main.pushConsoleMessage();
					return;
				}
				if (instruction != null) {
					instructionSet.insert(instruction);
				}
			}
			if (instructionSet.getInstructions().size() > 0) {
				boolean hasWait = false;
				long waitTime = 0;
				for (Instruction i : instructionSet.getInstructions()) {
					if (i.getFlag() == CommandHandler.COMMAND_SLEEP) {
						waitTime += i.get(0).toLong();
						if (waitTime >= 100) {
							hasWait = true;
						}
					}
				}
				if ((instructionSet.FLAGS & 0x04) != 0 && !hasWait) {
					Main.getConsoleBuffer().append("File: ").append(file).append(" Rejected, Requires a wait command if using the loop instruction, \n")
						.append("Or did not meet the required minimum wait time of 100ms. Detected delay time: ").append(waitTime).append("ms");
					Main.pushConsoleMessage();
					return;
				}

				//if the loop flag is not set, the end instruction is inserted
				if ((instructionSet.FLAGS & 0x04) == 0) {
					Instruction end = new Instruction(CommandHandler.COMMAND_END);
					instructionSet.insert(end);
				}

				//the instruction set passed message is printed to the console
				Main.getConsoleBuffer().append("File: ").append(file).append(" has been accepted. key bind:")
					.append(key).append(" loop:").append((instructionSet.FLAGS & 0x04) != 0).append(" window:").append(instructionSet.windowTitle);
				Main.pushConsoleMessage();

				//if the loop flag is not set and no sleeps are required, the threadless flag is set
				if ((instructionSet.FLAGS & 0x04) == 0 && !hasSleep) {

					//set the threadless flag
					instructionSet.FLAGS |= 0x01;
				}

				//finally the instruction set is inserted into the script container
				Main.getScriptContainer().insert(instructionSet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
