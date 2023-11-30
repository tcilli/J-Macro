package com.phukka.macro.scripting.runescape.metrics;

public record Profile(String username, int questsstarted, int totalskill, int questscomplete, int questsnotstarted, long totalxp, Activity[] activities, Skill[] skills)
{
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Username: ").append(username).append("\n");
        sb.append("Quests Complete: ").append(questscomplete).append("\n");
        sb.append("Quests Started: ").append(questsstarted).append("\n");
        sb.append("Quests Not Started: ").append(questsnotstarted).append("\n");
        sb.append("Total XP: ").append(totalxp).append("\n");
        sb.append("Total Level: ").append(totalskill).append("\n");

        for (int i = 0; i < skills.length; i++) {
            for (Skill skill : skills) {
                if (skill.id() == i) {
                    sb.append(skill.toString()).append("\n");
                    break;
                }
            }
        }

        for (Activity activity : activities) {
            sb.append(activity.toString()).append("\n");
        }
        return sb.toString();
    }
}