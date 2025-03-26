package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.function.Function;

import static com.example.demo.ViewBuilder.FOUR_SP;

public class HelloApplication extends Application {
    String key;
    private String makeRequest(String city) throws IOException {
        String link = "https://api.openweathermap.org/data/2.5/weather?units=metric&q=" +
                city +
                "&appid=" +
                key +
                "&lang=pl";
        try {
            URI uri = new URI(link);
            HttpURLConnection conn = (HttpURLConnection)(uri.toURL().openConnection());
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line;
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                builder.append(line);
            }
            return builder.toString();
        } catch (MalformedURLException | URISyntaxException e) {
            return null;
        }
    }
    @Override
    public void start(Stage stage) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("key.txt"));
            key = reader.readLine();
        } catch (FileNotFoundException e) {
            System.out.println("Could not read key.txt!");
            return;
        }
        Button exit = new Button("Zamknij");
        Button submit = new Button("🔎");
        Label label = new Label("API");
        Label miastoLbl = new Label("miasto: ");
        TextField miastoTxt = new TextField();
        ImageView imageView = new ImageView();
        HBox miastoHbox = new HBox(miastoLbl, miastoTxt, submit, imageView);
        miastoHbox.setAlignment(Pos.CENTER_LEFT);
        miastoHbox.setSpacing(5d);

        VBox data = new VBox();
        VBox vbox = new VBox(label, miastoHbox, data, exit);

        submit.setOnMouseClicked(_ -> {
            String text = miastoTxt.getText();
            String response;
            try {
                response = makeRequest(text);
                // response = "{\"coord\":{\"lon\":-121.9358,\"lat\":37.7021},\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"bezchmurnie\",\"icon\":\"01n\"}],\"base\":\"stations\",\"main\":{\"temp\":277.44,\"feels_like\":275.15,\"temp_min\":276.07,\"temp_max\":279.99,\"pressure\":1028,\"humidity\":89,\"sea_level\":1028,\"grnd_level\":1001},\"visibility\":10000,\"wind\":{\"speed\":2.57,\"deg\":60},\"clouds\":{\"all\":0},\"dt\":1742390861,\"sys\":{\"type\":2,\"id\":2096680,\"country\":\"US\",\"sunrise\":1742393536,\"sunset\":1742437110},\"timezone\":-25200,\"id\":5344157,\"name\":\"Dublin\",\"cod\":200}\n";
            } catch (IOException exc) {
                return;
            }
            if (response == null) return;
            JSONObject json = new JSONObject(response);
            String icon = json.getJSONArray("weather").getJSONObject(0).getString("icon");
            System.out.println(json.toString(4));
            System.out.println(response);
            System.out.println(icon);
            data.getChildren().clear();
            data.getChildren().add(getData(json, imageView));
        });

        exit.setOnMouseClicked(_ -> stage.hide());
        Scene scene = new Scene(vbox, 640, 480);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    private TextArea getData(JSONObject o, ImageView viewOutput) {
        String icon = o.getJSONArray("weather").getJSONObject(0).getString("icon");
        String url = "https://openweathermap.org/img/wn/" + icon + ".png";
        Image img = new Image(url);
        viewOutput.setImage(img);
        TextArea text = new TextArea();
        JSONObject main = o.getJSONObject("main");
        JSONObject wind = o.getJSONObject("wind");
        JSONObject weather = o .getJSONArray("weather").getJSONObject(0);
        String dir_ = degreesToDir(wind.getInt("deg"));
        final String dir = dir_ + " (" + dirToPolish(dir_) + ")";

        ViewBuilder view = new ViewBuilder();
        view.append("Miasto", o.getString("name"))
                .appendTitle("Temperatura").withIndent(FOUR_SP, b ->
                    b.append("Teraz", main.getBigDecimal("temp"), "°C")
                    .append("Minimalna", main.getBigDecimal("temp_min"), "°C")
                    .append("Maksymalna", main.getBigDecimal("temp_max"), "°C")
                    .append("W odczuciu", main.getBigDecimal("feels_like"), "°C")
        ).appendTitle("Cisnienie").withIndent(FOUR_SP, b ->
                    b.append("Cisnienie", main.getInt("pressure"), "hPa")
                    .append("Na poziomie ziemi", main.getInt("grnd_level"), "hPa")
                    .append("Na poziomie morza", main.getInt("sea_level"), "hPa")
        ).appendTitle("Wiatr").withIndent(FOUR_SP, b ->
                    b.append("Predkosc", wind.getBigDecimal("speed"), "m/sek")
                    .append("Kierunek", dir)
        )
                .append("Widocznosc", formatVisibility(o.getInt("visibility")))
                .append("Wilgotnosc", main.getInt("humidity"), "%")
                .append("Zachmurzenie", o.getJSONObject("clouds").getInt("all"), "%")
                .append("Pogoda", weather.getString("description"));
        if (o.has("rain")) {
            view.append("Deszcz", o.getJSONObject("rain").getInt("1h"), "mm/h");
        }
        if (o.has("snow")) {
            view.append("Snieg", o.getJSONObject("rain").getInt("1h"), "mm/h");
        }
        text.setText(view.toString());
        text.setEditable(false);
        return text;
    }

    /*
     * N: 1-22
     * NE: 23-67
     * E: 68-112
     * SE: 113-157
     * S: 158-202
     * SW: 203-247
     * W: 248-292
     * NW: 293-337
     * N: 338-360
     */
    public static String degreesToDir(int degrees) {
        if (!(degrees >= 1 && degrees <= 360)) {
            throw new RuntimeException("Invalid value for degreesToDir");
        }
        String[] dirs = { "N", "NE", "E", "SE", "S", "SW", "W", "NW", "N" };
        degrees += (45 / 2);
        int index = degrees / 45;
        return dirs[index];
    }

    private static String dirToPolish(String dir) {
        return switch (dir) {
            case "N" -> "północny";
            case "NE" -> "północno-wschodni";
            case "NW" -> "północno-zachodni";
            case "W" -> "zachodni";
            case "E" -> "wschodni";
            case "S" -> "południowy";
            case "SE" -> "południowo-wschodni";
            case "SW" -> "południowo-zachodni";
            default -> throw new RuntimeException("Invalid argument: " + dir);
        };
    }

    private static String formatVisibility(int visibility) {
        double vis = ((double)visibility) / 100;
        if ((int)vis == vis) {
            return (int)vis + "%";
        } else {
            return vis + "%";
        }
    }

    public static void main(String[] args) {
        launch();
    }
}

class ViewBuilder {
    public static String FOUR_SP = "    ";
    StringBuilder b;
    private String indent = "";
    ViewBuilder() {
        b = new StringBuilder();
    }

    public ViewBuilder appendTitle(String title) {
        b.append(indent).append(title).append("\n");
        return this;
    }

    public ViewBuilder withIndent(String indent, Function<ViewBuilder, ViewBuilder> do_) {
        this.indent = indent;
        do_.apply(this);
        this.indent = "";
        return this;
    }

    public <T> ViewBuilder append(String title, T keyValue) {
        return append(title, keyValue, "");
    }

    public <T> ViewBuilder append(String title, T keyValue, String postfix) {
        b.append(indent).append(title).append(": ").append(keyValue.toString()).append(postfix).append("\n");
        return this;
    }

    @Override
    public String toString() {
        return b.toString();
    }
}
