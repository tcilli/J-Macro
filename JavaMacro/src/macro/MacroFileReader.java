package macro;

import macro.instruction.Data;
import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.scripting.CommandHandler;
import macro.win32.MouseEvent;

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
		Main.getConsoleBuffer().append("Building scripts");
		Main.pushConsoleMessage();
		for (File file : files) {
			readLinesFromFile(file);
		}
	}

	private void readLinesFromFile(File file) {

		InstructionSet instructionSet = new InstructionSet();
		StringBuffer consoleBuffer = Main.getConsoleBuffer();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;

			byte cur_line = 0;

			String key = "";

			while ((line = reader.readLine()) != null) {
				cur_line++;

				if (line.isEmpty()) {
					continue;
				}
				if (line.length() < 4) {
					appendInvalidInstruction(consoleBuffer, file, cur_line, line);
					return;
				}
				String command = line.substring(0, 4);

				Instruction instruction = null;

				if (((instructionSet.bFlags >> 16) & 0xFFFF) == 0 && line.contains("macro on")) {
					short keyCode = 0;

					if (line.contains("ondown-")) {
						keyCode = Keys.getKeyCode(line.substring(13).trim());
					} else if (line.contains("onrelease-")) {
						keyCode = (short) -(Keys.getKeyCode(line.substring(16).trim()));
					}
					if (keyCode != 0) {
						instructionSet.bFlags |= ((keyCode & 0xFFFF) << 16);
						continue; //key has been set. goto next line
					} else {
						appendInvalidInstruction(consoleBuffer, file, cur_line, line);
						return;
					}
				}

				if (((instructionSet.bFlags >> 16) & 0xFFFF) == 0) {
					continue; //no key was found go to next line
				}

				if (command.equalsIgnoreCase("wait")) {
					try {
						long wait_for = Long.parseLong(line.substring(5).trim());
						if (wait_for > 0 && wait_for < Long.MAX_VALUE) {
							instruction = new Instruction(CommandHandler.COMMAND_SLEEP, new Data<>(wait_for));

							//set the threaded flag
							instructionSet.bFlags |= 0x01;
						}
					} catch (NumberFormatException e) {
						appendInvalidInstruction(consoleBuffer, file, cur_line, line);
						return;
					}
				} else if (command.equalsIgnoreCase("send")) {
					instruction = new Instruction(CommandHandler.COMMAND_SEND_STRING, new Data<>(line.substring(5)));

				} else if (line.equalsIgnoreCase("click")) {
					short mouseData = MouseEvent.mouseClickPacker( 1, 1, 1);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("rightclick") || line.equalsIgnoreCase("clickright")) {
					short mouseData = MouseEvent.mouseClickPacker( 2, 1, 1);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("middleclick") || line.equalsIgnoreCase("clickmiddle")) {
					short mouseData = MouseEvent.mouseClickPacker( 3, 1, 1);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("mouse1down")) {
					short mouseData = MouseEvent.mouseClickPacker( 1, 1, 0);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("mouse1up")) {
					short mouseData = MouseEvent.mouseClickPacker( 1, 0, 1);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("mouse2down")) {

					short mouseData = MouseEvent.mouseClickPacker( 2, 1, 0);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("mouse2up")) {
					short mouseData = MouseEvent.mouseClickPacker( 2, 0, 1);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("mouse3down")) {
					short mouseData = MouseEvent.mouseClickPacker( 3, 1, 0);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("mouse3up")) {
					short mouseData = MouseEvent.mouseClickPacker( 3, 0, 1);
					instruction = new Instruction(CommandHandler.COMMAND_CLICK, new Data<>(mouseData));

				} else if (line.equalsIgnoreCase("reload")) {
					instruction = new Instruction(CommandHandler.COMMAND_READ_MACRO_FILE, null);

				} else if (line.equalsIgnoreCase("get mousepos")) {
					instruction = new Instruction(CommandHandler.COMMAND_GET_MOUSE_POSITION, null);

				} else if (line.equalsIgnoreCase("get window")) {
					instruction = new Instruction(CommandHandler.COMMAND_PRINT_ACTIVE_WINDOW, null);

				} else if (line.equalsIgnoreCase("get memory")) {
					instruction = new Instruction(CommandHandler.COMMAND_PRINT_MEMORY, null);

				} else if (command.equalsIgnoreCase("move")) {

					boolean abs = !line.contains("moverel");

					String[] parts = line.replaceAll("[^0-9,]", "").split(",");
					if (parts.length < 2) {
						appendInvalidInstruction(consoleBuffer, file, cur_line, line);
						return;
					}
					try {
						short x = Short.parseShort(parts[0].trim());
						short y = Short.parseShort(parts[1].trim());

						int delay = 0;

						if (parts.length == 3) {
							delay = Short.parseShort(parts[2].trim());
							instructionSet.bFlags |= 0x01; //set the threaded flag
						}
						if (x > 0 && y > 0) {
							long mouseData = MouseEvent.mouseMovementPacker(x, y, delay, abs);
							instruction = new Instruction(CommandHandler.COMMAND_MOUSE_MOVE, new Data<>(mouseData));
						}
					} catch (NumberFormatException e) {
						appendInvalidInstruction(consoleBuffer, file, cur_line, e.toString());
						return;
					}
				} else if (command.equalsIgnoreCase("loop")) {
					//set the loop flag and set the threaded flag
					instructionSet.bFlags |= 0x04 | 0x01;

				} else if (line.equalsIgnoreCase("consume")) {
					//set the consume flag
					instructionSet.bFlags |= 0x02;
					Keys.addKeyToConsumableMap((short) ((instructionSet.bFlags >> 16) & 0xFFFF));

				} else if (command.equalsIgnoreCase("wind")) {
					String title = line.substring(6);
					title = title.replaceAll("\\s+", " ");

					if (title.length() > 0x7F) { // Max window title length restricted to 127 characters
						title = title.substring(0, 0x7F);
					}
					if (title.length() > 0) {
						instructionSet.bFlags |= 0x10;
						instructionSet.windowTitle = title.toLowerCase();
					}
				} else {
					appendInvalidInstruction(consoleBuffer, file, cur_line, line);
					return;
				}
				if (instruction != null) {
					instructionSet.insert(instruction);
				}
			}
			if (instructionSet.getInstructions().size() > 0) {

				long waitTime = 0;

				for (Instruction i : instructionSet.getInstructions()) {
					if (i.flag() == CommandHandler.COMMAND_SLEEP) {
						waitTime += i.data().toLong();
					}
				}

				//if the loop flag was set but the waitTime was not met, the file is rejected
				if ((instructionSet.bFlags & 0x04) != 0 && waitTime < 100) {
					appendInvalidInstruction(consoleBuffer, file, cur_line, null);
					return;
				}

				//if the loop flag is not set, the end instruction is inserted
				if ((instructionSet.bFlags & 0x04) == 0) {
					Instruction end = new Instruction(CommandHandler.COMMAND_END, null);
					instructionSet.insert(end);
				}

				//the instruction set passed message is printed to the console-
				Main.getConsoleBuffer().append("File: ").append(file).append(" has been accepted").append(" loop:").append((instructionSet.bFlags & 0x04) != 0).append(" window:").append(instructionSet.windowTitle);
				Main.pushConsoleMessage();

				//finally the instruction set is inserted into the script container
				Main.getScriptContainer().insert(instructionSet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void appendInvalidInstruction(StringBuffer consoleBuffer, File file, byte cur_line, String line) {
		consoleBuffer.append("WARNING: File: ").append(file).append(" Rejected, line(").append(cur_line).append(") is an invalid instruction: ").append(line);
		Main.pushConsoleMessage();
	}
}
