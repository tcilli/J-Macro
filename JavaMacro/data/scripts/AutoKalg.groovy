import com.phukka.macro.Main
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.util.Window

class AutoKalg {

    String windowTitle = "Runescape"

    String specialKeybind = "."
    String recallKeyBind = ","

    long recastTime = 30000L

    boolean keepAlive = false

    void start() {

        if (keepAlive)
            return

        keepAlive = true

        Main.getExecutor().execute(()-> {

            println "--------< AutoKalg started >--------"

            while(keepAlive) {

                recall()
                Thread.sleep(600)

                specialAttack()
                Thread.sleep(recastTime)
            }
        })
    }

    void recall() {
        if (Window.getActive().contains(windowTitle))
            KeyboardEvent.send(recallKeyBind)
    }

    void specialAttack() {
        if (Window.getActive().contains(windowTitle))
            KeyboardEvent.send(specialKeybind)
    }

    void stop() {
        keepAlive = false
        println "--------< AutoKalg ended >--------"
    }

    void setRecallKeyBind(String key) {
        recallKeyBind = key
        println "AutoKalg recall keybind has been set to $key"
    }

    void setSpecialKeyBind(String key) {
        specialKeybind = key
        println "AutoKalg special keybind has been set to $key"
    }

    void setWindowTitle(String title) {
        windowTitle = title
        println "AutoKalg window title has been set to $title"
    }

    void setRecastSeconds(int time) {
        if (time < 1) {
            println "AutoKalg recast time cannot be less than 1 second."
            println "AutoKalg recast time is ${recastTime/1000} seconds."
            return
        }
        if (time > 60) {
            recastTime = 60000
        } else {
            recastTime = (time * 1000)
        }
        println "AutoKalg recast time has been set to ${recastTime/1000} seconds."
    }

    void getSettings() {
        println "--------< AutoKalg settings >--------"
        println "Recall keybind is $recallKeyBind"
        println "Special keybind is $specialKeybind"
        println "Recast time is ${recastTime/1000} seconds."
        println "Window title check( $windowTitle )"
    }
}