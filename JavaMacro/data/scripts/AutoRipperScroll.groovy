import com.phukka.macro.Main
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.scripting.Scripts
import com.phukka.macro.util.Window

class AutoRipperScroll {

    def item = Scripts.get("Item").newInstance()

    def start() {

        println "--------< AutoRipper started >--------"
        def count = 0;
        keepAlive = true
        Main.getExecutor().execute(()-> {

            while(keepAlive) {
                specialAttack()
                Thread.sleep(2000L)
            }
        })
    }

    def specialAttack() {
        if (Window.getActive() == "runescape") {
            if (!item.isEquipped("full")) {
                if (!item.isEquipped("lock")) {
                    KeyboardEvent.sendKeycode(107)
                }
            }
        }
    }

    def stop() {
        keepAlive = false
        println "--------< AutoRipper ended >--------"
    }

    def keepAlive = false
}