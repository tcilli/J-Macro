package com.phukka.macro.scripting;

import com.phukka.macro.Main;
import com.phukka.macro.devices.keyboard.KeyListener;
import com.phukka.macro.devices.keyboard.keyListenerInterface;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Scripts {

    private final Map<String, Script> scripts = new HashMap<>();

    public void clear() {
        scripts.clear();
    }

    public void compile() {
        try {
            File scriptsDir = new File("./data/scripts");
            String SCRIPTS_DIR = "./data/scripts";
            if (!scriptsDir.exists()) {
                throw new FileNotFoundException("Scripts directory not found: " + SCRIPTS_DIR);
            }
            File[] scriptFiles = scriptsDir.listFiles();
            if (scriptFiles == null) {
                throw new FileNotFoundException("No scripts found in directory: " + SCRIPTS_DIR);
            }
            GroovyShell shell = new GroovyShell();

            for (File scriptFile : scriptFiles) {
                System.out.println("Compiling script: " + scriptFile.getName());
                Script script = shell.parse(scriptFile);
                onCompile(script);
                scripts.put(scriptFile.getName(), script);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String scriptName) {

        Script script = scripts.get(scriptName + ".groovy");

        if (script != null) {

            if ((boolean) script.getProperty("running")) {
                return;
            }
            Main.getExecutor().submit(() -> {

                prepare(script);

                script.run();

                end(script);
            });

        } else {
            System.out.println("Script not found: " + scriptName);
        }
    }

    public void stop() {
        for (Script script : scripts.values()) {
            end(script);
        }
    }

    /**
     * This method is called when a script is compiled.
     * It is used to set the default properties of the script.
     * @param script The script that was compiled.
     */
    private void onCompile(Script script) {
        script.setProperty("keyListener", null);
        script.setProperty("running", false);
    }

    /**
     * This method is called when a script is about to be run.
     * It is used to set the default properties of the script.
     * @param script The script that was run.
     */
    private void prepare(Script script) {
        script.setProperty("keyListener", null);
        script.setProperty("running", true);
    }

    /**
     * This method is called when a script has stopped.
     * It is used to clean up after the script has finished.
     * @param script The script that was run.
     */
    private void end(Script script) {
        script.setProperty("running", false);
        if (script.getProperty("keyListener") != null) {
            KeyListener.removeListener((keyListenerInterface) script.getProperty("keyListener"));
            script.setProperty("keyListener", null);
        }
    }
}
