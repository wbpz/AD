package com.zad8_bd;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class RowEditor {
    public Form form;
    Connection connection;
    Table table;
    Stage stage;
    RowEditor(Connection connection, Table table) {
        this.connection = connection;
        this.table = table;
        this.stage = new Stage();
    }
    void init() {
        form = new Form() {
            ComboBox<String> availableRows;
            HashMap<String, Integer> displayToIndex;
            VBox fieldsGUI;
            @Override
            void init() {
                displayToIndex = new HashMap<>();
                fieldsGUI = new VBox();
                VBox vBox = new VBox();
                Button delete = new Button("DELETE ROW");
                this.createSubmitButton(delete, _ -> {
                    String query = "DELETE FROM " + table.name + " " + constructWhere();
                    try {
                        Statement stmt = connection.createStatement();
                        stmt.execute(query);
                    } catch (SQLException e) {
                        System.err.println(e.getMessage());
                    }
                });
                ObservableList<String> availableRowsOl = FXCollections.observableArrayList();
                int i = 1;
                for (ArrayList<String> s : table.stringifiedRowValues) {
                    String display = i + ". " + s.getFirst();
                    availableRowsOl.add(display);
                    displayToIndex.put(display, i - 1);
                    i += 1;
                }
                availableRows = new ComboBox<>(availableRowsOl);
                availableRows.valueProperty().addListener(_ -> {
                    fieldsGUI.getChildren().clear();
                    String item = availableRows.getSelectionModel().getSelectedItem();
                    ArrayList<String> values = table.stringifiedRowValues.get(displayToIndex.get(item));
                    int j = 0;
                    for (String value : values) {
                        TableField data = table.fields.get(j);
                        Label label = new Label(data.name + " (" + data.type + ") " + ". " + value + " => ");
                        Node input = data.getFormInputNode();
                        HBox hbox = new HBox(label, input);
                        hbox.setSpacing(5);
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        fieldsGUI.getChildren().add(hbox);
                        j += 1;
                    }
                });
                this.submitBtn = new Button("UPDATE ROW");
                HBox buttons = new HBox(delete, submitBtn);
                vBox.getChildren().addAll(availableRows, fieldsGUI, buttons, errorLabel);
                this.root.getChildren().add(vBox);
            }

            @Override
            void submit() {
                String where = constructWhere();
                String item = availableRows.getSelectionModel().getSelectedItem();
                ArrayList<String> values = table.stringifiedRowValues.get(displayToIndex.get(item));
                StringBuilder query = new StringBuilder("UPDATE ");
                query.append(table.name).append(" SET ");
                boolean setField = false;
                for (TableField field : table.fields) {
                    if (!field.shouldBeInsertedManually()) {
                        continue;
                    }
                    String value = field.readInputNode();
                    if (value == null) {
                        continue;
                    }
                    setField = true;
                    query.append(field.name).append(" = ").append(value).append(", ");
                }
                String queryStr = Utils.stringTrimLastCharacters(query.toString(), ", ".length());
                if (!setField) {
                    error("No values to change!");
                    return;
                }
                try {
                    Statement stmt = connection.createStatement();
                    stmt.execute(queryStr + " " + where);
                } catch (SQLException e) {
                    error("ERROR: " + e.getMessage());
                }
            }
            String constructWhere() {
                StringBuilder s = new StringBuilder("WHERE ");
                String item = availableRows.getSelectionModel().getSelectedItem();
                ArrayList<String> values = table.stringifiedRowValues.get(displayToIndex.get(item));
                int i = 0;
                for (TableField field : table.fields) {
                    String formatted = field.type.formatStringifiedValue(values.get(i));
                    s.append(field.name).append(" = ").append(formatted);
                    s.append(" AND ");
                    i += 1;
                }
                return Utils.stringTrimLastCharacters(s.toString(), " AND ".length());
            }
        };
        VBox root = new VBox(form.root);
        Scene scene = new Scene(root);
        this.stage.setTitle("RowEditor");
        this.stage.setMinWidth(600);
        this.stage.setMinHeight(500);
        this.stage.setScene(scene);
        this.stage.show();
    }
}
