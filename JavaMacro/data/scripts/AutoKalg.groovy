import com.phukka.macro.Main
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.util.Window

class AutoKalg {

    AutoKalg(def script) {
        this.script = script
    }

    def start() {

        println "--------< AutoKalg started >--------"

        Main.getExecutor().execute(()-> {

            while(script.running && keepAlive) {

                recall()
                Thread.sleep(600L)

                specialAttack()
                Thread.sleep(30000L)
            }
        })
    }

    def recall() {
        if (Window.getActive().contains("Runescape"))
            KeyboardEvent.send(recallKeyBind)
    }

    def specialAttack() {
        if (Window.getActive().contains("Runescape"))
            KeyboardEvent.send(specialKeybind)
    }

    def stop() {
        keepAlive = false
        println "--------< AutoKalg ended >--------"
    }

    def script
    def specialKeybind = "."
    def recallKeyBind = ","
    def keepAlive = false
}