package com.zad8_bd;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.Arrays;

public enum SQLType {
    INT,
    VARCHAR,
    BOOLEAN;

    @Override
    public String toString() {
        return switch (this) {
            case INT -> "int";
            case VARCHAR -> "varchar";
            case BOOLEAN -> "bool";
        };
    }

    public boolean takesAttribute() {
        return this == VARCHAR;
    }

    public static ComboBox<SQLType> getDropDownMenu() {
        ObservableList<SQLType> options = FXCollections.observableArrayList();
        options.addAll(Arrays.asList(SQLType.values()));
        return new ComboBox<>(options);
    }

    public String formatStringifiedValue(String v) {
        switch (this) {
            case VARCHAR: return "\"" + v + "\"";
            case INT: return v;
            case BOOLEAN: {
                if (v.length() != 1) {
                    throw new RuntimeException("BOOLEAN is not a BOOLEAN value");
                }
                if (v.getBytes()[0] == '0') {
                    return "false";
                } else {
                    return "true";
                }
            }
        };
        throw new RuntimeException("UNREACHABLE");
    }
}
