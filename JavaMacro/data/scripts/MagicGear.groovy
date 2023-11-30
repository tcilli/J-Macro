import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.scripting.Scripts

class MagicGear {

    def item = Scripts.get("Item").newInstance()

    def weaponCycle() {

        if (item.isEquipped("wand_of_the_praesul_ice") && item.isEquipped("imperium_core_ice")) {
            wearMagic2H()
        } else {
            wearDualWield()
        }
        if (!item.isEquipped("affliction_prayer")) {
            KeyboardEvent.send("k")
        }
    }

    def usingMagicWeapon() {
        return item.isEquipped("wand_of_the_praesul_ice") || item.isEquipped("imperium_core_ice") || item.isEquipped("fractured_staff_of_armadyl_ice")
    }

    def wearDualWield() {
        if (!item.isEquipped("wand_of_the_praesul_ice")) {
            KeyboardEvent.send("7")
        }
        if (!item.isEquipped("imperium_core_ice")) {
            KeyboardEvent.send("8")
        }
    }

    def wearMagic2H() {
       // if (!item.isEquipped("fractured_staff_of_armadyl_ice")) {
            KeyboardEvent.send("'")
       // }
    }

    def wearTectonic() {
        KeyboardEvent.sendKeycode(insert)
        KeyboardEvent.sendKeycode(home)
        KeyboardEvent.sendKeycode(page_up)
        KeyboardEvent.sendKeycode(del)
        KeyboardEvent.send("9")
    }

    /**
     * KeyCodes
     */
    def insert = 45 //helmet slot
    def home = 36   //chest slot
    def page_up = 33   //legs slot
    def del = 46    //boots slot
}
