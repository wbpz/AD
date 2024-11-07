package com.example.calc;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.function.Function;

class BorderedTitledPane extends StackPane {
    static final int Y_OFFSET = 20;
    BorderedTitledPane(String title, Node content) {
        setStyle("-fx-border-color: #888888; -fx-border-insets: 20 15 15 15; -fx-background-color:" + Main.defaultBg);
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-padding: 0px 5px 0px 5px;" +
                "-fx-font-weight: bold;" +
                "-fx-translate-y: " + (-Y_OFFSET - 10) +
                "; -fx-background-color:" + Main.defaultBg
        );
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
        setPadding(new Insets(Y_OFFSET, Main.X_OFFSET, Main.X_OFFSET, Y_OFFSET));
        getChildren().addAll(titleLabel, content);
    }
}

public class Main extends Application {
    static final int X_OFFSET = 15;
    static final String defaultBg = "#f0f0f0";
    static final int[] vatValues = { 5, 8, 23 };
    @Override
    public void start(Stage stage) {
        RadioButton calcNettoToBrutto = new RadioButton("Od netto do brutto");
        RadioButton calcBruttoToNetto = new RadioButton("Od brutto do netto");
        RadioButton calcVatBased = new RadioButton("Dopasuj do kwoty VAT");
        ToggleGroup methodGroup = new ToggleGroup();
        calcNettoToBrutto.setToggleGroup(methodGroup);
        calcBruttoToNetto.setToggleGroup(methodGroup);
        calcVatBased.setToggleGroup(methodGroup);

        VBox vbox = new VBox();
        VBox methods = new VBox(calcNettoToBrutto, calcBruttoToNetto, calcVatBased);
        methods.setSpacing(15d);
        Scene scene = new Scene(vbox);
        BorderedTitledPane method = new BorderedTitledPane("Metoda Obliczeń:", methods);

        GridPane dane = new GridPane();
        dane.setVgap(15);
        dane.setHgap(10);
        Label wartoscBazowa = new Label("Wartosc Bazowa:");
        Label stawkaVAT = new Label("Stawka VAT:");
        TextField value = new TextField();
        ObservableList<String> observableVatValues = FXCollections.observableArrayList();
        for (int x : vatValues) {
            observableVatValues.add(x + "%");
        }
        ChoiceBox<String> vat = new ChoiceBox<>(observableVatValues);
        dane.add(wartoscBazowa, 0, 0);
        dane.add(value, 1, 0);
        dane.add(stawkaVAT, 0, 1);
        dane.add(vat, 1, 1);
        BorderedTitledPane danePane = new BorderedTitledPane("Dane:", dane);

        StackPane buttons = new StackPane();
        Button obliczBtn = new Button("OBLICZ");
        Button zamknijBtn = new Button("ZAMKNIJ");
        buttons.setPadding(new Insets(0, X_OFFSET, 0, X_OFFSET));
        StackPane.setAlignment(obliczBtn, Pos.CENTER_LEFT);
        StackPane.setAlignment(zamknijBtn, Pos.CENTER_RIGHT);
        buttons.setStyle("-fx-background-color: " + defaultBg);
        buttons.getChildren().addAll(obliczBtn, zamknijBtn);

        GridPane wyniki = new GridPane();
        Label nettoLbl = addResultLabels("Netto", wyniki, 0);
        Label vatLbl = addResultLabels("VAT", wyniki, 1);
        Label bruttoLbl = addResultLabels("Brutto", wyniki, 2);
        Label errorOutput = new Label("");
        wyniki.add(errorOutput, 0, 3);
        wyniki.setHgap(50);
        BorderedTitledPane results = new BorderedTitledPane("Wyniki:", wyniki);

        zamknijBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> stage.close());

        obliczBtn.addEventHandler(MouseEvent.MOUSE_CLICKED, _ -> {
            errorOutput.setText("");
            Toggle selectedMethod = methodGroup.getSelectedToggle();
            var selectionModel = vat.getSelectionModel();
            if (selectedMethod == null) {
                errorOutput.setText("Nie wprowadzono metody.");
                return;
            }
            if (selectionModel == null) {
                errorOutput.setText("Nie wprowadzono wartości bazowej.");
                return;
            }
            int index = selectionModel.getSelectedIndex();
            if (index == -1) {
                errorOutput.setText("Nie wprowadzono stawki VAT.");
                return;
            }
            int selectedVat = vatValues[index];
            int inputValue;
            try {
                inputValue = Integer.parseInt(value.getText());
            } catch (NumberFormatException _) {
                errorOutput.setText("Wprowadzono niepoprawną liczbę.");
                return;
            }
            vatLbl.setText(Integer.toString(selectedVat) + "%");
            Function<Double, Double> nettoToBrutto = (x -> x + x * selectedVat / 100);
            if (selectedMethod.equals(calcNettoToBrutto)) {
                nettoLbl.setText(formatAsPln(inputValue));
                double bruttoValue = nettoToBrutto.apply((double)inputValue);
                bruttoLbl.setText(formatAsPln(bruttoValue));
            } else if (selectedMethod.equals(calcBruttoToNetto)) {
                bruttoLbl.setText(formatAsPln(inputValue));
                double nettoValue = (double)inputValue / (100 + selectedVat) * 100;
                nettoLbl.setText(formatAsPln(nettoValue));
            } else /* if (selectedMethod.equals(calcVatBased)) */ {
                double nettoValue = (double)inputValue / selectedVat * 100;
                double bruttoValue = nettoToBrutto.apply(nettoValue);
                nettoLbl.setText(formatAsPln(nettoValue));
                bruttoLbl.setText(formatAsPln(bruttoValue));
            }
        });

        vbox.getChildren().addAll(method, danePane, buttons, results);
        stage.setTitle("kalkulator vat");
        stage.setScene(scene);
        stage.show();
    }

    public static String formatAsPln(double x) {
        return String.format("%.2fzł", x);
    }

    public static Label addResultLabels(String label, GridPane layout, int yCoord) {
        layout.add(new Label(label), 0, yCoord);
        Label changeableLbl = new Label("--");
        layout.add(changeableLbl, 1, yCoord);
        return changeableLbl;
    }

    public static void main(String[] args) {
        launch();
    }
}