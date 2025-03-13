package com.zad8_bd;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

public class TableCreator {
    Connection connection;
    ArrayList<TableFieldForm> fields = new ArrayList<>();
    Stage stage = new Stage();
    VBox root = new VBox();
    Form form;

    TableCreator(Connection conn) {
        connection = conn;
        TableField id = new TableField("id", SQLType.INT);
        TableFieldForm form = new TableFieldForm(id);
        form.attributes.setSelected(FieldAttribute.AUTO_INCREMENT);
        form.attributes.setSelected(FieldAttribute.PRIMARY_KEY);
        fields.add(form);
    }
    public void form() {
        form = new Form() {
            FormTextField tableName;
            VBox fieldGui;
            Button cancel;
            @Override
            void init() {
                this.tableName = new FormTextField("Table name:");
                this.root.setPadding(new Insets(20));
                this.cancel = new Button("Cancel");
                this.submitBtn = new Button("Create");
                refreshFieldGui();
                createRoot();
                cancel.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> stage.hide());
            }
            private void refreshFieldGui() {
                this.fieldGui = new VBox();
                fieldGui.setSpacing(5);
                for (TableFieldForm field : fields) {
                    fieldGui.getChildren().add(field.getHBox());
                }
                Button nextFieldButton = new Button("+");
                fieldGui.getChildren().add(nextFieldButton);
                nextFieldButton.setOnMouseClicked(_ -> {
                    TableField field = new TableField("", SQLType.INT);
                    fields.add(new TableFieldForm(field));
                    refreshFieldGui();
                    createRoot();
                });
            }
            private void createRoot() {
                HBox buttons = new HBox(submitBtn, cancel);
                buttons.setSpacing(10);
                ScrollPane fieldGuiScroll = new ScrollPane(fieldGui);
                fieldGuiScroll.setBorder(null);
                VBox vbox = new VBox(tableName.getHbox(), fieldGuiScroll, buttons, errorLabel);
                vbox.setSpacing(7);
                this.root.getChildren().clear();
                this.root.getChildren().addAll(vbox);
            }
            @Override
            void submit() {
                StringBuilder query = new StringBuilder("CREATE TABLE ");
                String name = this.tableName.getText();
                if (name.isEmpty()) {
                    error("Table name cannot be empty!");
                    return;
                }
                if (!Utils.isAValidSQLIdentifier(name)) {
                    error("Table name must be a valid SQL identifier!");
                    return;
                }
                query.append(name).append("(");
                StringBuilder fieldsBuilder = new StringBuilder();
                for (TableFieldForm f : fields) {
                    fieldsBuilder.append(f.name.getText()).append(" ");
                    if (f.name.getText().isEmpty()) {
                        error("Field name cannot be empty!");
                        return;
                    }
                    if (!Utils.isAValidSQLIdentifier(f.name.getText())) {
                        error("Field name must be a valid SQL identifier!");
                        return;
                    }
                    SQLType type = f.getType();
                    if (type == null) {
                        error("Must choose a type for all fields!");
                        return;
                    }
                    fieldsBuilder.append(type);
                    if (type.takesAttribute()) {
                        if (f.typeAttr.getText().isEmpty()) {
                            error("Type of `" + f.name.getText() + "` takes a parameter!");
                            return;
                        }
                        fieldsBuilder.append('(').append(f.typeAttr.getText()).append(')');
                    }
                    fieldsBuilder.append(" ");
                    for (FieldAttribute a : f.attributes.getAllSelected()) {
                        fieldsBuilder.append(a.toString()).append(" ");
                    }
                    fieldsBuilder.append(", ");
                }
                String fieldsString = Utils.stringTrimLastCharacters(fieldsBuilder.toString(), 2); // ", "
                query.append(fieldsString).append(");");
                try {
                    Statement stmt = connection.createStatement();
                    stmt.execute(query.toString());
                } catch (SQLException e) {
                    error("SQL Error: " + e.getMessage());
                }
                stage.hide();
            }
        };
        root = new VBox(form.root);
        Scene scene = new Scene(root);
        this.stage.setTitle("CREATE TABLE");
        this.stage.setMinWidth(600);
        this.stage.setMinHeight(500);
        this.stage.setScene(scene);
        this.stage.show();
    }
}

class TableFieldForm {
    TextField name;
    TextField typeAttr;
    CheckComboBox<FieldAttribute> attributes;
    ComboBox<SQLType> type;
    SQLType getType() {
        return type.getSelectionModel().getSelectedItem();
    }
    TableFieldForm(TableField field) {
        this.name = new TextField(field.name);
        this.type = SQLType.getDropDownMenu();
        this.typeAttr = new TextField();
        this.type.addEventHandler(ActionEvent.ANY, _ -> refreshForm());
        this.attributes = FieldAttribute.getCheckComboBox();
        attributes.onCheckPress = _ -> {
            refreshForm();
            return null;
        };
        for (FieldAttribute a : field.attributes) {
            this.attributes.setSelected(a);
        }
        this.type.setValue(field.type);
        refreshForm();
    }
    public void refreshForm() {
        SQLType type = getType();
        if (type != null && type.takesAttribute()) {
            this.typeAttr.setDisable(false);
        } else {
            this.typeAttr.setDisable(true);
            this.typeAttr.clear();
        }
        for (var item : this.attributes.getItems()) {
            boolean attributeCanBeUsed = Arrays.asList(item.item.getAllowedTypes()).contains(type);
            item.setCheckboxDisable(!attributeCanBeUsed);
            if (!attributeCanBeUsed) {
                item.setChecked(false);
            }
        }
    }
    HBox getHBox() {
        HBox hbox = new HBox(this.name, this.type, this.typeAttr, this.attributes);
        hbox.setSpacing(5);
        return hbox;
    }
}
