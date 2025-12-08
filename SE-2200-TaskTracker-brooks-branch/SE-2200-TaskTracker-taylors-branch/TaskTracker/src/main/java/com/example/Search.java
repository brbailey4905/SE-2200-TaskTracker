package com.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Search {


    public StackPane createSearch() {
        StackPane root = new StackPane();
        root.setPrefSize(600, 71);
        root.setStyle("-fx-background-color: transparent;");

        TextField textField = new TextField();
        textField.setPrefSize(600, 40);
        textField.setPromptText("Search");
        textField.setStyle("-fx-background-radius: 20; -fx-background-color: #FFFFFF; -fx-border-color: #000000; -fx-border-radius: 20;");
        textField.setPadding(new Insets(0, 0, 0, 40));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setHeight(30.3);
        dropShadow.setWidth(38.11);
        dropShadow.setRadius(16.6025);
        dropShadow.setOffsetY(4);
        dropShadow.setColor(new Color(0, 0, 0, 0.2519084));
        textField.setEffect(dropShadow);

        Label iconLabel = new Label();
        iconLabel.setPrefSize(586, 71);
        iconLabel.setMouseTransparent(true);
        StackPane.setAlignment(iconLabel, Pos.CENTER_RIGHT);

        ImageView searchImage = new ImageView();
        searchImage.setFitHeight(21);
        searchImage.setFitWidth(24);
        searchImage.setPreserveRatio(true);
        searchImage.setPickOnBounds(true);
        searchImage.setImage(new Image(getClass().getResourceAsStream("/com/example/icons/search.png")));

        iconLabel.setGraphic(searchImage);

        root.getChildren().add(textField);
        root.getChildren().add(iconLabel);

        return root;
    }
}
