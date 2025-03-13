package com.zad8_bd;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class Main extends Application {
    public Connection connection;
    VBox tableList = new VBox();
    GridPane tableView = new GridPane();
    @Override
    public void start(Stage stage) {
        try { refreshTableList(); } catch (SQLException _) { }
        tableView.setHgap(10);
        tableView.setPadding(new Insets(15));
        GridPane root = new GridPane();
        tableList.setStyle("-fx-border-color: black; -fx-border-width: 0 2 0 0");
        Form dbName = new Form() {
            FormTextField dbName;
            @Override
            void init() {
                this.dbName = new FormTextField("Database");
                this.submitBtn = new Button("Connect");
                this.root.setStyle("-fx-border-color: black; -fx-border-width: 0 0 2 0;");
                this.root.setAlignment(Pos.CENTER_LEFT);
                this.root.setSpacing(5);
                this.root.getChildren().addAll(dbName.getHbox(), submitBtn, this.errorLabel);
                GridPane.setHgrow(root, Priority.ALWAYS);
            }

            @Override
            void submit() {
                if (dbName.getText().isEmpty()) {
                    error("The field cannot be empty.");
                    return;
                }
                String url = "jdbc:mysql://localhost:3306/" + dbName.getText();
                try {
                    connection = DriverManager.getConnection(url, "root", "");
                    refreshTableList();
                } catch (SQLException e) {
                    System.err.println("Connection failed (" + url + "): " + e.getMessage());
                }
            }
        };
        root.add(dbName.root, 0, 0);
        GridPane.setColumnSpan(dbName.root, 2);
        tableList.setPadding(new Insets(8));
        tableList.setSpacing(8);
        GridPane.setVgrow(tableList, Priority.ALWAYS);
        GridPane.setHgrow(tableView, Priority.ALWAYS);
        root.add(tableList, 0, 1);
        root.add(tableView, 1, 1);
        Scene scene = new Scene(root, 940, 780);
        stage.setTitle("");
        stage.setMinWidth(940);
        stage.setMinHeight(780);
        stage.setScene(scene);
        stage.show();
    }

    public void refreshTableList() throws SQLException {
        tableList.getChildren().clear();
        if (connection == null || connection.isClosed()) {
            return;
        }
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getTables(connection.getCatalog(), null, "%", null);
        while (rs.next()) {
            String tableName = rs.getString(3);
            Label tableNameBtn = new Label(tableName);
            tableNameBtn.setOnMouseClicked(_ -> {
                try {
                    createTableView(tableName);
                } catch (SQLException e) {
                    tableView.add(new Label(e.getMessage()), 0, 0);
                }
            });
            tableList.getChildren().add(tableNameBtn);
        }
        Button addTableBtn = new Button("New table");
        addTableBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            var creator = new TableCreator(connection);
            creator.form();
            creator.form.afterSubmit = nil -> {
                try { refreshTableList(); } catch (SQLException _) {}
                return nil;
            };
        });
        tableList.getChildren().add(addTableBtn);
    }

    public void createTableView(final String tableName) throws SQLException {
        Table table = new Table(tableName);
        tableView.getChildren().clear();
        Button insertBtn = new Button("INSERT data");
        Button editBtn = new Button("edit data");
        insertBtn.setOnMouseClicked(_ -> {
            RowCreator creator = new RowCreator(connection, table);
            creator.init();
            creator.form.afterSubmit = nil -> {
                try {
                    createTableView(tableName); // refresh
                } catch (SQLException e) {
                    creator.form.error("Failed to refresh table view: " + e.getMessage());
                }
                return nil;
            };
        });
        String tableNameFriendly = "`" + tableName + "`";
        editBtn.setOnMouseClicked(_ -> {
            RowEditor editor = new RowEditor(connection, table);
            editor.init();
            editor.form.afterSubmit = nil -> {
                try {
                    createTableView(tableName);
                } catch (SQLException e) {
                    editor.form.error("Failed to refresh table view: " + e.getMessage());
                }
                editor.stage.hide();
                return nil;
            };
        });
        tableView.add(insertBtn, 1, 1);
        tableView.add(editBtn, 2, 1);
        if (connection == null || connection.isClosed()) {
            return;
        }
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableNameFriendly);
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            String columnName = rsmd.getColumnName(i);
            int type = rsmd.getColumnType(i);
            boolean ai = rsmd.isAutoIncrement(i);
            table.addField(columnName, type, ai);
            var label = new Label(columnName);
            label.setStyle("-fx-font-weight: bold;");
            tableView.add(label, i, 2);
        }
        int row = 0;
        while (rs.next()) {
            ArrayList<String> strings = new ArrayList<>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                String s = rs.getString(i);
                Text text = new Text(s);
                tableView.add(text, i, 3 + row);
                strings.add(s);
            }
            table.stringifiedRowValues.add(strings);
            row++;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
