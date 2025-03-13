package com.zad8_bd;

import java.util.Arrays;

import static com.zad8_bd.SQLType.*;

public enum FieldAttribute {
    NOT_NULL("NOT NULL"),
    UNIQUE("UNIQUE"),
    AUTO_INCREMENT("AUTO_INCREMENT", new SQLType[]{INT}),
    PRIMARY_KEY("PRIMARY KEY");

    private final String val;
    private final SQLType[] allowedForTypes;
    FieldAttribute(String s, SQLType[] types) {
        val = s;
        allowedForTypes = types;
    }

    FieldAttribute(String s) {
        val = s;
        allowedForTypes = SQLType.values();
    }

    public static CheckComboBox<FieldAttribute> getCheckComboBox() {
        return new CheckComboBox<>(Arrays.asList(FieldAttribute.values()));
    }

    @Override
    public String toString() {
        return val;
    }

    public SQLType[] getAllowedTypes() {
        return allowedForTypes;
    }
}
