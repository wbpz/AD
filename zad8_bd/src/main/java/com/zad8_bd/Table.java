package com.zad8_bd;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;

class TableField {
    public String name;
    public SQLType type;
    public ArrayList<FieldAttribute> attributes;
    public TableField(String name, SQLType type) {
        this.name = name;
        this.type = type;
        this.attributes = new ArrayList<>();
    }
    private Node generatedInputNode;

    @Override
    public String toString() {
        return "Field { name=" + name +", type=" + type + ", attributes="+attributes+ " }";
    }

    public boolean shouldBeInsertedManually() {
        return !attributes.contains(FieldAttribute.AUTO_INCREMENT);
    }

    private Node getFormInputNode_() {
        switch (this.type) {
            case INT -> {
                TextField field = new TextField();
                if (attributes.contains(FieldAttribute.AUTO_INCREMENT)) {
                    field.setDisable(true);
                    field.setText("Cannot set the value of an AUTO_INCREMENT field");
                    field.setPrefWidth(300);
                    return field;
                }
                field.addEventFilter(KeyEvent.KEY_TYPED, event -> {
                    if (!event.getCharacter().chars().allMatch(Character::isDigit)) {
                        event.consume();
                    }
                });
                return field;
            }
            case VARCHAR -> {
                return new TextField();
            }
            case BOOLEAN -> {
                return new CheckBox();
            }
            default -> throw new RuntimeException("Unhandled SQLType: " + this);
        }
    }

    public Node getFormInputNode() {
        generatedInputNode = getFormInputNode_();
        return generatedInputNode;
    }

    public String readInputNode() {
        if (generatedInputNode == null) {
            throw new RuntimeException("Attempt to read a Node not generatedInput yet by this Field!");
        }
        return switch (this.type) {
            case BOOLEAN -> String.valueOf(((CheckBox)generatedInputNode).isSelected());
            case VARCHAR -> "\"" + ((TextField)generatedInputNode).getText() + "\"";
            default -> ((TextField)generatedInputNode).getText();
        };
    }
}

public class Table {
    String name;
    ArrayList<TableField> fields;
    ArrayList<ArrayList<String>> stringifiedRowValues;

    public Table(String name) {
        this.fields = new ArrayList<>();
        this.name = name;
        this.stringifiedRowValues = new ArrayList<>();
    }

    public void addField(String name, int type, boolean isAutoIncrement) {
        SQLType sqlType = switch(type) {
            case java.sql.Types.INTEGER -> SQLType.INT;
            case java.sql.Types.VARCHAR -> SQLType.VARCHAR;
            case java.sql.Types.BOOLEAN,
                 java.sql.Types.TINYINT,
                 java.sql.Types.BIT -> SQLType.BOOLEAN;
            default -> throw new RuntimeException("unsupported type " + type);
        };
        var field = new TableField(name, sqlType);
        if (isAutoIncrement) {
            field.attributes.add(FieldAttribute.AUTO_INCREMENT);
        }
        fields.add(field);
    }
}
