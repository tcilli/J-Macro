package macro.io;

import macro.Main;
import macro.instruction.Data;
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

    public MacroFileReader() throws IOException {
        InstructionSetContainer.getInstance().clearInstructions();
        readFiles(DIR2);
        readFiles(DIR1);
        Main.console.append("Scripts can be stopped by pressing Esc");
        Main.pushConsoleMessage();
        System.gc();
    }

    public void readFiles(String DIR) throws IOException {
        File directory = new File(DIR);

        if (!directory.isDirectory()) {
            Main.console.append("Invalid directory path: ").append(DIR);
            Main.pushConsoleMessage();
            return;
        }
        File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));

        if (files == null || files.length == 0) {
            Main.console.append("No files found in the directory: ").append(DIR);
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
                    Main.console.append("File: ").append(file).append(" line(").append(cur_line).append(") is an invalid instruction: ").append(line);
                    Main.pushConsoleMessage();
                    return;
                }
                String command = line.substring(0,4);

                Instruction instruction = null;

                if (!key_found && line.length() >= 7 && line.substring(0, 6).equalsIgnoreCase("macro ")) {
                    key = line.substring(6);
                    key_found = true;
                    instruction = new Instruction((short) 0);
                    instruction.insert(new Data<>(key));
                    instructionSet.key = key;
                }
                else if (key_found && command.equalsIgnoreCase("wait")) {
                    String wait_command = line.substring(4);
                    wait_command = wait_command.replaceAll("\\D", "");
                    try {
                        long wait_for = Long.parseLong(wait_command);
                        if (wait_for > 0 && wait_for < Long.MAX_VALUE) {
                            instruction = new Instruction((short) 1);
                            instruction.insert(new Data<>(wait_for));
                        }
                    } catch (NumberFormatException e) {
                        Main.console .append("Invalid number format for ").append(line).append("\n")
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
                    instruction = new Instruction((short) 2);
                    for (char c : send_command.toCharArray()) {
                        instruction.insert(new Data<>(KeyMap.getCode(c)));
                    }
                }
                else if (key_found && command.equalsIgnoreCase("hold")) {
                    String hold_command = line.substring(4);
                    if (hold_command.charAt(0) == ' ') {
                        hold_command = hold_command.substring(1);
                    }
                    instruction = new Instruction((short) 3);
                    instruction.insert(new Data<>(KeyMap.keymap.get(hold_command)));
                }
                else if (key_found && command.equalsIgnoreCase("rele")) {
                    String release_command = line.substring(7);
                    if (release_command.charAt(0) == ' ') {
                        release_command = release_command.substring(1);
                    }
                    instruction = new Instruction((short) 4);
                    instruction.insert(new Data<>(KeyMap.keymap.get(release_command)));
                }
                else if (key_found && line.equalsIgnoreCase("click")) {
                    instruction = new Instruction((short) 5);
                } else if (key_found && line.equalsIgnoreCase("rightclick")) {
                    instruction = new Instruction((short) 6);
                } else if (key_found && line.equalsIgnoreCase("middleclick")) {
                    instruction = new Instruction((short) 7);
                }  else if (key_found && line.equalsIgnoreCase("mouse1down")) {
                    instruction = new Instruction((short) 8);
                } else if (key_found && line.equalsIgnoreCase("mouse1up")) {
                    instruction = new Instruction((short) 9);
                } else if (key_found && line.equalsIgnoreCase("mouse2down")) {
                    instruction = new Instruction((short) 10);
                } else if (key_found && line.equalsIgnoreCase("mouse2up")) {
                    instruction = new Instruction((short) 11);
                } else if (key_found && line.equalsIgnoreCase("mouse3down")) {
                    instruction = new Instruction((short) 12);
                } else if (key_found && line.equalsIgnoreCase("mouse3up")) {
                    instruction = new Instruction((short) 13);
                } else if (key_found && line.equalsIgnoreCase("reload")) {
                    instruction = new Instruction((short) 15);
                } else if (key_found && line.equalsIgnoreCase("get scripts")) {
                    instruction = new Instruction((short) 16);
                } else if (key_found && line.equalsIgnoreCase("get mousepos")) {
                    instruction = new Instruction((short) 17);
                } else if (key_found && line.equalsIgnoreCase("get window")) {
                    instruction = new Instruction((short) 18);
                }
                else if (key_found && command.equalsIgnoreCase("move")) {
                    String move_command = line.substring(4);

                    move_command = move_command.replaceAll("[a-zA-Z]", ""); // Remove non-numeric characters except commas
                    String[] coordinates = move_command.split(","); // Split the string by comma

                    if (coordinates.length == 2) {
                        coordinates[0] = coordinates[0].replaceAll("\\D", "");
                        coordinates[1] = coordinates[1].replaceAll("\\D", "");

                        try {
                            int x = Integer.parseInt(coordinates[0]);
                            int y = Integer.parseInt(coordinates[1]);

                            if (x > 0 && x < 65535 && y > 0 && y < 65535) { //0 - 65535 because -> (x * (65535 / screenWidth)) to get the normal
                                instruction = new Instruction((short) 14);
                                instruction.insert(new Data<>(x));
                                instruction.insert(new Data<>(y));
                            }
                        } catch (NumberFormatException e) {
                            Main.console .append("Invalid number format for ").append(line).append("\n")
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
                    Main.console.append("File: ").append(file).append(" line:").append(cur_line).append(" is an invalid instruction: ").append(line);
                    Main.pushConsoleMessage();
                    return;
                }
                if (instruction != null) {
                    instructionSet.insert(instruction);
                }
            }
            if (instructionSet.getInstructions().size() > 0) {
                instructionSet.scriptPath = file.getPath();
                Main.console.append("File: ").append(file).append(" has been accepted. keybind:").append(key).append(" loop:").append(instructionSet.loop).append(" window:").append(instructionSet.windowTitle);
                Main.pushConsoleMessage();
                InstructionSetContainer.getInstance().insert(instructionSet);
            }
        }
    }
}
