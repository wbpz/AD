module com.zad8_bd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires java.desktop;


    opens com.zad8_bd to javafx.fxml;
    exports com.zad8_bd;
}