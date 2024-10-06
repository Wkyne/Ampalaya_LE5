package com.ampalaya;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
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
                JsonObject resultsJsonObj = JsonParser.parseString(locationJson.toString()).getAsJsonObject();
                JsonArray locationData = resultsJsonObj.getAsJsonArray("results");
                
                JsonObject firstResult = locationData.get(0).getAsJsonObject();
                
                latitude = firstResult.get("latitude").getAsDouble();
                longitude = firstResult.get("longitude").getAsDouble();
                Double[] longlat = {longitude,latitude};
                return longlat;
                // longlat.
            }

        } catch (Exception e) {
           e.printStackTrace();
        }
        return null;
        // getWeatherdata(latitude, longitude);
    }

    public static JsonObject getWeatherdata(double latitude, double longitude){
        String urlString = "";

        urlString += "https://api.open-meteo.com/v1/forecast?"+
        "latitude="+ latitude + 
        "&longitude="+ longitude +"&hourly=temperature_2m,relative_humidity_2m,weather_code";


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

                //dis shi too long ngl
                JsonObject resultsJsonObj = JsonParser.parseString(weatherJson.toString()).getAsJsonObject();
                JsonArray locationData = resultsJsonObj.getAsJsonArray("results");
                
                JsonObject firstResult = locationData.get(0).getAsJsonObject();
                
                latitude = firstResult.get("latitude").getAsDouble();
                longitude = firstResult.get("longitude").getAsDouble();
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


}
