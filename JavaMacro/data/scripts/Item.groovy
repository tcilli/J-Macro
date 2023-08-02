import com.phukka.macro.devices.screen.ImagePosition
import com.phukka.macro.devices.screen.ImageRepository
import com.phukka.macro.devices.screen.ImageSearch
import com.phukka.macro.devices.screen.Screen

import java.awt.image.BufferedImage

class Item {

    static boolean debug = true

    static void sendMessage(String message) {
        if (debug) {
            println message
        }
    }

    static boolean isEquipped(String itemName)
    {
        isEquipped(itemName, 60)
    }

    static boolean isEquipped(String itemName, int variance)
    {
        try {
            int[] itemPosition = getItemPosition(itemName)

            itemPosition[0] = itemPosition[0] - 5 //x1
            itemPosition[1] = itemPosition[1] - 5 //y1
            itemPosition[2] = itemPosition[2] + 5
            itemPosition[3] = itemPosition[3] + 5

            int[] p = ImageSearch.find(Screen.captureArea(itemPosition), getItemByName(itemName))
            if (p[0] != -1) {
                //sendMessage("Wearing " + itemName)
                return true
            } else {
                sendMessage("Not wearing " + itemName)
                return false
            }
        } catch (Exception e) {
            sendMessage("Error: " + e)
            return false
        }
    }

    static BufferedImage getItemByName(String itemName) {
        return ImageRepository.get(itemName)
    }

    static int[] getItemPosition(String itemName) {
        switch(itemName) {
            case "bow_of_the_last_guardian_ice":
                return [1183, 883, 1210, 908]
            case "elite_sirenic_mask_ice":
                return [1229, 799, 1259, 831]
            case "elite_sirenic_hauberk_ice":
                return [1228, 881, 1256, 908]
            case "elite_sirenic_legs_ice":
                return [1234, 919, 1253, 950]
            case "enhanced_fleeting_boots":
                return [1227, 966, 1255, 984]
            case "eof_seren_god_bow":

                /**
                 * magic gear
                 */
                return [1228, 839, 1257, 868]
            case "elite_tectonic_mask_shadow":
                return [1231, 801, 1256, 831]
            case "elite_tectonic_robe_top_shadow":
                return [1228, 881, 1258, 907]
            case "elite_tectonic_robe_bottoms_shadow":
                return [1234, 919, 1253, 950]
            case "enhanced_blast_diffusion_boots":
                return [1227, 966, 1255, 984]
            case "eof_armadyl_battle_staff":
                return [1228, 839, 1257, 868]
            case "wand_of_the_praesul_ice":
                return [1181, 879, 1212, 910]
            case "imperium_core_ice":
                return [1275, 884, 1300, 908]
            case "fractured_staff_of_armadyl_ice":
                return [1181, 879, 1214, 910]

                /**
                 * damage buff prayers
                 */
            case "affliction_prayer": //magic
                return [1431, 596, 1462, 628]
            case "desolation_prayer": //range
                return [1431, 666, 1462, 698]
            case "malevolence_prayer": //melee
                return [1431, 666, 1462, 698]

        }
        println "Item not found: " + itemName
        return [-1,-1, -1, -1]
    }
}
