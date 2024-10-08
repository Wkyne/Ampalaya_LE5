package com.ampalaya;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
// import java.util.Arrays;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        
        scene = new Scene(loadFXML("appgui"));
        stage.setScene(scene);
        stage.show();
        
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        // System.out.println(WeatherLocationApp.getTime());


        // Double[] result = WeatherLocationApp.getLocationdata("MANILA");
        // JsonObject outcome = WeatherLocationApp.getWeatherdata(result);
        // // Assuming outcome is your JsonObject containing the weather data
        // System.out.println("Location: MANILA \n" + 
        //                     "Humidity: " + outcome.get("humidity") + "\n" +
        //                     "Weather Condition: " + outcome.get("weatherCondition") +  "\n" +
        //                     "Temperature: " + outcome.get("temperature") +  "\n" +
        //                     "Windspeed: " + outcome.get("windspeed") + "\n" +
        //                     "Other Days' Weather:");

        // // Extract the 'otherDays' array
        // JsonArray otherDays = outcome.getAsJsonArray("otherDays");

        // if (otherDays != null) {
        //     // Loop through each day-weather pair in the otherDays array
        //     for (int i = 0; i < otherDays.size(); i++) {
        //         JsonObject dayWeather = otherDays.get(i).getAsJsonObject();
        //         String day = dayWeather.get("day").getAsString();
        //         String condition = dayWeather.get("condition").getAsString();
                
        //         // Print each day's weather condition
        //         System.out.println(day + ": " + condition);
        //     }
        // } else {
        //     System.out.println("No data available for other days.");
        // }


        

                            



        launch();
        

        
    }

}