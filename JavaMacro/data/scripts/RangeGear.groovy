import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.devices.screen.ImagePosition
import com.phukka.macro.devices.screen.ImageRepository
import com.phukka.macro.devices.screen.ImageSearch
import com.phukka.macro.devices.screen.Screen
import com.phukka.macro.scripting.Scripts

class RangeGear {

    def item = Scripts.get("Item").newInstance()

    def weaponCycle() {
        wearRange2H()
    }

    def wearDualWield() {
    }

    def wearRange2H() {

        if (!item.isEquipped("bow_of_the_last_guardian_ice")) {
            KeyboardEvent.send("p")
        }
        if (!item.isEquipped("desolation_prayer")) {
            KeyboardEvent.send("6")
        }
    }

    def usingRangeWeapon() {
        if (item.isEquipped("bow_of_the_last_guardian_ice")) {
            return true
        }
        if (item.isEquipped("ecb_ice")) {
            return true
        }
    }

    def wearSirenic() {
        KeyboardEvent.send("j")
        KeyboardEvent.send("[")
        KeyboardEvent.send("]")
        KeyboardEvent.send(";")
        KeyboardEvent.send("o")
        KeyboardEvent.send('\\')
    }
}
