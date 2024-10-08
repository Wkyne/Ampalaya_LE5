module com.ampalaya {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive com.google.gson;
    requires com.gluonhq.maps;
 

    opens com.ampalaya to com.google.gson,javafx.fxml;
    exports com.ampalaya;
}
