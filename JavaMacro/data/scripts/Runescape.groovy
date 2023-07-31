import com.phukka.macro.devices.keyboard.KeyListener
import com.phukka.macro.scripting.Scripts

println "--------< RuneScript started >--------"

RuneScript runeScript = new RuneScript(this)

try {
    runeScript.start()
} catch (Exception e) {
    println("Exception: " + e)
}

println "--------< RuneScript ended >--------"

class RuneScript {

    def script

    RuneScript(def script) {
        this.script = script
    }

    def keyListener = KeyListener.newKeyListener()

    def autoKalg = Scripts.get("AutoKalg").newInstance()
    def keyConsumer = Scripts.get("KeyConsumer").newInstance()

    void start() {

        boolean debug = false

        autoKalg.start()

        int pressed = 0
        int released = 0

        while (script.running) {

            try {

                pressed = keyListener.getPressed()

                if (pressed != 0) {

                    switch (pressed) {
                        default:
                            if (debug) {
                                println "pressed: " + pressed
                            }

                    }
                }

                released = keyListener.getReleased()

                if (released != 0) {
                    switch (released) {
                        default:
                            if (debug) {
                                println "released: " + released
                            }
                    }
                }
                Thread.sleep(10)

            } catch (Exception e) {
                println("Exception: " + e)
            }
        }
        keyConsumer.clear()
    }
}