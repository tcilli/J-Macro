package com.phukka.macro.scripting;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class Scripts {

    private final Map<String, Script> scripts = new HashMap<>();

    private final String SCRIPTS_DIR = "./data/scripts";

    public void clear() {
        scripts.clear();
    }

    public void compile() {
        try {
            File scriptsDir = new File(SCRIPTS_DIR);
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
                scripts.put(scriptFile.getName(), script);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run(String scriptName) {
        Script script = scripts.get(scriptName + ".groovy");
        if (script != null) {
            script.run();
        } else {
            System.out.println("Script not found: " + scriptName);
        }
    }
}
