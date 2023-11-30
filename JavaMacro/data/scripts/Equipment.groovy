import com.phukka.macro.devices.screen.ImageRepository
import com.phukka.macro.devices.screen.ImageSearch
import com.phukka.macro.devices.screen.Screen

import java.awt.image.BufferedImage

class Equipment {

    static int[] equipmentArea = [1166, 794, 1319, 994]
    static int[] inventoryArea = [1494, 469, 1667, 723]

    static BufferedImage getItemByName(String itemName) {
        return ImageRepository.get(itemName)
    }

    static boolean wearing(String itemName) {
        return ImageSearch.foundExact(Screen.captureArea(equipmentArea), getItemByName(itemName))
    }

    static boolean has(String itemName) {
        return ImageSearch.find(Screen.captureArea(inventoryArea), getItemByName(itemName))
    }

}
