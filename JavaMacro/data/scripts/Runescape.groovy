import com.phukka.macro.devices.keyboard.KeyListener
import KeyConsumer

/**
 * Start of the Script
 */
println "Runescape.groovy started"

/**
 * Connects this script to the built in keylistener
 * 'keyListener' is a hidden variable that is pre-defined
 * in the Script class. It is used to listen for key presses
 * Calling newKeyListener() will automatically setup a listener
 * When this script ends. The listener will be removed automatically.
 */
keyListener = KeyListener.newKeyListener()

/**
 * Connect to the ConsumableKeyMap
 */
KeyConsumer keyConsumer = new KeyConsumer()




/**
 * Some key code to key name assignments
 * for ease of use
 */
int tab = 9

/**
 * Put some keys to the consume map, Mentioned above.
 */
keyConsumer.add(tab)

/**
 * Holds the key code of the last key pressed/released
 * when checking keyListener.getPressed() or keyListener.getReleased()
 * the value is erased as its called, So if you want to use it
 * you need to store it in a variable...
 */
int pressed = 0
int released = 0

/**
 * Controls printouts to the console
 */
boolean debug = true



/**
 * Main loop
 * 'running' is automatically set to true as any Script is started.
 * it is a hidden variable that is used to stop the script
 */
while (running) {

    try {

        /**
         * read the keyListener for key presses
         */
        pressed = keyListener.getPressed()

        if (pressed != 0) {

            switch (pressed) {
                case tab:
                    //KeyboardEvent.send("/")
                    break
                default:
                    if (debug) {
                        println "pressed: " + pressed
                    }
                    break
            }
        }

        /**
         * read the keyListener for key releases
         */
        released = keyListener.getReleased()

        if (released != 0) {
            switch (released) {
                case tab:
                    break
                default:
                    if (debug) {
                        println "released: " + released
                    }
                    break
            }
        }

        /**
         * Sleeping allows time for the OS to process any information.
         * 10 milliseconds is a good amount of time to sleep for.
         */
        Thread.sleep(10)
    } catch (Exception e) {
        println("Exception: " + e)
        break
    }
}


/**
 * remove the keys that were added to the consume map
 */
keyConsumer.clear()

/**
 * End of Script
 */
println "Runescape.groovy finished"

