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
    
    public static double[] getLocationdata(String locationCity){
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
                
                double[] latlong = {latitude, longitude};
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
        "&longitude="+ latlong[1] +"&current=temperature_2m,relative_humidity_2m,precipitation,rain,showers,snowfall,weather_code,wind_speed_10m&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&daily=weather_code,temperature_2m_max,temperature_2m_min";


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
                int index = findIndexperTime(time);

                // JsonArray tempData =
                
                JsonObject rightNowData = (JsonObject) resultsJsonObj.get("current");
                double tempData = rightNowData.get("temperature_2m").getAsDouble();

                String weatherCondition = convertWeatherCode(rightNowData.get("weather_code").getAsLong());
                
                long humidity = rightNowData.get("relative_humidity_2m").getAsLong();

                double windspeed = rightNowData.get("wind_speed_10m").getAsDouble();


                JsonObject resultData = new JsonObject();
                resultData.addProperty("weatherCondition", weatherCondition);
                resultData.addProperty("temperature", tempData);
                resultData.addProperty("humidity", humidity);
                resultData.addProperty("windspeed", windspeed);


                return resultData;
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

    private static int findIndexperTime(JsonArray timeList){
        String cTime = getTime();


        for (int i =0; i < timeList.size();i++){
            // String time = _(String) timeList.get(i);
            String time = String.valueOf(timeList.get(i));
            if(time.equalsIgnoreCase(cTime)){
                return i;
            }   
        }
        //cant find index 
        return 0;
    }


    private static String convertWeatherCode(long weather_code){
        String weatherCondition = "";

        if (weather_code == 0L) {
            weatherCondition = "Clear";
        } 
        else if(weather_code > 0L && weather_code < 3L) {
            weatherCondition = "Cloudy";
        }
        else if((weather_code >= 51L && weather_code <= 67L) || (weather_code >= 80L && weather_code <= 99L)){
            weatherCondition = "Raining";
        }
        else if(weather_code > 71L && weather_code < 77L) {
            weatherCondition = "Snowing";
        } 
        return weatherCondition;
    }


    public static String getTime(){
        LocalDateTime cTime = LocalDateTime.now();
        //reformater to  2024-10-07T07:00
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String fcTime = cTime.format(formatter);

        return fcTime;
    }
}

