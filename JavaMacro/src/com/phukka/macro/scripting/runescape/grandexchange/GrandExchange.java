package com.phukka.macro.scripting.runescape.grandexchange;

import org.json.JSONObject;

import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Useful links
 * <p>
 * RS-Wiki API
 * <a href="https://runescape.wiki/w/RuneScape:Grand_Exchange_Market_Watch/Usage_and_APIs">website</a>
 * </p>
 * <p>
 * HTTP Response codes
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Status">website</a>
 * </p>
 */
public class GrandExchange {

    static final String GE_PRICES_URL = "https://runescape.wiki/w/Module:GEPrices/data.json?action=raw";
    static final String GE_TRADE_VOLUME_URL = "https://runescape.wiki/w/Module:GEVolumes/data.json?action=raw";
    static final int TIME_OUT_SECONDS = 10;

    public static Map<String, Long> lookupItemPrice(String itemName) {
        itemName = itemName.toLowerCase();
        String formattedDateTime = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")).replaceAll("/", "");
        String filePath = "./data/runescape/grandexchange/prices/"+ formattedDateTime +".json";

        Map<String, Long> results = new HashMap<>();

        try {
            File file = new File(filePath);
            boolean found = false;
            if (!file.exists()) {
                int date = Integer.parseInt(formattedDateTime);
                for (int i = 0; i < 365; i++) {
                    filePath = "./data/runescape/grandexchange/prices/"+ (date - i) +".json";
                    file = new File(filePath);
                    if (file.exists()) {
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                System.out.println("No price data found for Grand Exchange prices");
                System.out.println("failed to fetch from "+ GE_PRICES_URL);
                System.out.println("more info https://runescape.wiki/w/RuneScape:Grand_Exchange_Market_Watch/Usage_and_APIs");
                return null;
            }

            // Read the JSON file
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonData.append(line);
            }
            reader.close();

            // Parse JSON data
            JSONObject jsonObject = new JSONObject(jsonData.toString());

            // Iterate through JSON keys to find the item
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                String lowercaseKey = key.toLowerCase();
                if (lowercaseKey.contains(itemName)) {
                    results.put(key, jsonObject.getLong(key));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (results.size() < 1) {
            return null;
        }
        if (results.size() > 50) {
            System.out.println("Too many results for "+ itemName +"!, exceeds 50 ("+results.size()+")");
            return null;
        }
        System.out.println("Found "+results.size()+" result(s) for "+ itemName);
        for (Map.Entry<String, Long> entry : results.entrySet()) {
            System.out.println(entry.getKey() + " : " + new DecimalFormat("#,###").format(entry.getValue()) + " gp");
        }
        return results;
    }

    public static void downloadGEPrices()
    {
        try
        {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GE_PRICES_URL))
                .header("User-Agent", "download_prices_request")
                .timeout(Duration.ofSeconds(TIME_OUT_SECONDS))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();

            if (responseCode == 200) {
                JSONObject saveFile = new JSONObject(response.body());
                String formattedDateTime = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")).replaceAll("/", "");
                saveJSON(saveFile, formattedDateTime, "prices");
            } else {
                System.out.println("Failed to fetch g.e price data. Status code " + responseCode + " https://developer.mozilla.org/en-US/docs/Web/HTTP/Status");
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("Request timed out > "+ TIME_OUT_SECONDS +" second(s) passed without a response. " +ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
        }
    }

    public static void downloadGETradeVolumes()
    {
        try
        {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GE_TRADE_VOLUME_URL))
                .header("User-Agent", "download_trade_volume_request")
                .timeout(Duration.ofSeconds(TIME_OUT_SECONDS))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int responseCode = response.statusCode();

            if (responseCode == 200) {
                JSONObject saveFile = new JSONObject(response.body());
                String formattedDateTime = LocalDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy/MM/dd")).replaceAll("/", "");
                saveJSON(saveFile, formattedDateTime, "trade_volume");
            } else {
                System.out.println("Failed to fetch g.e trade volume data. Status code " + responseCode + " https://developer.mozilla.org/en-US/docs/Web/HTTP/Status");
            }
        } catch (SocketTimeoutException ex) {
            System.out.println("Request timed out > "+ TIME_OUT_SECONDS +" second(s) passed without a response. " +ex.getMessage());
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.getMessage());
        }
    }

    static void saveJSON(JSONObject jsonObject, String title, String folder)
    {
        String fileName = title +".json";
        String directoryPath = "./data/runescape/grandexchange/" + folder + "/";
        String filePath = directoryPath + fileName;

        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(jsonObject.toString());
            fileWriter.close();

            System.out.println("updated " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
