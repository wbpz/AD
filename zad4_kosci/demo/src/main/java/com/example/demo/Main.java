package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    static int totalScore = 0;
    static ArrayList<Integer> diceValues = new ArrayList<>();
    public static int getScore() {
        int[] numberCount = new int[6];
        for (int i = 0; i < 6; i++) numberCount[i] = 0;
        for (int val : diceValues) {
            numberCount[val - 1] += 1;
        }
        int score = 0;
        for (int i = 0; i < 6; i++) {
            int val = numberCount[i];
            score += (val / 2) * 2 * (i + 1);
        }
        return score;
    }
    public static Circle getCircle() { return new Circle(4); }
    public static Circle getTransparentCircle() {
        Circle circle = getCircle();
        circle.setFill(Color.color(0, 0, 0, 0));
        circle.setId("transparent");
        return circle;
    }
    public static void generateInnerDice(GridPane dice) {
        int value = (int)Math.round(Math.random() * 5) + 1;
        diceValues.add(value);
        generateDiceDots(dice, value);
    }
    public static FlowPane generateOutputGetInner(int diceAmount) {
        FlowPane innerRow = new FlowPane();
        innerRow.setHgap(10d);
        innerRow.setAlignment(Pos.CENTER);
        GridPane[] dices = new GridPane[diceAmount];
        for (int i = 0; i < diceAmount; i++) {
            GridPane dice = new GridPane();
            dice.setBorder(new Border(new BorderStroke(Color.BLACK,
                    BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            dice.setPadding(new Insets(5));
            dices[i] = dice;
            generateInnerDice(dice);
        }
        innerRow.getChildren().addAll(dices);
        return innerRow;
    }
    public static final String DICE_PANEL_ID = "OUTER_DICES___ID";
    public static VBox generateOutput(int diceCount, VBox root) {
        diceValues.clear();
        int dicesPerRow = diceCount / 2;
        FlowPane inner1 = generateOutputGetInner(dicesPerRow);
        FlowPane inner2 = generateOutputGetInner(diceCount - dicesPerRow);
        FlowPane outer = new FlowPane(inner1, inner2);
        outer.setOrientation(Orientation.VERTICAL);
        outer.setVgap(10d);
        totalScore += getScore();
        Label score = new Label("Wynik: " + totalScore);
        Button resetButton = new Button("Resetuj wynik");
        resetButton.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            totalScore = 0;
            removeDices(root);
            VBox emptyOutput = generateOutput(0, root);
            root.getChildren().add(emptyOutput);
        });
        HBox scorePanel = new HBox(score, resetButton);
        scorePanel.setSpacing(15);
        VBox dicesWithScore = new VBox(outer, scorePanel);
        scorePanel.setAlignment(Pos.CENTER_LEFT);
        dicesWithScore.setSpacing(10d);
        dicesWithScore.setId(DICE_PANEL_ID);
        return dicesWithScore;
    }
    public static void removeDices(VBox root) {
        root.getChildren().removeIf(child -> child.getId() != null && child.getId().equals(DICE_PANEL_ID));
    }
    @Override
    public void start(Stage stage) {
        Label inputLabel = new Label("Ile kostek chcesz rzucić? (3 - 10)");
        TextField input = new TextField();
        Button submit = new Button("Zatwierdź");
        Label result = new Label();
        FlowPane flowPane = new FlowPane(inputLabel, input, submit);
        flowPane.setHgap(10d);
        VBox root = new VBox(flowPane, result);
        VBox emptyOutput = generateOutput(0, root);
        root.getChildren().add(emptyOutput);
        submit.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            int inputValue;
            try {
                inputValue = Integer.parseInt(input.getText());
            } catch (NumberFormatException _) {
                result.setText("Wejście nie jest liczbą!");
                return;
            }
            if (inputValue < 3 || inputValue > 10) {
                result.setText("Wartość musi mieścić się w zakresie od 3 do 10!");
                return;
            }
            removeDices(root);
            root.getChildren().add(generateOutput(inputValue, root));
        });
        root.setPadding(new Insets(20, 20, 20, 20));
        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void generateDiceDots(GridPane dice, int dotsCount) {
        dice.setHgap(2);
        Circle circle = getCircle();
        switch (dotsCount) {
            case 1:
                dice.add(getTransparentCircle(), 0, 0);
                dice.add(circle, 1, 1);
                dice.add(getTransparentCircle(), 2, 2);
                break;
            case 2:
                dice.add(getCircle(), 0, 2);
                dice.add(getTransparentCircle(), 1, 1);
                dice.add(getCircle(), 2, 0);
                break;
            case 3:
                dice.add(getCircle(), 0, 2);
                dice.add(getCircle(), 1, 1);
                dice.add(getCircle(), 2, 0);
                break;
            case 4:
                dice.add(getCircle(), 0, 0);
                dice.add(getTransparentCircle(), 1, 1);
                dice.add(getCircle(), 0, 2);
                dice.add(getCircle(), 2, 0);
                dice.add(getCircle(), 2, 2);
                break;
            case 5:
                generateDiceDots(dice, 4);
                for (Node c : dice.getChildren()) {
                    if (c.getId() != null && c.getId().equals("transparent")) {
                        ((Circle)c).setFill(Color.BLACK);
                    }
                }
                break;
            case 6:
                for (int i = 0; i < 6; i++) {
                    dice.add(getCircle(), i % 3, ((i / 3)) * 2);
                }
                dice.add(getTransparentCircle(), 1, 1);
                break;
        }
    }
}