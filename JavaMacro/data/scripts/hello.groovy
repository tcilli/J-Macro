import com.phukka.macro.Main
import com.phukka.macro.devices.keyboard.KeyboardEvent
import com.phukka.macro.devices.mouse.MouseEvent
import com.phukka.macro.devices.screen.*


import java.awt.image.BufferedImage


BufferedImage overload = Main.getImageRepository().get("overload_vid");


// Capture an area of the screen
//ImagePosition area = Main.getScreen().captureArea(1781, 1046, 1798, 1066)
//ImagePosition area = Main.getScreen().captureArea(1232, 817, 1252, 843)//helmet
ImagePosition area = Main.getScreen().capture()

//display the captured area on 2nd screen
Main.getScreen().display(overload, 1);


long start = System.currentTimeMillis()

// Search for the image in the area and gets its position (centered)
int[] pos = ImageSearch.findCenter(area, overload)


println "Time: " + (System.currentTimeMillis() - start)

if (pos[0] == -1) {
    println "Not found"
    return
} else {
    // Add the offset of the captured area to the found position
    println "Found at: x=" + (pos[0])+ ", y=" + (pos[1])
}
println pos.join(", ")
MouseEvent.move(pos[0], pos[1], 100, true)
