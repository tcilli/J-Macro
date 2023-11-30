
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.scripting.Scripts

class MeleeGear {

    def item = Scripts.get("Item").newInstance()
    def equipment = Scripts.get("Equipment").newInstance()

    def weaponCycle() {

        if (item.isEquipped("lengMH") && item.isEquipped("lengOH")) {
            wear2H()
        } else {
            wearDualWield()
        }
        if (!item.isEquipped("malevolence_prayer")) {
            KeyboardEvent.send("6")
        }
    }

    def usingMeleeWeapon() {
        return item.isEquipped("lengMH") && item.isEquipped("lengOH") || item.isEquipped("ezk") || item.isEquipped("spear")
    }

    def wearingMeleeArmor() {
        if (equipment.wearing("vestmentBottom")) {
            println "wearing vestmentBottom"
            return true
        }
        println "wasnt wearing vestmentBottom"
        return false;
    }

    def wearDualWield() {
        if (!item.isEquipped("lengMH")) {
            KeyboardEvent.send("o")
        }
        if (!item.isEquipped("lengOH")) {
            KeyboardEvent.send("p")
        }
    }

    def wear2H() {
        KeyboardEvent.send(",")

    }

    def wearMelee() {
        KeyboardEvent.send("j")
        KeyboardEvent.send("[")
        KeyboardEvent.send("]")
        KeyboardEvent.send(";")
        KeyboardEvent.send('\\')
    }
}
