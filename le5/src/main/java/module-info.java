module com.ampalaya {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;

    opens com.ampalaya to javafx.fxml;
    exports com.ampalaya;
}
