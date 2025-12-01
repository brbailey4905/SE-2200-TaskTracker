package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;

public class App extends Application {

    private final ObservableList<Task> allTasks = FXCollections.observableArrayList();

    private ListView<Task> taskListView;
    private ListView<SubTask> subTaskListView;

    private TextField taskTitleField;
    private TextArea taskDescriptionArea;
    private DatePicker taskDueDatePicker;

    private TextField subTaskField;

    private ProgressBar progressBar;
    private Label progressLabel;

    private ToggleGroup viewToggleGroup;



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Task Tracker");

        BorderPane root = new BorderPane();

        // --- Left: Add Task Form ---
        VBox leftPane = createAddTaskPane();

        // --- Center: Task List + Filter ---
        VBox centerPane = createTaskListPane();

        // --- Right: Task Details / Subtasks ---

        root.setLeft(leftPane);
        root.setCenter(centerPane);


        Scene scene = new Scene(root, 1400, 900);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // -------------------- LEFT: Add Task --------------------

    private VBox createAddTaskPane() {
        VBox box = new VBox();
        box.setPadding(new Insets(15));
        box.setId("pane");
        box.setFillWidth(true);
        box.getStylesheets().addAll(this.getClass().getResource("/com/example/style.css").toExternalForm());
        box.setAlignment(Pos.TOP_CENTER);

        HBox accButtonBox = new HBox();
        accButtonBox.setSpacing(10);
        accButtonBox.setAlignment(Pos.CENTER_LEFT);

        Button accountButton = new Button();
        accountButton.setId("accountButton");
            URL userIcon = this.getClass().getResource("/com/example/icons/user.png");
            ImageView userImage = new ImageView(new Image(userIcon.toExternalForm()));
            userImage.setFitWidth(30);
            userImage.setPreserveRatio(true);
            accountButton.setGraphic(userImage);



        accButtonBox.getChildren().add(accountButton);

        VBox filterButtonBox = new VBox(13);
        filterButtonBox.setAlignment(Pos.CENTER);
        MenuButton mnuAccount = new MenuButton();
        mnuAccount.setId("accountMenu");
        mnuAccount.setText("My Account");
        mnuAccount.getItems().add(new MenuItem("App Color Scheme"));
        mnuAccount.getItems().add(new MenuItem("Notification Settings"));

        Region region = new Region();
        accButtonBox.getChildren().add(mnuAccount);
        box.getChildren().add(accButtonBox);
        region.getStyleClass().add("regionLeftMenu");
        box.getChildren().add(region);


        String[] btnNames = new String[]{"Today", "7-day", "Month", "Completed"};
        for (String btnName : btnNames) {
            Button newButton = new Button(btnName);
            newButton.getStyleClass().add("btnLeftMenu");
            filterButtonBox.getChildren().add(newButton);
        }

        Separator btnSeparator = new Separator();
        btnSeparator.setId("btnSeparator");
        filterButtonBox.getChildren().add(btnSeparator);

        btnNames = new String[]{"Work", "School", "Home", "Other"};
        for (String btnName : btnNames) {
            Button newButton = new Button(btnName);
            newButton.getStyleClass().add("btnLeftMenu");
            filterButtonBox.getChildren().add(newButton);
        }

        box.getChildren().add(filterButtonBox);
        return box;
    }

    private void handleAddTask() {
        String title = taskTitleField.getText().trim();
        String description = taskDescriptionArea.getText().trim();
        LocalDate dueDate = taskDueDatePicker.getValue();

        if (title.isEmpty() || dueDate == null) {
            showAlert(Alert.AlertType.WARNING,
                    "Missing data",
                    "Please enter a title and select a due date.");
            return;
        }

        Task task = new Task(title, description, dueDate);
        allTasks.add(task);

        // Clear fields
        taskTitleField.clear();
        taskDescriptionArea.clear();
        taskDueDatePicker.setValue(null);


    }

