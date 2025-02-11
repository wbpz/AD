package com.example.zad7_zip;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Label inputLabel = new Label("Wejście: ");
        TextField inputTxt = new TextField();
        HBox input = new HBox(inputLabel, inputTxt);
        Label outputLabel = new Label("Wyjście: ");
        TextField outputTxt = new TextField();
        HBox output = new HBox(outputLabel, outputTxt);
        Button zip = new Button("ZIP");
        Button unzip = new Button("UNZIP");
        Label outputInfo = new Label("");
        zip.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            String outputPath = outputTxt.getText();
            String inputPath = inputTxt.getText();
            try {
                Zip.fileToZip(inputPath, outputPath);
                outputInfo.setText("Succesfully zipped " + inputPath + " into " + outputPath);
            } catch (IOException e) {
                outputInfo.setText("Failed zipping " + inputPath + ": " + e.getMessage());
            }
        });
        unzip.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            String outputPath = outputTxt.getText();
            String inputPath = inputTxt.getText();
            try {
                Zip.zipDecompressor(inputPath, outputPath);
                outputInfo.setText("Succesfully unzipped " + inputPath + " into " + outputPath);
            } catch (IOException e) {
                outputInfo.setText("Failed unzipping " + inputPath + ": " + e.getMessage());
            }
        });
        VBox root = new VBox(input, output, zip, unzip, outputInfo);
        Scene scene = new Scene(root, 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}