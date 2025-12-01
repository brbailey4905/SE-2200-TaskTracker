package com.example;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.time.YearMonth;

public class Calendar {

    public VBox createCalendar() {
        VBox root = new VBox();
        root.getStylesheets().addAll(this.getClass().getResource("/com/example/style.css").toExternalForm());
        root.setAlignment(Pos.TOP_CENTER);
        root.setPrefSize(400, 350);
        root.setMinSize(350, 300);
        root.setMaxSize(550, 500);
        root.setStyle("-fx-background-radius: 15; -fx-background-color: #FFFCF3;-fx-border-radius: 15; -fx-border-color: #000000;");

        HBox topButtons = new HBox();
        topButtons.setAlignment(Pos.CENTER);
        topButtons.setPrefHeight(55);
        topButtons.setPrefWidth(474);
        topButtons.setSpacing(10);

        Button previousMonth = new Button("<");
        previousMonth.setPrefSize(71, 41);
        previousMonth.setStyle("-fx-background-color: transparent;");
        topButtons.getChildren().add(previousMonth);

        Region topMenuSpaceBefore = new Region();
        topMenuSpaceBefore.setPrefSize(75, 75);
        topButtons.getChildren().add(topMenuSpaceBefore);

        YearMonth ym = YearMonth.now();

        Text month = new Text(populateYearMonth(ym));
        topButtons.getChildren().add(month);

        Region topMenuSpaceAfter = new Region();
        topMenuSpaceAfter.setPrefSize(75, 75);
        topButtons.getChildren().add(topMenuSpaceAfter);

        Button nextMonth = new Button(">");
        nextMonth.setPrefSize(71, 41);
        nextMonth.setStyle("-fx-background-color: transparent;");
        topButtons.getChildren().add(nextMonth);

        root.getChildren().add(topButtons);


        Line line = new Line(758, -0.4, 537, -0.4);
        line.setStroke(Color.web("#000000"));
        root.getChildren().add(line);


        HBox gridBox = new HBox();
        gridBox.setAlignment(Pos.CENTER);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPrefHeight(40*7);
        grid.setMaxHeight(40*7);



        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints(50);
            cc.setHalignment(HPos.CENTER);
            grid.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < 7; i++) {
            RowConstraints rc = new RowConstraints(50);
            rc.setVgrow(Priority.SOMETIMES);
            grid.getRowConstraints().add(rc);
        }

        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < 7; i++) {
            Text dayText = new Text(days[i]);
            dayText.setFill(Color.web("#282933"));
            grid.getChildren().add(dayText);
            GridPane.setColumnIndex(dayText, i);
            GridPane.setRowIndex(dayText, 0);
        }

        populateCalendar(ym, grid);

        gridBox.getChildren().add(grid);


        root.getChildren().add(gridBox);


        return root;
    }

    public String populateYearMonth(YearMonth ym) {
        String month;
        String year;
        month = ym.getMonth().toString();
        year = String.valueOf(ym.getYear());
        return month + " " + year;


    }

    public void populateCalendar(YearMonth ym, GridPane grid) {
        int monthLength = ym.lengthOfMonth();
        int firstDay = ym.atDay(1).getDayOfWeek().getValue();
        int startingCol = firstDay % 7;
        int day = 1;

        for (int row = 1; row <= 7; row++) {
            for (int col = 0; col <= 6; col++) {
                Button btn = new Button();
                btn.setPrefSize(34, 30);
                btn.setStyle("-fx-background-color: #f9f9f9;");

                if (row == 1 && col < startingCol) {
                    btn.setText("");
                    btn.setDisable(true);
                    btn.setOpacity(0);
                } else if (day <= monthLength) {
                    btn.setText(String.valueOf(day));
                    day++;
                } else {
                    btn.setText("");
                    btn.setDisable(true);
                    btn.setOpacity(0);
                }
                grid.getChildren().add(btn);
                GridPane.setColumnIndex(btn, col);
                GridPane.setRowIndex(btn, row);
                btn.setId("button");
                btn.setStyle("-fx-background-radius: 10; -fx-background-color: #FFFFFF; -fx-border-radius: 10; -fx-border-color: #000000;");

            }
        }
    }
}
