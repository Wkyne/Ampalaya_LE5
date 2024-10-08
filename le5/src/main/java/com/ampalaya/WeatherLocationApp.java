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

import com.gluonhq.maps.MapPoint;
import com.gluonhq.maps.MapView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// import javafx.collections.FXCollections;
// import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;


public class WeatherLocationApp {
    
    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("settings");
    }

    
    @FXML
    private HBox Mainlayout;

    @FXML
    private TextField citySearch;

    @FXML
    private Button searchbtn;

    @FXML
    private Label curCity;

    @FXML
    private Label curHumid;

    @FXML
    private Label curTemp;

    @FXML
    private ImageView curWeather;

    @FXML
    private Label curWindspeed;

    @FXML
    private Label day2nd;

    @FXML
    private Label day3rd;

    @FXML
    private Label day4th;

    @FXML
    private Label day5th;

    @FXML
    private Label day6th;

    @FXML
    private Label day7th;

    @FXML
    private Label temp2nd;

    @FXML
    private Label temp3rd;

    @FXML
    private Label temp4th;

    @FXML
    private Label temp5th;

    @FXML
    private Label temp6th;

    @FXML
    private Label temp7th;

    @FXML
    private ImageView weather2nd;

    @FXML
    private ImageView humidicon;

    @FXML
    private ImageView windicon;

    @FXML
    private ImageView weather3rd;

    @FXML
    private ImageView weather4th;

    @FXML
    private ImageView weather5th;

    @FXML
    private ImageView weather6th;

    @FXML
    private ImageView weather7th;

    @FXML
    private MapView mapView;

    // @FXML
    // private ObservableList<String> savedLocations = FXCollections.observableArrayList();

    private MapPoint effielPoint = new MapPoint(14.5995, 120.9842);
    
    @FXML
    public void initialize() {
        // loadSavedLocations();
        MapView mapView = createMapView();
        Mainlayout.getChildren().add(mapView);
        HBox.setHgrow(mapView, Priority.ALWAYS);
        Image wicon = new Image("file:le5/src/main/resources/com/ampalaya/images/Windspeed.png");
        Image hicon = new Image("file:le5/src/main/resources/com/ampalaya/images/humidity.png");
        windicon.setImage(wicon);
        humidicon.setImage(hicon);


        // labels
        curCity.setStyle("-fx-font-size: 30px; -fx-font-weight: bold; -fx-text-fill: #023E8A;"); // DodgerBlue
        curTemp.setStyle("-fx-font-size: 25px; -fx-text-fill: #1E90FF;"); // DeepSkyBlue
        curWindspeed.setStyle("-fx-font-size: 16px; -fx-text-fill: #4169E1;"); // RoyalBlue
        curHumid.setStyle("-fx-font-size: 16px; -fx-text-fill: #4169E1;"); // CornflowerBlue    
        // buttons
        searchbtn.setStyle(
            "-fx-background-color: #007bff; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 14px; " +
            "-fx-border-radius: 5px;"
        );
    
        // main layout (with border radius and background color)
        Mainlayout.setStyle(
            "-fx-border-radius: 50%; " +
            "-fx-clip-radius: 50%; " +
            "-fx-background-color: linear-gradient(to bottom right, #add8e6, #b0e0e6);"        );



    
        // Set dimensions of the main layout to maintain a square aspect ratio
        Mainlayout.setPrefWidth(500);
        Mainlayout.setPrefHeight(500);
    
        // Add the map view to the layout
        fetchWeatherData("MANILA");
    }
    
        


    
    











    public void searchLoc(){
        fetchWeatherData(citySearch.getText());
    }
     @FXML
    void searchLoc(ActionEvent event) {
        searchLoc();
    }
    // private void loadSavedLocations() {
    //     // For testing, you can hardcode some locations.
    //     savedLocations.addAll("Manila", "Cebu", "Davao");
        
    //     // If you're using a file or database, load them here.
        
    //     // Update the UI
    //     updateSavedLocationsUI();
    // }
    
    // private void updateSavedLocationsUI() {
    //     // Object listViewSavedLocations;
    //     // Assuming you have a ListView to display the saved locations
    //     // listViewSavedLocations.setItems(savedLocations);
    // }


    

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

                    curCity.setText(locationCity);
                    Double[] latLong = getLocationdata(locationCity); // Fetch latitude and longitude
                    updateMapUI(latLong[0], latLong[1]);
                    updateWeatherUI(outcome);
                    updateWeeklyUI(outcome);
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
                System.out.println("Failed to fetch weather data.");
                curCity.setText("No City Found");
                curTemp.setText("N/A");
                curWindspeed.setText("N/A");
                curHumid.setText("N/A");
                // Optionally, set some default icons for the weather condition
                curWeather.setImage(null); 
            }
            
        };

        // Start the thread
        new Thread(weatherTask).start();
    }
    
    public void updateWeeklyUI(JsonObject outcome) {
        if (outcome != null) {
            JsonArray otherDays = outcome.getAsJsonArray("otherDays");
            
            // Check if otherDays is not null and contains data
            if (otherDays != null && otherDays.size() > 0) {
                // Loop through the next 7 days, updating the respective UI components
                for (int i = 0; i < otherDays.size(); i++) {
                    JsonObject dayWeather = otherDays.get(i).getAsJsonObject();
                    
                    // Assuming the days start from 2nd day (index 0) to the 7th day (index 5)
                    switch (i) {
                        case 0:
                            day2nd.setText(dayWeather.get("day").getAsString());
                            temp2nd.setText(dayWeather.get("condition").getAsString()); // You might want to get temperature too
                            setWeatherImage(dayWeather.get("condition").getAsString(), weather2nd);
                            break;
                        case 1:
                            day3rd.setText(dayWeather.get("day").getAsString());
                            temp3rd.setText(dayWeather.get("condition").getAsString());
                            setWeatherImage(dayWeather.get("condition").getAsString(), weather3rd);
                            break;
                        case 2:
                            day4th.setText(dayWeather.get("day").getAsString());
                            temp4th.setText(dayWeather.get("condition").getAsString());
                            setWeatherImage(dayWeather.get("condition").getAsString(), weather4th);
                            break;
                        case 3:
                            day5th.setText(dayWeather.get("day").getAsString());
                            temp5th.setText(dayWeather.get("condition").getAsString());
                            setWeatherImage(dayWeather.get("condition").getAsString(), weather5th);
                            break;
                        case 4:
                            day6th.setText(dayWeather.get("day").getAsString());
                            temp6th.setText(dayWeather.get("condition").getAsString());
                            setWeatherImage(dayWeather.get("condition").getAsString(), weather6th);
                            break;
                        case 5:
                            day7th.setText(dayWeather.get("day").getAsString());
                            temp7th.setText(dayWeather.get("condition").getAsString());
                            setWeatherImage(dayWeather.get("condition").getAsString(), weather7th);
                            break;
                        default:
                            break;
                    }
                }
            } else {
                System.out.println("No data available for the upcoming days.");
            }
        } else {
            System.out.println("Failed to retrieve weekly weather data.");
        }
    }
    

    private void updateWeatherUI(JsonObject outcome) {
        if (outcome != null) {
            String humidity = outcome.get("humidity").toString();
            String weatherCondition = outcome.get("weatherCondition").getAsString();
            String temperature = outcome.get("temperature").toString();
            String windspeed = outcome.get("windspeed").toString();

            // Here, you would update your UI components with the fetched data
            
            curHumid.setText("Humidity: "+humidity);
            curTemp.setText(temperature+"°");
            setWeatherImage(weatherCondition, curWeather);
            curWindspeed.setText(windspeed +"km/s");
            // Image hicon = new Image("file:le5/src/main/resources/com/ampalaya/images/humidity.png");
            // Image wicon = new Image("file:le5/src/main/resources/com/ampalaya/images/Windspeed.png");
            // humidicon.setImage(hicon);
            // windicon.setImage(wicon);

            // System.out.println("Humidity: " + humidity);
            // System.out.println("Weather Condition: " + weatherCondition);
            // System.out.println("Temperature: " + temperature);
            // System.out.println("Windspeed: " + windspeed);

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


    public void updateMapUI(double lat, double lon) {
        MapPoint newPoint = new MapPoint(lat, lon);
        for (Node node : Mainlayout.getChildren()) {
            if (node instanceof MapView) {
                mapView = (MapView) node;
                mapView.flyTo(0, newPoint, 8.0);
                break;
            }
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

        for (int i = 2; i < date.size();i++){
            String day = getDayoftheWeek(date.get(i).getAsString());
            String condition = convertWeatherCode(cond.get(i).getAsLong());
            otherDays.add(createDayWeather(day, condition));
        }
        String day = getDayoftheWeek(date.get(0).getAsString());
        String condition = convertWeatherCode(cond.get(0    ).getAsLong());
        otherDays.add(createDayWeather(day, condition));
        
        return otherDays;
    }

    @FXML
    private void setWeatherImage(String weathertype, ImageView weathericon) {
        switch (weathertype) {
            case "Clear":
                Image clearImage = new Image("file:le5/src/main/resources/com/ampalaya/images/CLEAR.png");
                weathericon.setImage(clearImage);
                break;
            case "Cloudy":
                Image cloudyImage = new Image("file:le5/src/main/resources/com/ampalaya/images/CLOUDY.png");
                weathericon.setImage(cloudyImage);
                break;
            case "Raining":
                Image rainingImage = new Image("file:le5/src/main/resources/com/ampalaya/images/RAINY.png");
                weathericon.setImage(rainingImage);
                break;
            case "Snowing":
                Image snowingImage = new Image("file:le5/src/main/resources/com/ampalaya/images/SNOWY.png");
                weathericon.setImage(snowingImage);
                break;
            default:
                weathericon.setImage(null);  // Default to no image if type is unknown
                break;
        }
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

    private MapView createMapView(){
        MapView mapView = new MapView();
        mapView.setPrefSize(500, 400);
        mapView.setZoom(13);
        mapView.flyTo(0, effielPoint, 0.1);
        
        return mapView;
    }

}

