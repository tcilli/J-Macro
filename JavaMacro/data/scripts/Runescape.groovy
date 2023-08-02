import com.phukka.macro.devices.keyboard.KeyListener
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.scripting.Scripts
import com.phukka.macro.util.Window

import java.awt.event.KeyEvent


class RuneScript {

    def script

    RuneScript(def script) {
        this.script = script
    }

    def keyListener = KeyListener.newKeyListener()
    def autoKalg = Scripts.get("AutoKalg").newInstance()
    def keyConsumer = Scripts.get("KeyConsumer").newInstance()

    def magicGear = Scripts.get("MagicGear").newInstance()
    def rangeGear = Scripts.get("RangeGear").newInstance()

    void start() {

        println "--------< RuneScript started >--------"

        while (script.running) {

            try {

                pressed = keyListener.getPressed()

                if (pressed != 0) {






                    switch (pressed) {

                        //tab pressed
                        case 9:
                            Thread.sleep(20)
                            KeyboardEvent.send("/")
                            break



                        case 135: //mouse button converted to keystroke
                            String window = Window.getActive()
                            if (window == "runescape") {
                                magicGear.weaponCycle()
                                magicGear.wearTectonic()
                                //magicGear.locateAll()
                            }
                            break

                        //F23 pressed
                        case 134: //mouse button converted to keystroke
                            String window = Window.getActive()
                            if (window == "runescape") {
                                rangeGear.weaponCycle()
                                rangeGear.wearSirenic()
                                //rangeGear.locateAll()
                            }
                            break


                        case 46:
                            if (autoKalg.keepAlive) {
                                autoKalg.stop()
                            } else {
                                autoKalg.start()
                            }
                            break

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
        autoKalg.stop()
    }

    int pressed = 0
    int released = 0

    boolean debug = true
}


println "--------< RuneScript starting >--------"

RuneScript runeScript = new RuneScript(this)

try {
    runeScript.start()
} catch (Exception e) {
    println("Exception: " + e)
}

println "--------< RuneScript ended >--------"
