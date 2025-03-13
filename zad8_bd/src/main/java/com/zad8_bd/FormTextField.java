package com.zad8_bd;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class FormTextField extends TextField {
    private final Label label;

    public FormTextField(String label) {
        this.label = new Label(label);
    }
    public HBox getHbox() {
        HBox hbox = new HBox(label, this);
        hbox.setSpacing(7);
        hbox.setPadding(new Insets(10));
        hbox.setAlignment(Pos.CENTER_LEFT);
        return hbox;
    }
}
