package com.phukka.macro.scripting.runescape.abilities;

public class Damage {

    // Method to calculate the average hit increase based on the parameters
    public static double calculateAverageHitIncrease(double minDamage, double maxDamage, int preciseRank) {
        double withoutPrecise = ((minDamage + maxDamage) / 2);

        double withPrecise = minDamage + ((maxDamage - minDamage) * (0.3 + (preciseRank * 0.015)));
        withPrecise = (minDamage + withPrecise) / 2;

        double averageHitIncrease = ((withPrecise - withoutPrecise) / withoutPrecise) * 100;
        return averageHitIncrease;
    }

    //Calculate the damage of an ability

    public static int calculateDamage(Ability ability, int baseDamage, int critChance, int preciseRank, int equilibriumRank, int bitingRank, int prayerId, int aura, int combatType) {
        int damage = 0;


        return damage;
    }



    public double getPrayerMultiplier(int combatType, int prayerId) {
        double multiplier = 1.0;
        if (prayerId == 1) //Burst of Strength
            multiplier += 0.05;
        else if (prayerId == 2) //Superhuman Strength
            multiplier += 0.1;
        else if (prayerId == 3) //Ultimate Strength
            multiplier += 0.15;
        else if (prayerId == 4) //Chivalry
            multiplier += 0.18;
        else if (prayerId == 5) //Piety
            multiplier += 0.23;
        else if (prayerId == 6) //Sharp Eye
            multiplier += 0.05;
        else if (prayerId == 7) //Hawk Eye
            multiplier += 0.1;
        else if (prayerId == 8) //Eagle Eye
            multiplier += 0.15;
        else if (prayerId == 9) //Rigour
            multiplier += 0.18;
        return multiplier;
    }
}
