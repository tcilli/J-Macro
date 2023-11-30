import com.phukka.macro.Main
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.util.Window

class AutoKalg {

    def count = 0

    def start() {

        println "--------< AutoKalg started >--------"
        def count = 0;
        keepAlive = true
        Main.getExecutor().execute(()-> {

            while(keepAlive) {
                specialAttack()
                Thread.sleep(30000L)
            }
        })
    }

    def recall() {
        //if (Window.getActive() == "runescape")
         //   KeyboardEvent.send(recallKeyBind)
    }

    def specialAttack() {
        if (Window.getActive() == "runescape")
            KeyboardEvent.send(specialKeybind)
    }

    def stop() {
        keepAlive = false
        println "--------< AutoKalg ended >--------"
    }

    def specialKeybind = "."
    def recallKeyBind = ","
    def keepAlive = false
}