import com.phukka.macro.scripting.Scripts;
import com.phukka.macro.devices.keyboard.KeyListener
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.devices.mouse.MouseEvent

class FortniteScript {

    def script

    FortniteScript(def script) {
        this.script = script
    }

    def keyListener = KeyListener.newKeyListener()
    def keyConsumer = Scripts.get("KeyConsumer").newInstance()

    int resetBuildTrigger = 107 //k
    int editBuildTrigger = 117 //u

    int pressed = 0
    int released = 0

    boolean debug = false

    void start() {

        println "--------< FortniteScript started >--------"

        keyConsumer.add(resetBuildTrigger)
        keyConsumer.add(editBuildTrigger)

        while (script.running) {

            try {

                pressed = keyListener.getPressed()

                if (pressed != 0) {

                    switch (pressed) {

                        case resetBuildTrigger:
                            KeyboardEvent.send("g")
                            Thread.sleep(20)
                            MouseEvent.rightClick()
                            break

                        case editBuildTrigger:
                            KeyboardEvent.send("g")
                            Thread.sleep(20)
                            MouseEvent.leftClickDown()
                            break

                        default:
                            if (debug) {
                                println "pressed: " + pressed
                            }

                    }
                }

                /**
                 * read the keyListener for key releases
                 */
                released = keyListener.getReleased()

                if (released != 0) {
                    switch (released) {

                        case editBuildTrigger:
                            MouseEvent.leftClickUp()
                            break

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

println "--------< FortniteScript starting >--------"

FortniteScript fortnite = new FortniteScript(this)

try {
    fortnite.start()
} catch (Exception e) {
    println("Exception: " + e)
}

println "--------< FortniteScript ended >--------"

