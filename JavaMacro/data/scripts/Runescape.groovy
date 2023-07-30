import com.phukka.macro.devices.keyboard.KeyListener
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.util.ConsumableKeyMap

println "Runescape.groovy started"


keyListener = KeyListener.newKeyListener()


int tab = 9

ConsumableKeyMap.addKey(tab)

while (running) {
    try {

        int pressed = keyListener.getPressed()

        if (pressed != 0) {

            switch(pressed) {
                case tab:
                    KeyboardEvent.send("/")
                    break
                default:
                    println "pressed: " + pressed
                    break
            }
           println "Pressed: "+ pressed
        }

        int released = keyListener.getReleased()

        if (released != 0) {
            println "released: " + released
        }

        Thread.sleep(10)

    } catch (Exception e) {
        println("Exception: " + e)
        break
    }
}

ConsumableKeyMap.removeKey(tab)

println "Runescape.groovy finished"

