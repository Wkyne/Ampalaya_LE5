package com.ampalaya;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.fxml.FXML;

public class WeatherLocationApp {

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("settings");
    }
    
    public static Double[] getLocationdata(String locationCity){
        //initialize variables
        double latitude = 0;
        double longitude = 0;
        locationCity = locationCity.replaceAll(" ", "+");
        String urlString = "";

        urlString += "https://geocoding-api.open-meteo.com/v1/search?"+
                    "name="+ locationCity + "&count=10&language=en&format=json";


        try {
            HttpsURLConnection connec = getAPIResponse(urlString);

            if (connec.getResponseCode() != 200){
                System.out.println("shi cant connect");
            }
            else{
                StringBuilder locationJson = new StringBuilder();
                Scanner scan = new Scanner(connec.getInputStream());
                while (scan.hasNext()) {
                    locationJson.append(scan.nextLine());
                }
                scan.close();
                connec.disconnect();

                //dis shi too long ngl
                JsonObject resultsJsonObj = JsonParser.parseString(String.valueOf(locationJson)).getAsJsonObject();
                JsonArray locationData = resultsJsonObj.getAsJsonArray("results");
                
                JsonObject firstResult = locationData.get(0).getAsJsonObject();
                
                latitude = firstResult.get("latitude").getAsDouble();
                longitude = firstResult.get("longitude").getAsDouble();
                
                Double[] latlong = {latitude, longitude};
                return latlong;
            }

        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
        
    }

    public static JsonObject getWeatherdata(double[] latlong){
        String urlString = "";

        urlString += "https://api.open-meteo.com/v1/forecast?"+
        "latitude="+ latlong[0] + 
        "&longitude="+ latlong[1] +"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m";


        try {
            HttpsURLConnection connec = getAPIResponse(urlString);

            if (connec.getResponseCode() != 200){
                System.out.println("shi cant connect");
            }
            else{
                StringBuilder weatherJson = new StringBuilder();
                Scanner scan = new Scanner(connec.getInputStream());
                while (scan.hasNext()) {
                    weatherJson.append(scan.nextLine());
                }
                scan.close();
                connec.disconnect();

                JsonObject resultsJsonObj = JsonParser.parseString(String.valueOf(weatherJson)).getAsJsonObject();
                JsonArray weatherData = resultsJsonObj.getAsJsonArray("results");
                JsonObject hourly = (JsonObject) resultsJsonObj.get("hourly");
                JsonArray time = (JsonArray) hourly.get("time");


            }    
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        

        return null;
    }

    private static HttpsURLConnection getAPIResponse(String urlString){
        try {
            @SuppressWarnings("deprecation")
            URL url = new URL(urlString);
            HttpsURLConnection connec = (HttpsURLConnection)url.openConnection();

            connec.setRequestMethod("GET");
            connec.connect();
            return connec;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //cant connect typ shi
        return null;
    }

    private static int findIndexperTime(JsonArray time){
        return 0;

    }

    public static String getTime(){
        LocalDateTime cTime = LocalDateTime.now();
        //reformater to  2024-10-07T07:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String fcTime = cTime.format(formatter);

        return fcTime;
    }
}
