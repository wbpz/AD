package com.zad8_bd;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.stream.Collectors;

class FormField {
    Label label;
    Node node;
    FormField(Label label, Node node) {
        this.label = label;
        this.node = node;
    }
}

public class RowCreator {
    public Form form;
    Connection connection;
    Table table;
    Stage stage = new Stage();
    public RowCreator(Connection conn, Table table) {
        this.connection = conn;
        this.table = table;
    }

    public void init() {
        form = new Form() {
            ArrayList<FormField> fields;
            @Override
            void init() {
                this.fields = new ArrayList<>();
                Button cancel = new Button("Cancel");
                cancel.setOnMouseClicked(_ -> stage.hide());
                for (TableField field : table.fields) {
                    Label label = new Label(field.name + " (" + field.type + ") ");
                    this.fields.add(new FormField(label, field.getFormInputNode()));
                }
                VBox vbox = new VBox();
                vbox.getChildren().addAll(fields.stream()
                    .map(r -> {
                        HBox hbox = new HBox(r.label, r.node);
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        hbox.setSpacing(5);
                        return hbox;
                    })
                    .collect(Collectors.toCollection(ArrayList::new))
                );
                this.submitBtn = new Button("INSERT data");
                HBox buttons = new HBox(this.submitBtn, cancel);
                buttons.setSpacing(5);
                vbox.getChildren().addAll(buttons, errorLabel);
                this.root.getChildren().add(vbox);
            }

            @Override
            void submit() {
                StringBuilder query = new StringBuilder("INSERT INTO ");
                query.append(table.name).append(" ");
                String fieldNames = Utils.repeatAndAddCommas(table.fields, field -> {
                    if (!field.shouldBeInsertedManually()) {
                        return null;
                    }
                    return field.name;
                });
                if (!fieldNames.isEmpty()) {
                    query.append("(").append(fieldNames).append(") ");
                }
                query.append(" VALUES (");
                String values = Utils.repeatAndAddCommas(table.fields, field -> {
                    if (!field.shouldBeInsertedManually()) {
                        return null;
                    }
                    String str = field.readInputNode();
                    if (str == null) {
                        error("Fields must be filled in.");
                        return null;
                    }
                    return field.readInputNode();
                });
                if (errored) {
                    return;
                }
                query.append(values).append(");");
                try {
                    Statement stmt = connection.createStatement();
                    stmt.execute(query.toString());
                } catch (SQLException e) {
                    error("Failed INSERT INTO: " + e.getMessage());
                }
            }
        };
        VBox root = new VBox(form.root);
        Scene scene = new Scene(root);
        this.stage.setTitle("INSERT INTO");
        this.stage.setMinWidth(600);
        this.stage.setMinHeight(500);
        this.stage.setScene(scene);
        this.stage.show();
    }

}
