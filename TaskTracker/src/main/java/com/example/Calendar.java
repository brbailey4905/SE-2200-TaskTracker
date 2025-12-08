package com.example;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.time.YearMonth;

public class Calendar {
    private YearMonth ym = YearMonth.now();
    private Text monthText;
    private GridPane grid;

    public VBox createCalendar() {
        VBox root = new VBox();

        root.getStylesheets().addAll(
                this.getClass().getResource("/com/example/style.css").toExternalForm()
        );
        root.setAlignment(Pos.TOP_CENTER);
        root.setPrefSize(400, 350);
        root.setMinSize(350, 300);
        root.setMaxSize(550, 500);
        root.setId("calRoot");

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

        monthText = new Text(populateYearMonth());
        topButtons.getChildren().add(monthText);

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
        line.setId("line");
        root.getChildren().add(line);


        HBox gridBox = new HBox();
        gridBox.setAlignment(Pos.CENTER);

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setPrefHeight(Double.MAX_VALUE);
        grid.setMaxHeight(Double.MAX_VALUE);



        for (int i = 0; i < 7; i++) {
            ColumnConstraints cc = new ColumnConstraints(50);
            cc.setHalignment(HPos.CENTER);
            grid.getColumnConstraints().add(cc);
        }

        for (int i = 0; i < 7; i++) {
            RowConstraints rc = new RowConstraints(45);
            rc.setVgrow(Priority.SOMETIMES);
            grid.getRowConstraints().add(rc);
        }



        gridBox.getChildren().add(grid);

        populateCalendar();

        previousMonth.setOnAction(e -> {
            ym = ym.minusMonths(1);
            updateCalendar();
        });

        nextMonth.setOnAction(e -> {
            ym = ym.plusMonths(1);
            updateCalendar();
        });



        root.getChildren().add(gridBox);


        return root;
    }

    private void updateCalendar() {
        monthText.setText(populateYearMonth());
        grid.getChildren().clear();
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};
        for (int i = 0; i < 7; i++) {
            Text dayText = new Text(days[i]);
            grid.getChildren().add(dayText);
            GridPane.setColumnIndex(dayText, i);
            GridPane.setRowIndex(dayText, 0);
            dayText.setId("dayText");
        }
        populateCalendar();
    }

    private String populateYearMonth() {
        return ym.getMonth() + " " + ym.getYear();


    }

    private void populateCalendar() {
        int monthLength = ym.lengthOfMonth();
        int firstDay = ym.atDay(1).getDayOfWeek().getValue();
        int startingCol = firstDay % 7;
        int day = 1;
        int totalCells = startingCol + monthLength;
        int totalRows = (int) Math.ceil(totalCells / 7.0);

        for (int row = 1; row <= totalRows; row++) {
            for (int col = 0; col <= 6; col++) {
                Button btn = new Button();
                btn.setPrefSize(37, 34);

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
                btn.setId("btnDay");

            }
        }
    }
}