    // -------------------- CENTER: Task List & View Toggle --------------------

    private VBox createTaskListPane() {
        VBox topBar = new VBox();
        topBar.getStylesheets().addAll(this.getClass().getResource("/com/example/style.css").toExternalForm());
        topBar.setId("topBar");
        AnchorPane topPane = new AnchorPane();
        topPane.setId("topPane");
        topBar.getChildren().add(topPane);
        Search search = new Search();
        HBox hBox = new HBox(search.createSearch());
        hBox.setAlignment(Pos.CENTER);
        topPane.getChildren().add(hBox);

        topPane.setTopAnchor(hBox, 10.0);
        topPane.setLeftAnchor(hBox, 10.0);
        topPane.setRightAnchor(hBox, 10.0);
        topPane.setBottomAnchor(hBox, 10.0);


        GridPane splitView = new GridPane();
        splitView.setId("splitView");

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.ALWAYS);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.ALWAYS);
        column2.setPercentWidth(5);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setHgrow(Priority.ALWAYS);

        RowConstraints rowConstraints = new RowConstraints();
        rowConstraints.setVgrow(Priority.ALWAYS);

        splitView.getColumnConstraints().addAll(column1, column2, column3);
        splitView.setAlignment(Pos.CENTER);
        splitView.getRowConstraints().addAll(rowConstraints);

        Separator separator = new Separator();
        separator.setId("separator");
        separator.setOrientation(Orientation.VERTICAL);

        BorderPane leftPane = new BorderPane();
        StackPane leftStack = new StackPane();
        leftStack.setId("leftStack");
        leftPane.setCenter(leftStack);

        BorderPane rightPane = new BorderPane();
        StackPane rightStack = new StackPane();
        rightStack.setId("rightStack");
        rightPane.setCenter(rightStack);


        splitView.add(leftPane, 0, 0);
        splitView.add(separator, 1, 0);
        splitView.add(rightPane, 2, 0);

        leftPane.setTop(new Region());
        leftPane.getTop().getStyleClass().add("region");
        leftPane.setRight(new Region());
        leftPane.getRight().getStyleClass().add("region");
        leftPane.setLeft(new Region());
        leftPane.getLeft().getStyleClass().add("region");
        leftPane.setBottom(new Region());
        leftPane.getBottom().getStyleClass().add("region");

        rightPane.setTop(new Region());
        rightPane.getTop().getStyleClass().add("region");
        rightPane.setRight(new Region());
        rightPane.getRight().getStyleClass().add("region");
        rightPane.setLeft(new Region());
        rightPane.getLeft().getStyleClass().add("region");
        rightPane.setBottom(new Region());
        rightPane.getBottom().getStyleClass().add("region");

        topBar.getChildren().add(splitView);

        GridPane.setHalignment(separator, HPos.CENTER);
        GridPane.setValignment(separator, VPos.CENTER);
        VBox.setVgrow(splitView, Priority.ALWAYS);
        ScrollPane leftScrollPane = new ScrollPane();
        leftScrollPane.setId("leftScrollPane");
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftStack.getChildren().add(leftScrollPane);
        leftStack.setMinSize(400, 350);
        leftStack.setPrefSize(400, 350);
        leftStack.setMaxSize(600, 1100);

        VBox rightVBox = new VBox();
        rightVBox.setId("rightVBox");
        rightVBox.setAlignment(Pos.TOP_CENTER);

        leftStack.setPadding(new Insets(50, 10, 10, 10));
        Calendar calendar = new Calendar();
        rightVBox.getChildren().add(calendar.createCalendar());
        rightStack.getChildren().add(rightVBox);
        rightStack.setMinSize(400, 350);
        rightStack.setPrefSize(400, 350);
        rightStack.setMaxSize(600, 1100);
        rightStack.setPadding(new Insets(30, 20,0,20));




        return topBar;
    }

    // -------------------- UTILITY --------------------

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
