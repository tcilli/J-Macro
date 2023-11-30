import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import org.json.JSONArray
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Locale
import java.net.URLEncoder

class RuneMetrics {

    /**
     * STATUS CODES
     * See <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">HTTP Status Codes</a>
     */
    static final int HTTP_STATUS_OK = 200

    static final String LOOKUP_USERNAME_URL = "https://apps.runescape.com/runemetrics/profile/profile?user="

    static String lookupUsername(String username) {

        String encodedUsername = URLEncoder.encode(username, "UTF-8");

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOOKUP_USERNAME_URL + encodedUsername))
                .header("User-Agent", "stat_lookup")
                .build();

        try {

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();

            if (statusCode == HTTP_STATUS_OK) {
                return decodedProfile(username, response.body()).toString();
            } else {
                System.out.println("Failed to fetch data. Status code: " + statusCode + " https://developer.mozilla.org/en-US/docs/Web/HTTP/Status");
            }
        } catch (SocketTimeoutException ex) {
            println "Socket Timeout Exception: ${ex.message}"
        } catch (Exception ex) {
            println "Exception occurred: ${ex.message}"
        }
        return null
    }

    static String decodedProfile(String username, String response) {
        JSONObject jsonObject = new JSONObject(response);

        JSONArray activityArray = jsonObject.getJSONArray("activities");
        JSONArray skillValuesArray = jsonObject.getJSONArray("skillvalues");

        Activity[] activity = new Activity[activityArray.length()]
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
        Profile profile = new Profile(
                username,
                jsonObject.getInt("questsstarted"),
                jsonObject.getInt("totalskill"),
                jsonObject.getInt("questscomplete"),
                jsonObject.getInt("questsnotstarted"),
                jsonObject.getLong("totalxp"),
                activity,
                skillValue);

        return profile.toString()
    }


    static List<String> skillName = Arrays.asList(
            "Attack", "Defence", "Strength", "Hitpoints", "Ranged", "Prayer", "Magic", "Cooking",
            "Woodcutting", "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing", "Mining",
            "Herblore", "Agility", "Thieving", "Slayer", "Farming", "Runecrafting", "Hunter",
            "Construction", "Summoning", "Dungeoneering", "Divination", "Invention", "Archaeology","Necromancy"
    );


    static class Profile {
        public String username;
        public int questsstarted;
        public int totalskill;
        public int questscomplete;
        public int questsnotstarted;
        public long totalxp;
        public Activity[] activities;
        public Skill[] skills;
        Profile(String username, int questsstarted, int totalskill, int questscomplete, int questsnotstarted, long totalxp, Activity[] activities, Skill[] skills) {
            this.username = username;
            this.questsstarted = questsstarted;
            this.totalskill = totalskill;
            this.questscomplete = questscomplete;
            this.questsnotstarted = questsnotstarted;
            this.totalxp = totalxp;
            this.activities = activities;
            this.skills = skills;
        }

        @Override
        public String toString() {
            def activitiesString = activities.collect { activity ->
                activity.toString()
            }.join("\n")


            def orderedSkills = []

            // Loop through each skill ID from 0 to 28
            for (int i = 0; i < skills.length; i++) {
                // Find the skill object with the current ID
                def skill = skills.find { it.id == i }

                if (skill) orderedSkills << skill // Add the skill to the ordered list
            }

            def skillsString = orderedSkills.collect { skill ->
                skill.toString()
            }.join("\n")

            return """Quests Started: ${questsstarted}
Total Skill: ${totalskill}
Quests Complete: ${questscomplete}
Quests Not Started: ${questsnotstarted}
Total XP: ${totalxp}
${activitiesString}
${skillsString}
"""
        }
    }


    static class Activity {
        public String date;
        public String details;
        public String text;
        Activity(String date, String details, String text) {
            this.date = date;
            this.details = details;
            this.text = text;
        }

        @Override
        public String toString() {
            return "Recent activity[ "+ date +" ] " + details +"\n -"+ text;
        }
    }


    static class Skill {
        public int id;
        public int level;
        public int xp;
        public int rank;
        Skill(int id, int level, int xp, int rank) {
            this.id = id;
            this.level = level;
            this.xp = xp;
            this.rank = rank;
        }

        @Override
        public String toString() {
            return skillName[id] + ", rank "+ rank +"\nLevel " + level + ", exp "+ xp;
        }
    }
}
