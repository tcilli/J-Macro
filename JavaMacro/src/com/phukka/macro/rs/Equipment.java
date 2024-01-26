package com.phukka.macro.rs;

import com.phukka.macro.Main;
import com.phukka.macro.devices.screen.ImagePosition;
import com.phukka.macro.devices.screen.ImageSearch;
import com.phukka.macro.devices.screen.Screen;

public class Equipment {

    public static boolean isWorn(ImagePosition searchArea, String name) {
        return ImageSearch.find(searchArea, Main.getImageRepository().get(name)) != null;
    }

    public static ImagePosition getImagePositionInventory() {
        return Screen.captureArea(REGION_WORN_ITEMS);
    }

    public static ImagePosition getImagePositionPrayerBinds() {
        return Screen.captureArea(REGION_PRAYER_BINDINGS);
    }


    /**
     * The area of where the prayer bindings are located
     */
    final static int[] REGION_PRAYER_BINDINGS = { 1691, 490, 1733, 626 };

    /**
     * The area of where the equipment is located
     */
    final static int[] REGION_WORN_ITEMS = { 1164, 788, 1320, 993 };
}
