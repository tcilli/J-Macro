import com.phukka.macro.devices.screen.ImagePosition
import com.phukka.macro.devices.screen.ImageRepository
import com.phukka.macro.devices.screen.ImageSearch
import com.phukka.macro.devices.screen.Screen

import java.awt.image.BufferedImage

class Item {

    static boolean debug = true

    static def prayer_area = [1691, 490, 1733, 626]

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

            long start = System.currentTimeMillis()
            System.out.println("startTime: " + start)
            int[] p = ImageSearch.find(Screen.captureArea(itemPosition), getItemByName(itemName))
            long end = System.currentTimeMillis()
            System.out.println("took: " + (end - start) + " ms")
            return p[0] != -1
        } catch (Exception e) {
            sendMessage("Error: " + e)
            return false
        }
    }

    static BufferedImage getItemByName(String itemName) {
        return ImageRepository.get(itemName)
    }

    static int[] equipmentArea = [1164, 788, 1319, 993]

    static int[] getItemPosition(String itemName) {

        switch(itemName) {

            /**
             * range gear
             */
            case "bow_of_the_last_guardian_ice":
                return [1183, 883, 1210, 908]

            case "elite_sirenic_mask_ice":
                return [1229, 799, 1259, 831]

            case "elite_sirenic_hauberk_ice":
                return [1228, 881, 1256, 908]

            case "elite_sirenic_hauberk_ice_20":
                return [1228, 881, 1256, 908]

            case "elite_sirenic_legs_ice":
                return [1234, 919, 1253, 950]

            case "enhanced_fleeting_boots":
                return [1227, 966, 1255, 984]

            case "eof_seren_god_bow":
                return [1228, 839, 1257, 868]

            case "ecb_ice":
                return [1181, 879, 1212, 910]

            /**
             * magic gear
             */
            case "elite_tectonic_mask_shadow":
                return [1231, 801, 1256, 831]

            case "elite_tectonic_mask_shadow_25":
                return [1231, 801, 1256, 831]

            case "elite_tectonic_robe_top_shadow":
                return [1228, 881, 1258, 907]

            case "elite_tectonic_robe_bottoms_shadow":
                return [1234, 919, 1253, 950]

            case "elite_tectonic_robe_bottoms_shadow_50":
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
                return prayer_area

            case "desolation_prayer": //range
                return prayer_area

            case "malevolence_prayer": //melee
                return prayer_area

            /**
             * melee gear
             */
            case "vestmentTop":
                return [1228, 881, 1258, 907]
            case "vestmentB3ottom":
                return [1234, 919, 1253, 950]

            case "lengOH":
                return [1280, 885, 1303, 911]

            case "lengMH":
                return [1181, 879, 1212, 910]

            case "spear":
                return [1181, 879, 1212, 910]

            case "ezk":
                return [1181, 879, 1212, 910]

        }
        println("item bounds not specified: " + itemName + " returning default area: " + equipmentArea)
        return equipmentArea
    }
}
