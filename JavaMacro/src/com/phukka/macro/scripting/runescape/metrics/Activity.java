package com.phukka.macro.scripting.runescape.metrics;

public record Activity(String date, String details, String text)
{
    @Override
    public String toString()
    {
        return "Recent activity[ " + date + " ] " + details + "\n -" + text;
    }
}