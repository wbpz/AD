package com.zad8_bd;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.function.Function;

public abstract class Form {
    HBox root = new HBox();
    Button submitBtn = new Button();
    Label errorLabel = new Label("");
    boolean errored = false;
    public Function<Void, Void> afterSubmit;
    Form() {
        this.init();
        createSubmitButton(submitBtn, _ -> {
            submit();
        });
    }
    protected void createSubmitButton(Button btn, EventHandler<? super MouseEvent> action) {
        btn.setOnMouseClicked(a -> {
            errorLabel.setText("");
            errored = false;
            action.handle(a);
            if (afterSubmit != null) {
                afterSubmit.apply(null);
            }
        });
    }
    abstract void init();
    abstract void submit();
    void error(String s) {
        errorLabel.setText(s);
        errored = true;
    }
}
