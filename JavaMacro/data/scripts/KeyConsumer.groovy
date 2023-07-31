import com.phukka.macro.util.ConsumableKeyMap

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

