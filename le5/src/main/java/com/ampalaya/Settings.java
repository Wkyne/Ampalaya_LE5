package com.ampalaya;

import java.io.IOException;
import javafx.fxml.FXML;

public class Settings {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("appgui");
    }
}