package com.example.zad5_files;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Label inputLabel = new Label("Ścieżka odczyt: ");
        TextField input = new TextField();
        Button readFileBtn = new Button("Odczytaj");
        TextArea output = new TextArea();
        FlowPane inputPane = new FlowPane(inputLabel, input, readFileBtn);
        inputPane.setHgap(15d);
        inputPane.setPadding(new Insets(20, 0, 0, 20));
        output.setEditable(false);
        readFileBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            output.clear();
            String path = input.getText();
            try {
                List<String> content = File.getFileLines(path);
                // content.forEach(System.out::println);
                content.forEach(c -> output.appendText(c + "\n"));
            } catch (IOException err) {
                output.setText("ERROR: " + err.getMessage() + ".");
            }
        });
        TextArea fileInput = new TextArea();
        Button appendTextBtn = new Button("Dodaj do pliku");
        Button overwriteTextBtn = new Button("Nadpisz plik");
        FlowPane btns = new FlowPane(appendTextBtn, overwriteTextBtn);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.setHgap(15d);

        appendTextBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
           String path = input.getText();
           List<String> content = Arrays.asList(fileInput.getText().split("\\n"));
           try {
               File.writeFileLines(path, content);
           } catch (IOException err) {
               output.setText("ERROR: " + err.getMessage() + ".");
           }
        });
        overwriteTextBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            String path = input.getText();
            List<String> content = Arrays.asList(fileInput.getText().split("\\n"));
            try {
                File.clearFile(path);
                File.writeFileLines(path, content);
            } catch (IOException err) {
                output.setText("ERROR: " + err.getMessage() + ".");
            }
        });
        btns.setPadding(new Insets(0, 20, 20, 0));
        VBox root = new VBox(inputPane, output, fileInput, btns);
        root.setSpacing(15d);
        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}