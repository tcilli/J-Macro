package com.phukka.macro.rs;

import com.phukka.macro.Main;
import com.phukka.macro.devices.Keyboard;
import com.phukka.macro.devices.mouse.MouseCallback;
import com.phukka.macro.devices.screen.ImagePosition;

public class Runescape implements Keyboard.Listener {

    private Keyboard keyboard = Main.getKeyboard();

    public Runescape() {
        keyboard.addListener("runescape", this);

    }

    @Override
    public void onKeyPressed(int vkCode)
    {
        long start = System.currentTimeMillis();
        switch(vkCode) {
            case 9 -> {
                keyboard.sendKeycode(191);
            }
            case -122 -> {
                cycle_weapons_1_range();
            }
            case -121 -> {
               cycle_weapons_2_magic();
            }
            case 120 -> {
                keyboard.sendKeycode(116);
            }
            case 121 -> {
                keyboard.sendKeycode(109);
            }
            case 123 -> {
                System.out.println("Mouse x" + MouseCallback.getX() + ", y" + MouseCallback.getY());
            }
        }
        System.out.println("Time taken: " + (System.currentTimeMillis() - start) + "ms, key: " + vkCode);
    }
    @Override
    public void onKeyReleased(int characterCode)
    {
    }

    private void cycle_weapons_2_magic() {

        ImagePosition wornRegion   = Equipment.getImagePositionInventory();
        ImagePosition prayerRegion = Equipment.getImagePositionPrayerBinds();

        boolean sliske      = Equipment.isWorn(wornRegion, "sliske_helmet");
       // boolean tectonic    = Equipment.isWorn(wornRegion, "elite_tectonic_mask_shadow");
        //boolean crypt       = Equipment.isWorn(wornRegion, "crypt_helmet");
        boolean mainhand    = Equipment.isWorn(wornRegion, "wand_of_the_praesul_ice");
        boolean offhand     = Equipment.isWorn(wornRegion, "imperium_core_ice");
        //boolean staff       = Equipment.isWorn(wornRegion, "fractured_staff_of_armadyl_ice");
        boolean prayer      = Equipment.isWorn(prayerRegion, "affliction_prayer");

        if (!sliske) {
            keyboard.sendKeycode( 45, 36, 33, 46, 57, 79);//head, body, legs, feet, necklace, ring
        }
        if (mainhand && offhand) {
            keyboard.sendKeycode(222);//2h

        } else {
            keyboard.sendKeycode(55, 56);//duel wields
        }

        if (!prayer) {
            keyboard.sendKeycode(75);//prayer
        }
    }

    private void cycle_weapons_1_range()
    {
        ImagePosition worn = Equipment.getImagePositionInventory();
        ImagePosition prayerRegion = Equipment.getImagePositionPrayerBinds();

        //boolean sirenic         = Equipment.isWorn(worn,         "elite_sirenic_mask_ice");
        boolean dracolich       = Equipment.isWorn(worn,         "elite_dracolich_coif");
        boolean bolg            = Equipment.isWorn(worn,         "bow_of_the_last_guardian_ice");
        boolean prayer          = Equipment.isWorn(prayerRegion, "desolation_prayer");

       // System.out.println("sirenic: " + sirenic + ", dracolich: " + dracolich + ", bolg: " + bolg + ", prayer: " + prayer);
        if (!dracolich) {
            keyboard.sendKeycode(74, 219, 221, 220, 186, 80);//head, body, legs, feet, necklace, ring

        } else if (dracolich && !bolg) {
            keyboard.sendKeycode(74, 219, 221, 220, 186, 80);//head, body, legs, feet, necklace, ring
        }
        if (!bolg) {
            keyboard.sendKeycode(188);//2h
        } else {
            keyboard.sendKeycode(79, 80);//duel wields
            // keyboard.sendKeycode(79);//o (ecb)
        }
        if (!prayer) {
            keyboard.sendKeycode(54);//range prayer
        }
    }

    private void cycle_weapons_1_melee()
    {
        ImagePosition wornItems = Equipment.getImagePositionInventory();

        boolean spear = Equipment.isWorn(wornItems, "spear");
        boolean mainhand = Equipment.isWorn(wornItems, "lengMH");
        boolean offhand = Equipment.isWorn(wornItems, "lengOH");

        if (!Equipment.isWorn(wornItems, "vestmentBottom")) {
            keyboard.sendKeycode(74, 219, 221, 220, 186);//head, body, legs, feet, necklace
        }
        if (mainhand && offhand) {
            keyboard.sendKeycode(188);//2h
        } else {
            keyboard.sendKeycode(79, 80);//duel wields
        }
        if (!Equipment.isWorn(Equipment.getImagePositionPrayerBinds(), "malevolence_prayer")) {
            keyboard.sendKeycode(76);//melee prayer
        }
    }
}

