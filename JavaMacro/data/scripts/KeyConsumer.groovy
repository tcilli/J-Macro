import com.phukka.macro.util.ConsumableKeyMap

/**
 * Allows a script to add and remove keys from the ConsumableKeyMap.
 * The reason why this would be used is so when your script is done,
 * it can remove all the keys that the script added to the ConsumableKeyMap.
 * So yes, It's important to use clear() when you're done with the script.
 */
class KeyConsumer {

    def keyArray = []

    def add(int key) {
        keyArray.add(key)
        ConsumableKeyMap.addKey(key)
    }

    def remove(int key) {
        keyArray.remove(key)
        ConsumableKeyMap.removeKey(key)
    }

    def clear() {
        for (int key : keyArray) {
            ConsumableKeyMap.removeKey(key)
        }
        keyArray.clear()
    }
}

