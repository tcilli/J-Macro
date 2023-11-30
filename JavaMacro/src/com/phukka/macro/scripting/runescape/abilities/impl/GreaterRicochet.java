package com.phukka.macro.scripting.runescape.abilities.impl;

import com.phukka.macro.scripting.runescape.abilities.Ability;

import java.util.ArrayList;
import java.util.List;

public class GreaterRicochet extends Ability {

    public static List<int[]> damage_range = new ArrayList<>();

    static {
        damage_range.add(new int[] {20 , 100});
        damage_range.add(new int[] {10 , 50});
        damage_range.add(new int[] {15 , 15});
        damage_range.add(new int[] {5 , 15});
        damage_range.add(new int[] {5 , 15});
        damage_range.add(new int[] {5 , 15});
        damage_range.add(new int[] {5 , 15});
    }

    public GreaterRicochet() {
        super("Greater Ricochet", true, false, false, damage_range, 7 , 10.2 , false , false , 0 , "q");
    }

}
