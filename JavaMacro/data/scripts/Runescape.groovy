import com.phukka.macro.Main
import com.phukka.macro.devices.keyboard.KeyListener
import KeyConsumer
import com.phukka.macro.devices.mouse.MouseCallback
import com.phukka.macro.devices.mouse.MouseEvent
import com.phukka.macro.devices.screen.ImageSearch

/**
 * Start of the Script
 */
println "--------< Runescape started >--------"

/**
 * Connects this script to the built in keylistener
 * 'keyListener' is a hidden variable that is pre-defined
 * in the Script class. It is used to listen for key presses
 * Calling newKeyListener() will automatically setup a listener
 * When this script ends. The listener will be removed automatically.
 */
keyListener = KeyListener.newKeyListener()

/**
 * Create an instance of the KeyConsumer class
 */
KeyConsumer keyConsumer = new KeyConsumer()

/**
 * Create an instance of the AutoKalg class
 */
AutoKalg kalg = new AutoKalg()

/**
 * Some key code to key name assignments
 * for ease of use
 */
int tab = 9
int f = 102
int fullstop = 46
int comma = 44

/**
 * Put some keys to the consume map, Mentioned above.
 */
keyConsumer.add(f)

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
boolean debug = false


/**
 * Main loop
 * 'running' is automatically set to true as any Script is started.
 * it is a hidden variable that is used to stop the script
 */
while (running) {

    try {

        /**
         * read the keyListener for key pressesf
         */
        pressed = keyListener.getPressed()

        if (pressed != 0) {

            switch (pressed) {
                case f:

                    int x = MouseCallback.x
                    int y = MouseCallback.y
                    println "x: " + x + " y: " + y

                    MouseEvent.move(486, 269);
                    MouseEvent.rightClick();
                    MouseEvent.move(x, y);
                    MouseCallback.disableUserMovement = false
                    break

                case fullstop:
                    kalg.start()
                    break
                case comma:
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
 * Stop the AutoKalg class, If it is running
 */
if (kalg.keepAlive) {
    kalg.stop()
}

/**
 * remove the keys that were added to the consume map
 */
keyConsumer.clear()

/**
 * End of Script
 */
println "--------< Runescape ended >--------"