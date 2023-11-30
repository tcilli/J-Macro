package com.phukka.macro.scripting.runescape.abilities;

import com.phukka.macro.scripting.runescape.abilities.impl.GreaterDazingShot;
import com.phukka.macro.scripting.runescape.abilities.impl.GreaterRicochet;

import java.util.ArrayList;
import java.util.Random;


public class Abilties {


    public Abilties() {

    }

    public static Random random = new Random();


    public static int get_critical_strike_chance() {
        int crit_chance = 10; //the base crit chance is 10%
        int grim = 12;
        int biting = 8;
        int kalg_spec = 5;
        int kalg_summoned = 1;
        crit_chance += grim;
        crit_chance += biting;
        crit_chance += kalg_spec;
        crit_chance += kalg_summoned;
        return 100;
    }

    public static ArrayList<Integer> hits = new ArrayList<>();


    public static void generate(Ability ability, int baseDamage) {

        Random random = new Random();

        int crit_chance = get_critical_strike_chance();

        int total_damage_dealt = 0;

        for (int i = 0; i < ability.numberOfHits; i++) {
            int min = ability.damageRange.get(i)[0];
            int max = ability.damageRange.get(i)[1];
            int range = max - min;
            int range_rolled = min + random.nextInt(range + 1);
            int crit_roll = random.nextInt(100);
            int modifiedDamage = (int) (baseDamage * (range_rolled / 100.0)); // Calculate modified damage

            double upper95thPercentile = min + 0.95 * range;
            double randomRangeWithinPercentile = upper95thPercentile + random.nextDouble() * (max - upper95thPercentile); // Calculate a random value within the upper 95th percentile range


            if (crit_roll <= crit_chance || range_rolled >= upper95thPercentile) {
                if (crit_roll <= crit_chance) {
                    //System.out.println("rolled a critical hit!");
                } else {
                    //System.out.println("rolled natural critical hit!");
                }
                // Calculate upper 95th percentile range
                modifiedDamage = (int) (baseDamage * (randomRangeWithinPercentile / 100.0));

                System.out.println("Hit " + i + ": (roll " + (int)randomRangeWithinPercentile + "%/"+max+", " + modifiedDamage + ")");

            } else {
                System.out.println("Hit " + i + ": (roll " + range_rolled + "%/"+max+", " + modifiedDamage + ")");
            }

            total_damage_dealt += modifiedDamage;
            test(modifiedDamage);
        }
        System.out.println("Total damage dealt: " + total_damage_dealt);
        hits.add(total_damage_dealt);
    }


    public static void test(int AD) {
       // int AD = 2140;
        // Base Fixed value and Variable value calculation
        int basePercent = (int) (0.2 * 0.94 * 100); // 0.2 * 94% as an integer value
        int fixedBase = (int) (AD * basePercent / 100);
        int variableBase = (int) (AD * (0.94 - 0.188) * 100 / 100);

        // Effective Fixed and Variable amount (PRAYERS)
        //int effectiveFixed = (int) (fixedBase * 1.12);
        //int effectiveVariable = (int) (variableBase * 1.12);
        int effectiveFixed = (int) (fixedBase * 1.0);
        int effectiveVariable = (int) (variableBase * 1.0);

        // Boosted amount from stats
        int boostedStat = 1;
        int fixedBoosted = effectiveFixed + 4 * boostedStat;
        int variableBoosted = effectiveVariable + 4 * boostedStat;

        // Calculation related to the Precise perk
        int preciseRank = 6;
        int preciseDamage = (int) (0.0015 * preciseRank * (fixedBoosted + variableBoosted));
        int fixedPrecise = fixedBoosted + preciseDamage;
        int variablePrecise = variableBoosted - preciseDamage;

        // Calculations for natural critical probability and minimum hit for Precise 5
        int pcritNatPrecise = (int) (0.005 * (fixedPrecise + variablePrecise));
        int critMinNatPrecise = (int) ((fixedPrecise + ((1 - pcritNatPrecise / 100.0) * variablePrecise)) * 1.1);

        // Calculations for Equilibrium 3 perk
        int equilibriumRank = 2;
        int fixedEquilibrium = fixedPrecise + (int) (0.002 * equilibriumRank * variablePrecise);
        int variableEquilibrium = (int) (((1 - 0.004 * equilibriumRank) * variablePrecise));

        // Applying 10% Berserker aura
        int fixedFinal = (int) (fixedEquilibrium * 1.1);
        int variableFinal = (int) (variableEquilibrium * 1.1);

        // Calculating the entire damage range
        int minDamage = fixedFinal;
        int maxDamage = fixedFinal + variableFinal;

        // Calculations for final natural critical hit minimum with Equilibrium
        int critMinNatEq = (int) ((fixedEquilibrium + ((1 - pcritNatPrecise / 100.0) * variableEquilibrium)) * 1.1);

        // Natural critical hit and forced critical hit minimum with Equilibrium
        int critMinNatForced = (int) ((fixedEquilibrium + 0.95 * variableEquilibrium) * 1.1);

        // Displaying the results
        System.out.println("----------------");
        System.out.println("Damage range: " + minDamage + " to " + maxDamage);
        System.out.println("Final natural critical hit: " + critMinNatEq);
        System.out.println("Natural critical hit probability: " + critMinNatEq);
        System.out.println("Forced critical hit minimum: " + critMinNatForced);
        System.out.println("----------------");
    }


