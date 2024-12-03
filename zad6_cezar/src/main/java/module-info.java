module com.example.zad6_cezar {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.zad6_cezar to javafx.fxml;
    exports com.example.zad6_cezar;
}