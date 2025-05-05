package main;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;

public class MyTwitch {

    private final String clientID;
    private final String Client_Secret;
    private String accesstoken;

    public MyTwitch(String clientID, String Client_Secret) {
        this.clientID = clientID;
        this.Client_Secret = Client_Secret;
        this.getaccesstoken();
    }

    public List<String> getstreamer(String categoryname) {
        List<String> streamerList = new ArrayList<>();
        String categoryId = getcategoryid(categoryname);
        if (categoryId == null) {
            return streamerList;
        }
        try {
            for (int attempt = 0; attempt < 2; attempt++) {
                HttpResponse<JsonNode> response = Unirest.get("https://api.twitch.tv/helix/streams")
                        .queryString("game_id", categoryId)
                        .queryString("first", 100)
                        .header("Client-ID", this.clientID)
                        .header("Authorization", "Bearer " + this.accesstoken)
                        .asJson();

                if (response.getStatus() == 200) {
                    JSONArray data = response.getBody().getObject().optJSONArray("data");
                    if (data != null) {
                        for (int i = 0; i < data.length(); i++) {
                            String userName = data.getJSONObject(i).getString("user_name");
                            streamerList.add(userName);
                        }
                    } else {
                        System.out.println("Keine Streamer gefunden für Kategorie: " + categoryname);
                    }
                    return streamerList;
                } else if (response.getStatus() == 401 && attempt == 0) {
                    System.err.println("Token ungültig. Erneut versuchen...");
                    this.getaccesstoken();
                } else {
                    System.err.println("Fehler bei Twitch-API-Anfrage. Status: " + response.getStatus());
                    System.err.println("Antwort: " + response.getBody());
                }
            }
        } catch (Exception e) {
            System.err.println("Fehler in getstreamer für Kategorie: " + categoryname);
            e.printStackTrace();
        }

        return streamerList;
    }

    private void getaccesstoken() {
        try {
            HttpResponse<JsonNode> response = Unirest.post("https://id.twitch.tv/oauth2/token")
                    .field("client_id", this.clientID)
                    .field("client_secret", Client_Secret)
                    .field("grant_type", "client_credentials")
                    .asJson();
            if (response.getStatus() == 200) {
                this.accesstoken = response.getBody().getObject().getString("access_token");
                if (this.accesstoken == null) {
                    System.err.println("Access Token is missing in response!");
                }
            } else {
                System.err.println("Failed to get the access token. Status code: " + response.getStatus());
            }
        } catch (Exception e) {
            System.err.println("Failed fetching access token.");
            e.printStackTrace();
        }
    }

    private String getcategoryid(String categoryname) {
        // get request for the category id
        int maxattempts = 2;
        for (int attempt = 0; attempt < maxattempts; attempt++) {
            try {
                HttpResponse<JsonNode> response = Unirest.get("https://api.twitch.tv/helix/games")
                        .queryString("name", categoryname)
                        .header("Client-ID", this.clientID)
                        .header("Authorization", "Bearer " + this.accesstoken)
                        .asJson();
                if (response.getStatus() == 200) {

                    String id = response.getBody().getObject().optJSONArray("data").getJSONObject(0).getString("id");

                    return id;
                } else {
                    if (response.getStatus() == 401) {
                        this.getaccesstoken();
                        return getcategoryid(categoryname);
                    } else {
                        System.err.println("Failed to get the ID. Status code: " + response.getStatus());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "-1";
            }
        }
        try {
            HttpResponse<JsonNode> response = Unirest.get("https://api.twitch.tv/helix/games")
                    .queryString("name", categoryname)
                    .header("Client-ID", this.clientID)
                    .header("Authorization", "Bearer " + this.accesstoken)
                    .asJson();
            if (response.getStatus() == 200) {

                String id = response.getBody().getObject().optJSONArray("data").getJSONObject(0).getString("id");

                return id;
            } else {
                if (response.getStatus() == 401) {
                    this.getaccesstoken();
                    return getcategoryid(categoryname);
                } else {
                    System.err.println("Failed to get the ID. Status code: " + response.getStatus());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "-1";
        }
        return "-1";

    }

}
