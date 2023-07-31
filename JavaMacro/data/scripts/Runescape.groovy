import com.phukka.macro.devices.keyboard.KeyListener
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.scripting.Scripts;

/**
 * Start of the Script
 */
println "--------< Runescape started >--------"

/**
 * Connects this script to the built in keylistener
 * Calling newKeyListener() will automatically setup a listener
 * This ignores system messages and will only trigger for a user input
 */
keyListener = KeyListener.newKeyListener()

println "keyListener: " + keyListener

/**
 * Create an instance of the AutoKalg class
 */
GroovyShell shell = new GroovyShell()
def autoKalg = shell.parse(new File('./data/scripts/AutoKalg.groovy'))
println "autoKalg: " + autoKalg

/**
 * Some key code to key name assignments
 * for ease of use
 */
int tab = 9
int f = 102
int fullstop = 46
int comma = 44

int capitalD = 68
keyConsumer.add(capitalD)

/**
 * Put some keys to the consume map, Mentioned above.
 */
//keyConsumer.add(capitalD)

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
         * read the keyListener for key pressesf
         */
        pressed = keyListener.getPressed()

        if (pressed != 0) {

            switch (pressed) {
                case capitalD:
                    KeyboardEvent.send("Hello you just pressed shift + d")
                    break
                case fullstop:
                    KeyboardEvent.send("hello")
                    kalg.start()
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