    public void simulate_ability(int amount) {

        Ability greater_ricochet = new GreaterRicochet();
        Ability greater_dazing_shot = new GreaterDazingShot();

        System.out.println("Simulating " + amount + " hits for " + greater_ricochet.toString());
        for (int i = 0; i < amount; i++) {
            generate(greater_ricochet, 2140);
        }
        long sum = 0;
        long average = 0;

        for (int i : hits) {
            sum += i;
        }
        average = sum / hits.size();

        System.out.println("Average hit for " + greater_ricochet.name + " is: " + average);
        System.out.println("Total damage dealt: " + sum +" over " + hits.size() + " hits");

    }



    public static int[] generate_hits(int baseDamage, Ability abil, boolean target_moving, boolean target_stunned) {

        int[] hit = new int[abil.numberOfHits];

        int min_range = abil.damageRange.get(0)[0]; // Get min damage (first element of first array
        int range = (abil.damageRange.get(0)[1] - abil.damageRange.get(0)[0]);

        int hit_pointer = 0;

        if (abil.name.equals("Greater Ricochet"))
        {
            //Inital 3 hits are 20-100% damage
            hit[hit_pointer++] = min_range + (random.nextInt(range + 1));
            hit[hit_pointer++] = min_range + (random.nextInt(range + 1));
            hit[hit_pointer++] = min_range + (random.nextInt(range + 1));

            //following 4 hits are 10-50% damage
            int halfMin = min_range / 2;
            int halfRange = range / 2;
            hit[hit_pointer++] = halfMin + random.nextInt(halfRange + 1);
            hit[hit_pointer++] = halfMin + random.nextInt(halfRange + 1);
            hit[hit_pointer++] = halfMin + random.nextInt(halfRange + 1);
            hit[hit_pointer] = halfMin + random.nextInt(halfRange + 1);
        }
        if (abil.name.equals("Greater Dazing Shot"))
        {
            hit[hit_pointer++] = min_range + (random.nextInt(range + 1));
        }
        if (abil.name.equals("Piercing Shot"))
        {
            hit[hit_pointer++] = min_range + (random.nextInt(range + 1));
            hit[hit_pointer] = min_range + (random.nextInt(range + 1));
        }
        if (abil.name.equals("Corruption Shot"))
        {
            int initial_hit = min_range + (random.nextInt(range + 1));
            hit[hit_pointer++] = initial_hit;
            hit[hit_pointer++] = initial_hit - (initial_hit * 20) / 100;
            hit[hit_pointer++] = initial_hit - (initial_hit * 40) / 100;
            hit[hit_pointer] =   initial_hit - (initial_hit * 60) / 100;
        }
        if (abil.name.equals("Fragmentation Shot"))
        {
            int initial_hit = min_range + (random.nextInt(range + 1));
            if (target_moving)
            {
                initial_hit = initial_hit * 2;
            }
            hit[hit_pointer++] = initial_hit;
            hit[hit_pointer++] = initial_hit;
            hit[hit_pointer++] = initial_hit;
            hit[hit_pointer] =   initial_hit;
        }
        if (abil.name.equals("Snap Shot"))
        {
            hit[hit_pointer++] = min_range + (random.nextInt(range + 1));
            hit[hit_pointer] = min_range + (random.nextInt() * 110);
        }
        if (abil.name.equals("Dead Shot"))
        {
            int initial_hit = min_range + (random.nextInt(range + 1));
            hit[hit_pointer++] = initial_hit - (initial_hit * 30) / 100;
            hit[hit_pointer++] = initial_hit - (initial_hit * 30) / 100;
            hit[hit_pointer++] = initial_hit - (initial_hit * 30) / 100;
            hit[hit_pointer++] = initial_hit - (initial_hit * 30) / 100;
            hit[hit_pointer++] = initial_hit - (initial_hit * 30) / 100;
            hit[hit_pointer++] = initial_hit - (initial_hit * 30) / 100;
            hit[hit_pointer] = initial_hit - (initial_hit * 30) / 100;
        }
        if (abil.name.equals("Rapid Fire"))
        {
            hit[hit_pointer++] = 19 + (random.nextInt() * 75);
            hit[hit_pointer++] = 19 + (random.nextInt() * 75);
            hit[hit_pointer++] = 19 + (random.nextInt() * 75);
            hit[hit_pointer++] = 19 + (random.nextInt() * 75);
            hit[hit_pointer++] = 19 + (random.nextInt() * 75);
            hit[hit_pointer++] = 19 + (random.nextInt() * 75);
            hit[hit_pointer++] = 19 + (random.nextInt() * 75);
            hit[hit_pointer] = 19 + (random.nextInt() * 75);
        }
        return hit;
    }

}
