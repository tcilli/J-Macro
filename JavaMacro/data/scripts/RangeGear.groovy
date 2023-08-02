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

    def wearSirenic() {
        if (!item.isEquipped("elite_sirenic_mask_ice")) {
            KeyboardEvent.send("j")
        }
        if (!item.isEquipped("elite_sirenic_hauberk_ice")) {
            KeyboardEvent.send("[")
        }
        if (!item.isEquipped("elite_sirenic_legs_ice")) {
            KeyboardEvent.send("]")
        }
        if (!item.isEquipped("eof_seren_god_bow")) {
            KeyboardEvent.send(";")
        }
        if (!item.isEquipped("enhanced_fleeting_boots")) {
            KeyboardEvent.send('\\')
        }
    }
}
