import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.scripting.Scripts

class MagicGear {

    def item = Scripts.get("Item").newInstance()

    def weaponCycle() {
        if (item.isEquipped("fractured_staff_of_armadyl_ice")) {
            wearDualWield()
        } else {
            wearMagic2H()
        }
        if (!item.isEquipped("affliction_prayer")) {
            KeyboardEvent.send("k")
        }
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
        if (!item.isEquipped("fractured_staff_of_armadyl_ice")) {
            KeyboardEvent.send("'")
        }
    }

    def wearTectonic() {
        if (!item.isEquipped("elite_tectonic_mask_shadow", 100)) {
            KeyboardEvent.sendKeycode(insert)
        }
        if (!item.isEquipped("elite_tectonic_robe_top_shadow", 100)) {
            KeyboardEvent.sendKeycode(home)
        }
        if (!item.isEquipped("elite_tectonic_robe_bottoms_shadow", 100)) {
            KeyboardEvent.sendKeycode(page_up)
        }
        if (!item.isEquipped("enhanced_blast_diffusion_boots")) {
            KeyboardEvent.sendKeycode(del)
        }
        if (!item.isEquipped("eof_armadyl_battle_staff")) {
            KeyboardEvent.send("9")
        }
    }

    /**
     * KeyCodes
     */
    def insert = 45 //helmet slot
    def home = 36   //chest slot
    def page_up = 33   //legs slot
    def del = 46    //boots slot
}
