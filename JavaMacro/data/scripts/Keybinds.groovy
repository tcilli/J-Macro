import com.phukka.macro.devices.keyboard.KeyboardEvent

class Keybinds {

    static def mainhand_1 = "o"
    static def offhand_1 =  "p"
    static def amulet_1 = "j"
    static def helmet_1 = "["
    static def chest_1 = "]"
    static def legs_1 = ";"
    static def boots_1 = "\\"

    static def mainhand_2 = "7"
    static def offhand_2 = "8"
    static def amulet_2 = "9"
    static def helmet_2 = "45"  //<- keycode
    static def chest_2 = "36"   //<- keycode
    static def legs_2 = "33"    //<- keycode
    static def boots_2 = "46"   //<- keycode

    static def wear_weapons_1() {
        KeyboardEvent.send(mainhand_1)
        KeyboardEvent.send(offhand_1)
    }

    static def wear_weapon_2h_1() {
        KeyboardEvent.send(",")
    }

    static def wear_weapons_2() {
        KeyboardEvent.send(mainhand_2)
        KeyboardEvent.send(offhand_2)
    }

    static def wear_armour_1() {
        KeyboardEvent.send(amulet_1)
        KeyboardEvent.send(helmet_1)
        KeyboardEvent.send(chest_1)
        KeyboardEvent.send(legs_1)
        KeyboardEvent.send(boots_1)
    }

    static def wear_armour_2() {
        KeyboardEvent.send(amulet_2)
        KeyboardEvent.sendKeycode(helmet_2 as int)
        KeyboardEvent.sendKeycode(chest_2 as int)
        KeyboardEvent.sendKeycode(legs_2 as int)
        KeyboardEvent.sendKeycode(boots_2 as int)
    }

    static def magic_prayer() {
        KeyboardEvent.send("k")
    }
    static def melee_prayer() {
        KeyboardEvent.send("6")
    }
    static def ranged_prayer() {
        KeyboardEvent.send("l")
    }

}
