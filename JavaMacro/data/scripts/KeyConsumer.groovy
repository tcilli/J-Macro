import com.phukka.macro.util.ConsumableKeyMap

/**
 * These keys are a set of low level keyboard codes
 * that will be intercepted and ignored before reaching
 * any application on the OS
 */
class KeyConsumer {

    def keyArray = []

    def add(int key) {
        keyArray.add(key)
        ConsumableKeyMap.addKey(key)
        println "Added keycode: $key to the ConsumableKeyMap"
    }

    def remove(int key) {
        keyArray.remove(key)
        ConsumableKeyMap.removeKey(key)
        println "Removed keycode: $key from the ConsumableKeyMap"
    }

    def clear() {
        for (int key : keyArray) {
            ConsumableKeyMap.removeKey(key)
        }
        keyArray.clear()
        println "Cleared keycodes from ConsumableKeyMap"
    }
}
