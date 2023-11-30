package com.phukka.macro.scripting.runescape.abilities;

import java.util.Arrays;
import java.util.List;

public class Ability {

    String name;

    boolean isBasic;
    boolean isThreshold;
    boolean isUltimate;

    //store min and max damage percents of each hit
    List<int[]> damageRange;

    int numberOfHits;

    double cooldown;

    boolean isDualWield;
    boolean isTwoHanded;

    int adrenaline_cost;

    String keybind;


    public Ability(String name , boolean isBasic , boolean isThreshold , boolean isUltimate , List<int[]> damageRange , int numberOfHits , double cooldown , boolean isDualWield , boolean isTwoHanded , int adrenaline_cost , String keybind) {
        this.name = name;
        this.isBasic = isBasic;
        this.isThreshold = isThreshold;
        this.isUltimate = isUltimate;
        this.damageRange = damageRange;
        this.numberOfHits = numberOfHits;
        this.cooldown = cooldown;
        this.isDualWield = isDualWield;
        this.isTwoHanded = isTwoHanded;
        this.adrenaline_cost = adrenaline_cost;
        this.keybind = keybind;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ability{")
            .append("name='").append(name).append('\'')
            .append(", isBasic=").append(isBasic)
            .append(", isThreshold=").append(isThreshold)
            // Include other fields as needed...

            // Printing the contents of damageRange
            .append(", damageRange=[");
        for (int i = 0; i < damageRange.size(); i++) {
            int[] range = damageRange.get(i);
            sb.append(Arrays.toString(range));
            if (i < damageRange.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");

        // Closing the rest of the toString method
        sb.append(", numberOfHits=").append(numberOfHits)
            .append(", cooldown=").append(cooldown)
            .append(", isDualWield=").append(isDualWield)
            .append(", isTwoHanded=").append(isTwoHanded)
            .append(", adrenaline_cost=").append(adrenaline_cost)
            .append(", keybind='").append(keybind).append('\'')
            .append('}');
        return sb.toString();
    }
}
