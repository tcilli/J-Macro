package com.phukka.macro.scripting;

import com.phukka.macro.Main;
import com.phukka.macro.devices.Keyboard;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class Scripts {

    public static final String PATH = "./data/scripts/";
    public static final String SCRIPT_EXTENSION = ".groovy";
    private Keyboard keyboard = Main.getKeyboard();
    private static final String KEY_LISTENER = "keyListener";
    private static final String SCRIPT_RUNNING = "running";


    private final Map<String, Script> scripts = new HashMap<>();

    public void clear() {
        scripts.clear();
    }

    public void compile() {
        try {
            File scriptsDir = new File(PATH);
            if (!scriptsDir.exists()) {
                throw new FileNotFoundException("Scripts directory not found: " + PATH);
            }
            Main.getConsoleBuffer().append("Script directory: ").append(PATH);
            Main.pushConsoleMessageAlways();

            File[] scriptFiles = scriptsDir.listFiles();
            if (scriptFiles == null) {
                throw new FileNotFoundException("No scripts found in directory: " + PATH);
            }
            GroovyShell shell = new GroovyShell();

            for (File scriptFile : scriptFiles) {
                if (scriptFile.isDirectory() || !scriptFile.getName().toLowerCase().endsWith(SCRIPT_EXTENSION)) {
                    continue; // Skips directories and files not ending with .groovy
                }
                Main.getConsoleBuffer().append("Compiling script: ").append(scriptFile.getName());
                Main.pushConsoleMessageAlways();

                Script script = shell.parse(scriptFile);
                onCompile(script);
                scripts.put(scriptFile.getName(), script);
            }
            Main.getConsoleBuffer().append("Compiled ").append(scripts.size()).append(" scripts.");
            Main.pushConsoleMessageAlways();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String scriptName) {

        if (!scriptName.endsWith(SCRIPT_EXTENSION)) {
            scriptName = scriptName + SCRIPT_EXTENSION;
        }

        Script script = scripts.get(scriptName);

        if (script != null) {

            if ((boolean) script.getProperty(SCRIPT_RUNNING)) {
                Main.getConsoleBuffer().append("Script already running: ").append(scriptName);
                Main.pushConsoleMessageAlways();
                return;
            }
            Main.getExecutor().submit(() -> {

                prepare(script);

                script.run();

                end(script);
            });

        } else {
            Main.getConsoleBuffer().append("Script not found: ").append(scriptName);
            Main.pushConsoleMessageAlways();
        }
    }

    public void stop() {
        for (Script script : scripts.values()) {
            end(script);
        }
        //keyboard.clear();
    }

    /**
     * This method is called when a script is compiled.
     * It is used to set the default properties of the script.
     * @param script The script that was compiled.
     */
    private void onCompile(Script script) {
        script.setProperty(KEY_LISTENER, null);
        script.setProperty(SCRIPT_RUNNING, false);
    }

    /**
     * This method is called when a script is about to be run.
     * It is used to set the default properties of the script.
     * @param script The script that was run.
     */
    private void prepare(Script script) {
        script.setProperty(KEY_LISTENER, null);
        script.setProperty(SCRIPT_RUNNING, true);
    }

    /**
     * This method is called when a script has stopped.
     * It is used to clean up after the script has finished.
     * @param script The script that was run.
     */
    private void end(Script script) {
        script.setProperty(SCRIPT_RUNNING, false);
        if (script.getProperty(KEY_LISTENER) != null) {
            script.setProperty(KEY_LISTENER, null);
        }
    }

    public static Constructor<?> get(String name) {
        Constructor<?> classConstructor = null;

        try (GroovyClassLoader gcl = new GroovyClassLoader()) {
            File file = new File(PATH + name + SCRIPT_EXTENSION);

            try {
                Class<?> newClass = gcl.parseClass(file);
                classConstructor = newClass.getDeclaredConstructor();

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return classConstructor;
    }
}
