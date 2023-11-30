import com.phukka.macro.devices.keyboard.KeyListener
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.devices.mouse.MouseCallback
import com.phukka.macro.scripting.Scripts
import com.phukka.macro.scripting.runescape.grandexchange.GrandExchange
import com.phukka.macro.scripting.runescape.metrics.Profile
import com.phukka.macro.scripting.runescape.metrics.RuneMetrics
import com.phukka.macro.util.Window
import java.text.DecimalFormat;

class RuneScript {

    def script

    RuneScript(def script) {
        this.script = script
    }

    def keyListener = KeyListener.newKeyListener()

    def BUTTON_TAB = 9
    def F9 = 120
    def MOUSE_SIDE_BUTTON_FORWARDS = 134
    def MOUSE_SIDE_BUTTON_BACKWARDS = 135

    def magicGear = Scripts.get("MagicGear").newInstance()
    def rangeGear = Scripts.get("RangeGear").newInstance()
    def meleeGear = Scripts.get("MeleeGear").newInstance()

    long lastPressed = 0
    long lastDeto = 0

    String rsclient = "runescape"

    void start() {

        println "--------< RuneScript started >--------"

        while (script.running) {

            try {

                /**
                 * check if runescape is active window
                 */
                if (rsclient != Window.getActive() && client_is_focused) {
                    Thread.sleep(500)
                    continue
                }

                /**
                 * read from listener
                 */
                pressed = keyListener.getPressed()
                released = keyListener.getReleased()



                if (pressed != 0) {

                    switch (pressed) {

                        case F9:
                            //String username = "sick phukka"
                            //println("rune-metrics: requesting lookup on username "+ username)

                            //Profile profile = RuneMetrics.lookupUsername(username);
                            //GrandExchange.downloadGEPrices();
                            //GrandExchange.downloadGETradeVolumes();
                            GrandExchange.lookupItemPrice("party");
                            break

                        case 89: //detonate - fixes target cycle smoke clouding
                            lastDeto = System.currentTimeMillis()
                            break

                        case BUTTON_TAB:
                            println("mousex = "+ MouseCallback.x + " mousey = " + MouseCallback.y)
                            if (System.currentTimeMillis() - lastDeto > 10000) {
                                Thread.sleep(20)
                                KeyboardEvent.send("/")
                            }
                            break

                        case MOUSE_SIDE_BUTTON_FORWARDS:
                            rangeGear.weaponCycle()
                            rangeGear.wearSirenic()

                            /**
                             * melee

                            if (!meleeGear.wearingMeleeArmor()) {
                                meleeGear.wearMelee()
                            }
                            if (System.currentTimeMillis() - lastPressed < 200) {
                                meleeGear.wear2H()
                            } else {
                                meleeGear.weaponCycle()
                            }
                            lastPressed = System.currentTimeMillis()
                                    */
                            break

                        case MOUSE_SIDE_BUTTON_BACKWARDS:
                            magicGear.wearTectonic()

                            if (System.currentTimeMillis() - lastPressed < 200) {
                                magicGear.wearMagic2H()
                            } else {
                                magicGear.weaponCycle()
                            }
                            lastPressed = System.currentTimeMillis()
                            break

                        default:
                            if (debug) {
                                println "pressed: " + pressed
                            }
                            break
                    }
                }

                if (released != 0) {
                    switch (released) {
                        default:
                            if (debug) {
                                println "released: " + released
                            }
                            break
                    }
                }
                Thread.sleep(10)

            } catch (Exception e) {
                println("Exception: " + e)
            }
        }
    }
    int pressed = 0
    int released = 0

    boolean debug = false
    boolean client_is_focused = false
}


println "--------< RuneScript starting >--------"

RuneScript runeScript = new RuneScript(this)

try {
    runeScript.start()
} catch (Exception e) {
    println("Exception: " + e)
}

println "--------< RuneScript ended >--------"
