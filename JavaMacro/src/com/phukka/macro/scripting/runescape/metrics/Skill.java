package com.phukka.macro.scripting.runescape.metrics;

public record Skill(int id, int level, int xp, int rank)
{
    @Override
    public String toString()
    {
        return RuneMetrics.SKILL_NAME[id] + ", rank " + rank + "\nLevel " + level + ", exp " + xp;
    }
}