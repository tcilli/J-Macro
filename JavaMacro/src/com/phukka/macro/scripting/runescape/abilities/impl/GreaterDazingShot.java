package com.phukka.macro.scripting.runescape.abilities.impl;

import com.phukka.macro.scripting.runescape.abilities.Ability;

import java.util.ArrayList;
import java.util.List;

public class GreaterDazingShot extends Ability {

    public static List<int[]> damage_range = new ArrayList<>();

    static {
        damage_range.add(new int[] {31 , 157});
    }

    public GreaterDazingShot() {
        super("Greater Dazing Shot", true, false, false, damage_range, 1 , 5.4 , false , true , 0 , "w");
    }

}
