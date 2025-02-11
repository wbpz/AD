module com.example.zad7_zip {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.zad7_zip to javafx.fxml;
    exports com.example.zad7_zip;
}