package com.example.zad6_cezar;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main extends Application {
    public final String ALPHABET_LOWERCASE = "aąbcćdeęfghijklłmnńoóprsśtuwyzźż";
    public final String ALPHABET_UPPERCASE = "AĄBCĆDEĘFGHIJKLŁMNŃOÓPRSŚTUWYZŹŻ";
    public final int ALPHABET_LEN = ALPHABET_LOWERCASE.length();
    public final String cipher(String input, int key) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            String str = String.valueOf(c);
            String alphabet = (ALPHABET_LOWERCASE.contains(str)
                             ? ALPHABET_LOWERCASE
                             : ALPHABET_UPPERCASE);
            // Neither of the alphabets contains the symbol, use ASCII
            if (!alphabet.contains(str)) {
                int character = c;
                c += key;
                c %= 256;
                out.append((char)character);
                continue;
            }
            int j = alphabet.indexOf(str);
            j = (j + key);
            if (j < 0) {
                j = ALPHABET_LEN + j;
            } else if (j >= ALPHABET_LEN) {
                j %= ALPHABET_LEN;
            }
            out.append(alphabet.charAt(j));
        }
        return out.toString();
    }

    private void appendText(TextArea output, TextField input, TextArea fileInput, TextField inputKey) {
        String path = input.getText();
        List<String> content = Arrays.asList(fileInput.getText().split("\\n"));
        ArrayList<String> cipheredContent = new ArrayList<>();
        int key;
        try {
            String text = inputKey.getText();
            key = Integer.parseInt(text);
        } catch (NumberFormatException exc) {
            output.setText("Key is not a number.");
            return;
        }
        try {
            for (String s : content) {
                cipheredContent.add(cipher(s, key));
            }

            File.writeFileLines(path, cipheredContent);
        } catch (IOException err) {
            output.setText("ERROR: " + err.getMessage() + ".");
        }
    }

    @Override
    public void start(Stage stage) {
        Label inputLabel = new Label("Ścieżka odczyt: ");
        Label inputKeyLabel = new Label("Klucz szyfru: ");
        TextField input = new TextField();
        TextField inputKey = new TextField();
        Button readFileBtn = new Button("Odczytaj");
        TextArea output = new TextArea();
        FlowPane inputPane1 = new FlowPane(inputLabel, input, inputKeyLabel, inputKey, readFileBtn);
        // FlowPane inputPane2 = new FlowPane(inputKeyLabel, inputKey);
        VBox inputPane = new VBox(inputPane1);
        inputPane1.setHgap(15d);
        inputPane1.setPadding(new Insets(20, 0, 0, 20));
        output.setEditable(false);
        readFileBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            output.clear();
            String path = input.getText();
            int key;
            try {
                String text = inputKey.getText();
                key = Integer.parseInt(text);
            } catch (NumberFormatException exc) {
                output.setText("Key is not a number.");
                return;
            }
            try {
                List<String> content = File.getFileLines(path);
                // content.forEach(System.out::println);
                content.forEach(c -> output.appendText(cipher(c, key) + "\n"));
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
            appendText(output, input, fileInput, inputKey);
        });
        overwriteTextBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            String path = input.getText();
            try {
                File.clearFile(path);
            } catch (IOException err) {
                output.setText("ERROR: " + err.getMessage() + ".");
            }
            appendText(output, input, fileInput, inputKey);
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
