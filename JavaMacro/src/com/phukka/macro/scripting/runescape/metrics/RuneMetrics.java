package com.phukka.macro.scripting.runescape.metrics;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class RuneMetrics
{
    static final int TIME_OUT_SECONDS = 10;
    static final String URL = "https://apps.runescape.com/runemetrics/profile/profile?user=";

    public static Profile lookupUsername(String username)
    {
        try
        {
            String encodedUsername = URLEncoder.encode(username, StandardCharsets.UTF_8);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + encodedUsername))
                .header("User-Agent", "stat_lookup")
                .timeout(Duration.ofSeconds(TIME_OUT_SECONDS))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int responseCode = response.statusCode();

            if (responseCode == 200) {
                return profileBuilder(username, response.body());
            } else {
                System.out.println("Failed to fetch data. Status code " + responseCode + " https://developer.mozilla.org/en-US/docs/Web/HTTP/Status");
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("Request timed out > "+ TIME_OUT_SECONDS +" second(s) passed without a response. " +ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
        }
        return null;
    }

    static Profile profileBuilder(String username, String response) {
        JSONObject jsonObject = new JSONObject(response);

        JSONArray activityArray = jsonObject.getJSONArray("activities");
        JSONArray skillValuesArray = jsonObject.getJSONArray("skillvalues");

        Activity[] activity = new Activity[activityArray.length()];
        Skill[] skillValue = new Skill[skillValuesArray.length()];

        JSONObject objectReader = null;

        for (int i = 0; i < activityArray.length(); i++) {
            objectReader = activityArray.getJSONObject(i);
            activity[i] = new Activity(
                objectReader.getString("date"),
                objectReader.getString("details"),
                objectReader.getString("text"));
        }
        for (int i = 0; i < skillValuesArray.length(); i++) {
            objectReader = skillValuesArray.getJSONObject(i);
            skillValue[i] = new Skill(
                objectReader.getInt("id"),
                objectReader.getInt("level"),
                objectReader.getInt("xp"),
                objectReader.getInt("rank"));
        }

        return new
            Profile(
                username,
                jsonObject.getInt("questsstarted"),
                jsonObject.getInt("totalskill"),
                jsonObject.getInt("questscomplete"),
                jsonObject.getInt("questsnotstarted"),
                jsonObject.getLong("totalxp"),
                activity,
                skillValue
            )
        ;
    }

    public static final String[] SKILL_NAME = {
        "Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking",
        "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
        "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter",
        "Construction", "Summoning", "Dungeoneering", "Divination", "Invention", "Archaeology", "Necromancy"
    };
}
