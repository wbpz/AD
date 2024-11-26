module com.example.zad5_files {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.zad5_files to javafx.fxml;
    exports com.example.zad5_files;
}