package com.phukka.macro.util;

import com.phukka.macro.Main;
import com.phukka.macro.scripting.Scripts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ConsoleReader {

    public Scanner scanner = new Scanner(System.in); // Create a Scanner object
    public boolean keepAlive = true;

    public void setup() {

        while (keepAlive) {
            Main.getConsoleBuffer().append("Enter script name to run (or 'exit' to quit): ");
            Main.pushConsoleMessageAlways();

            String input = scanner.nextLine(); // Read a line of text

            if (input.equalsIgnoreCase("exit")) {
                System.exit(0);
                break; // Exit the loop if 'exit' is entered
            }
            runScript(input);
        }
        scanner.close();
    }

    public void runScript(String scriptName) {
        if (!scriptName.endsWith(Scripts.SCRIPT_EXTENSION)) {
            scriptName = scriptName + Scripts.SCRIPT_EXTENSION;
        }

        try {
            File scriptsDir = new File(Scripts.PATH);
            if (!scriptsDir.exists()) {
                throw new FileNotFoundException("Scripts directory not found: " + Scripts.PATH);
            }
            File[] scriptFiles = scriptsDir.listFiles();
            if (scriptFiles == null) {
                throw new FileNotFoundException("No scripts found in directory: " + Scripts.PATH);
            }
            boolean scriptFound = false;
            for (File scriptFile : scriptFiles) {
                if (scriptFile.isDirectory() || !scriptFile.getName().toLowerCase().endsWith(Scripts.SCRIPT_EXTENSION)) {
                    continue; // Skips directories and files not ending with .groovy
                }
                if (scriptFile.getName().equalsIgnoreCase(scriptName)) {
                    scriptFound = true;
                    Main.getConsoleBuffer().append("found script: ").append(scriptFile.getName());
                    Main.pushConsoleMessageAlways();

                    Main.getScript().run(scriptFile.getName());
                }
            }
            if (!scriptFound) {
                Main.getConsoleBuffer().append("Script not found: ").append(scriptName);
                Main.pushConsoleMessageAlways();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        keepAlive = false;
    }
}
