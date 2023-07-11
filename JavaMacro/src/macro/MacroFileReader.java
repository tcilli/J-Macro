package macro;

import macro.instruction.Instruction;
import macro.instruction.InstructionSet;
import macro.instruction.InstructionSetContainer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MacroFileReader {

    public static final String DIR1 = "./data/";
    public static final String DIR2 = "./data/predefined";

    public MacroFileReader() {
        InstructionSetContainer.getInstance().clearInstructions();
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

    public void readFiles(String DIR) throws IOException {
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

    private void readLinesFromFile(File file) throws IOException
    {
        InstructionSet instructionSet = new InstructionSet();

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
                if (line.length() < 4 ) {
                    Main.getConsoleBuffer().append("File: ").append(file).append(" line(").append(cur_line).append(") is an invalid instruction: ").append(line);
                    Main.pushConsoleMessage();
                    return;
                }
                String command = line.substring(0,4);

                Instruction instruction = null;

                if (!key_found && line.length() >= 7 && line.substring(0, 6).equalsIgnoreCase("macro ")) {
                    key = line.substring(6);
                    key_found = true;
                    instruction = new Instruction(0);
                    instruction.insert(key);
                    instructionSet.key = key;
                }
                else if (key_found && command.equalsIgnoreCase("wait")) {
                    String wait_command = line.substring(4);
                    wait_command = wait_command.replaceAll("\\D", "");
                    try {
                        long wait_for = Long.parseLong(wait_command);
                        if (wait_for > 0 && wait_for < Long.MAX_VALUE) {
                            instruction = new Instruction(1);
                            instruction.insert(wait_for);
                        }
                    } catch (NumberFormatException e) {
                        Main.getConsoleBuffer() .append("Invalid number format for ").append(line).append("\n")
                                .append("Syntax: wait 1000 \n")
                                .append(e);
                        Main.pushConsoleMessage();
                        return;
                    }
                }
                else if (key_found && command.equalsIgnoreCase("send")) {
                    String send_command = line.substring(4);
                    if (send_command.charAt(0) == ' ') {
                        send_command = send_command.substring(1);
                    }
                    instruction = new Instruction(2);
                    instruction.insert(send_command);

                }
                else if (key_found && line.equalsIgnoreCase("click")) {
                    instruction = new Instruction(3);
                    instruction.insert(1);
                } else if (key_found && line.equalsIgnoreCase("rightclick")) {
                    instruction = new Instruction(3);
                    instruction.insert(2);
                } else if (key_found && line.equalsIgnoreCase("middleclick")) {
                    instruction = new Instruction(3);
                    instruction.insert(3);
                }  else if (key_found && line.equalsIgnoreCase("mouse1down")) {
                    instruction = new Instruction(4);
                    instruction.insert(1);
                } else if (key_found && line.equalsIgnoreCase("mouse1up")) {
                    instruction = new Instruction(5);
                    instruction.insert(1);
                } else if (key_found && line.equalsIgnoreCase("mouse2down")) {
                    instruction = new Instruction(4);
                    instruction.insert(2);
                } else if (key_found && line.equalsIgnoreCase("mouse2up")) {
                    instruction = new Instruction(5);
                    instruction.insert(2);
                } else if (key_found && line.equalsIgnoreCase("mouse3down")) {
                    instruction = new Instruction(4);
                    instruction.insert(3);
                } else if (key_found && line.equalsIgnoreCase("mouse3up")) {
                    instruction = new Instruction(5);
                    instruction.insert(3);
                } else if (key_found && line.equalsIgnoreCase("reload")) {
                    instruction = new Instruction(7);
                } else if (key_found && line.equalsIgnoreCase("get scripts")) {
                    instruction = new Instruction(9);
                } else if (key_found && line.equalsIgnoreCase("get mousepos")) {
                    instruction = new Instruction(10);
                } else if (key_found && line.equalsIgnoreCase("get window")) {
                    instruction = new Instruction(11);
                } else if (key_found && line.equalsIgnoreCase("get memory")) {
                    instruction = new Instruction(8);
                }
                else if (key_found && command.equalsIgnoreCase("move")) {
                    String move_command = line.substring(4);

                    move_command = move_command.replaceAll("[a-zA-Z]", ""); // Remove non-numeric characters except commas
                    String[] coordinates = move_command.split(","); // Split the string by comma

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
                            }
                            if (x > 0 && x < 65535 && y > 0 && y < 65535) {
                                instruction = new Instruction(6);
                                instruction.insert(x);
                                instruction.insert(y);
                                instruction.insert(delay);
                            }
                        } catch (NumberFormatException e) {
                            Main.getConsoleBuffer() .append("Invalid number format for ").append(line).append("\n")
                                    .append("Syntax: move 500,500 \n")
                                    .append(e);
                            Main.pushConsoleMessage();
                            return;
                        }
                    }
                }
                else if (key_found && command.equalsIgnoreCase("loop")) {
                    instructionSet.loop = true;
                }
                else if (key_found && command.equalsIgnoreCase("wind")) {
                    String title = line.substring(6);
                    title = title.replaceAll(" ", "");
                    instructionSet.windowTitle = title.toLowerCase();
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
                instructionSet.scriptPath = file.getPath();
                Main.getConsoleBuffer().append("File: ").append(file).append(" has been accepted. keybind:").append(key).append(" loop:").append(instructionSet.loop).append(" window:").append(instructionSet.windowTitle);
                Main.pushConsoleMessage();
                InstructionSetContainer.getInstance().insert(instructionSet);
            }
        }
    }
}