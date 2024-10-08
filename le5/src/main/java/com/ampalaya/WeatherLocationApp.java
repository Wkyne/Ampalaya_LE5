package com.ampalaya;

// import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;


public class WeatherLocationApp {
    
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("settings");
    }

    @FXML
    private ObservableList<String> savedLocations = FXCollections.observableArrayList();

    
    
    @FXML
    public void initialize() {
        loadSavedLocations();

    }
    
    private void loadSavedLocations() {
        // For testing, you can hardcode some locations.
        savedLocations.addAll("Manila", "Cebu", "Davao");
        
        // If you're using a file or database, load them here.
        
        // Update the UI
        updateSavedLocationsUI();
    }
    
    private void updateSavedLocationsUI() {
        Object listViewSavedLocations;
        // Assuming you have a ListView to display the saved locations
        // listViewSavedLocations.setItems(savedLocations);
    }




    //threading
    public void fetchWeatherData(String locationCity) {
        Task<JsonObject> weatherTask = new Task<>() {
            @Override
            protected JsonObject call() throws Exception {
                // Fetch location data
                Double[] result = getLocationdata(locationCity);
                // Fetch weather data based on location
                return getWeatherdata(result);
            }

            @Override
            protected void succeeded() {
                // This method is called on the JavaFX Application Thread
                JsonObject outcome = new JsonObject();
                try {
                    outcome = get();
                } catch (InterruptedException e) {
                    System.out.println(e);
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    System.out.println(e);
                    e.printStackTrace();
                }
                // You can now update your UI with the weather data
                updateWeatherUI(outcome);
            }

            @Override
            protected void failed() {
                // Handle failure
                System.out.println("Failed to fetch weather data.");
            }
        };

        // Start the thread
        new Thread(weatherTask).start();
    }

    private void updateWeatherUI(JsonObject outcome) {
        if (outcome != null) {
            String humidity = outcome.get("humidity").toString();
            String weatherCondition = outcome.get("weatherCondition").getAsString();
            String temperature = outcome.get("temperature").toString();
            String windspeed = outcome.get("windspeed").toString();

            // Here, you would update your UI components with the fetched data
            System.out.println("Humidity: " + humidity);
            System.out.println("Weather Condition: " + weatherCondition);
            System.out.println("Temperature: " + temperature);
            System.out.println("Windspeed: " + windspeed);

            // Don't forget to update the other days as well
            JsonArray otherDays = outcome.getAsJsonArray("otherDays");
            if (otherDays != null) {
                for (int i = 0; i < otherDays.size(); i++) {
                    JsonObject dayWeather = otherDays.get(i).getAsJsonObject();
                    String day = dayWeather.get("day").getAsString();
                    String condition = dayWeather.get("condition").getAsString();
                    System.out.println(day + ": " + condition);
                }
            } else {
                System.out.println("No data available for other days.");
            }
        } else {
            System.out.println("Failed to fetch weather data.");
        }
    }







    //API call for location
    public static Double[] getLocationdata(String locationCity){
        //initialize variables
        Double latitude = 0.0;
        Double longitude = 0.0;
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
    //API call for Weather
    public static JsonObject getWeatherdata(Double[] latlong){
        String urlString = "";

        urlString += "https://api.open-meteo.com/v1/forecast?"+
        "latitude="+ latlong[0] + 
        "&longitude="+ latlong[1] +"&current=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&daily=weather_code,temperature_2m_max&timezone=Asia%2FSingapore";


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
                
                
                JsonObject rightNowData = (JsonObject) resultsJsonObj.get("current");
                Double tempData = rightNowData.get("temperature_2m").getAsDouble();

                String weatherCondition = convertWeatherCode(rightNowData.get("weather_code").getAsLong());
                
                long humidity = rightNowData.get("relative_humidity_2m").getAsLong();

                Double windspeed = rightNowData.get("wind_speed_10m").getAsDouble();


                JsonObject resultData = new JsonObject();
                resultData.addProperty("weatherCondition", weatherCondition);
                resultData.addProperty("temperature", tempData);
                resultData.addProperty("humidity", humidity);
                resultData.addProperty("windspeed", windspeed);

                JsonArray otherDays = getWeeklyData(resultsJsonObj);


                resultData.add("otherDays", otherDays);
                System.out.println("Weather JSON: " + resultsJsonObj.toString());
                return resultData;
            }    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //method for returning API response
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
    //weekly feature
    private static JsonArray getWeeklyData(JsonObject resultsJsonObj){

        JsonArray otherDays = new JsonArray();
        JsonObject daily = (JsonObject) resultsJsonObj.get("daily");
        JsonArray date = (JsonArray) daily.get("time");
        JsonArray cond = (JsonArray) daily.get("weather_code");

        for (int i = 1; i < date.size();i++){
            String day = getDayoftheWeek(date.get(i).getAsString());
            String condition = convertWeatherCode(cond.get(i).getAsLong());
            otherDays.add(createDayWeather(day, condition));
        }
        return otherDays;
    }

    


    //basic methods
    private static String convertWeatherCode(long weather_code){
        String weatherCondition = "";

        if (weather_code == 0L) {
            weatherCondition = "Clear";
        } 
        else if(weather_code > 0L && weather_code <= 3L) {
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

    public static String reformatDate(){
        LocalDate cDate = LocalDate.now();
        //reformater to  2024-10-07 just to be sure
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fcDate = cDate.format(formatter);
        return fcDate;
    }

    public static String getDayoftheWeek(String dateStr){

        LocalDate date = LocalDate.parse(dateStr.replace("\"", ""));
        String dayOfWeek = date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        return dayOfWeek;
    }

    private static JsonObject createDayWeather(String day, String condition) {
        JsonObject dayWeather = new JsonObject();
        dayWeather.addProperty("day", day);
        dayWeather.addProperty("condition", condition);
        return dayWeather;
    }


}

