package com.example;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class App extends Application {

    // ---------- DATA ----------
    private final ObservableList<Task> allTasks = FXCollections.observableArrayList();
    private final FilteredList<Task> filteredTasks =
            new FilteredList<>(allTasks, t -> true);   // wraps allTasks

    // ---------- UI FIELDS ----------
    private BorderPane root;
    private Stage addStage;

    private ListView<Task> taskListView;
    private ListView<SubTask> subTaskListView;

    private TextField taskTitleField;
    private TextArea taskDescriptionArea;
    private DatePicker taskDueDatePicker;
    private ComboBox<String> categoryCombo;
    private Button exitButton;
    private Parent newWindow;

    private TextField subTaskField;

    private ComboBox<String> sortMenu;
    private ComboBox<String> filterMenu;
    private Button confirmTaskCreationButton;

    private ProgressBar progressBar;
    private Label progressLabel;

    private ToggleGroup viewToggleGroup;

    private Stage primaryStage;
    private Scene homeScene;
    private Scene mainScene;

    // filters we combine: date + category
    private Predicate<Task> dateFilter = t -> true;
    private Predicate<Task> categoryFilter = t -> true;

    public static void main(String[] args) {
        launch(args);
    }

    // apply both filters together
    private void updateFilters() {
        filteredTasks.setPredicate(task ->
                dateFilter.test(task) && categoryFilter.test(task));
    }

    @Override
public void start(Stage primaryStage) {
    this.primaryStage = primaryStage;
    primaryStage.setTitle("Task Tracker");

    // ----- MAIN UI -----
    root = new BorderPane();

    VBox leftPane = createFilterPane();
    VBox centerPane = createTaskListPane(primaryStage);

    root.setLeft(leftPane);
    root.setCenter(centerPane);

    mainScene = new Scene(root, 1400, 900);
    mainScene.getStylesheets().add(
            Objects.requireNonNull(
                    getClass().getResource("/com/example/style.css")
            ).toExternalForm()
    );

    // ----- HOME PAGE -----
    Parent homeRoot = createHomePage();
    homeScene = new Scene(homeRoot, 1400, 900);
    homeScene.getStylesheets().add(
            Objects.requireNonNull(
                    getClass().getResource("/com/example/style.css")
            ).toExternalForm()
    );

    // show HOME first
    primaryStage.setScene(homeScene);
    primaryStage.show();
}

private Parent createHomePage() {
    // StackPane = background + centered content
    StackPane root = new StackPane();
    root.setId("pane");  // uses #pane from style.css (your image)

    VBox box = new VBox(20);
    box.setAlignment(Pos.CENTER);

    Label title = new Label("TaskTracker");
    title.setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");

    Label subtitle = new Label("Organize your day with ease.");
    subtitle.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

    Button enterButton = new Button("Press here to enter");
    enterButton.setStyle("-fx-font-size: 22px; -fx-padding: 10 20 10 20;");
    enterButton.setOnAction(e -> primaryStage.setScene(mainScene));

    Button exitButton = new Button("Exit");
    exitButton.setOnAction(e -> primaryStage.close());

    box.getChildren().addAll(title, subtitle, enterButton, exitButton);
    root.getChildren().add(box);

    return root;
}

    // -------------------- LEFT: Filters and Sidebar --------------------

    private VBox createFilterPane() {
        VBox box = new VBox();
        box.setPadding(new Insets(15));
        box.setId("pane");
        box.setFillWidth(true);
        box.getStylesheets().addAll(
                this.getClass().getResource("/com/example/style.css").toExternalForm()
        );
        box.setAlignment(Pos.TOP_CENTER);
        box.setPrefWidth(280);
        box.setMinWidth(280);
        box.setMaxWidth(280);

        // --- Account button + menu ---
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

        MenuButton mnuAccount = new MenuButton();
        mnuAccount.setId("accountMenu");
        mnuAccount.setText("My Account");
        mnuAccount.getItems().add(new MenuItem("App Color Scheme"));
        mnuAccount.getItems().add(new MenuItem("Notification Settings"));

        accButtonBox.getChildren().add(mnuAccount);
        box.getChildren().add(accButtonBox);

        Region region = new Region();
        region.getStyleClass().add("regionLeftMenu");
        box.getChildren().add(region);

        // ---- FILTER BUTTONS (date + category) ----
        VBox filterButtonBox = new VBox(13);
        filterButtonBox.setAlignment(Pos.CENTER);

        // Date filters
        Button todayButton = new Button("Today");
        todayButton.getStyleClass().add("btnLeftMenu");
        todayButton.setOnAction(e -> {
            LocalDate today = LocalDate.now();
            dateFilter = task -> today.equals(task.getDueDate());
            updateFilters();
        });

        Button sevenButton = new Button("7-day");
        sevenButton.getStyleClass().add("btnLeftMenu");
        sevenButton.setOnAction(e -> showNextSevenDaysTasks());

        Button monthButton = new Button("Month");
        monthButton.getStyleClass().add("btnLeftMenu");
        // TODO: add month filter (set dateFilter then updateFilters)

        Button completedButton = new Button("Completed");
        completedButton.getStyleClass().add("btnLeftMenu");
        // TODO: add completed filter (once tasks have a "completed" flag)

        filterButtonBox.getChildren().addAll(todayButton, sevenButton, monthButton, completedButton);

        Separator btnSeparator = new Separator();
        btnSeparator.setId("btnSeparator");
        filterButtonBox.getChildren().add(btnSeparator);

        // Category filters
        String[] btnNames = new String[]{"Work", "School", "Home", "Other"};
        for (String btnName : btnNames) {
            Button newButton = new Button(btnName);
            newButton.getStyleClass().add("btnLeftMenu");

            newButton.setOnAction(e -> {
                // set categoryFilter based on which button was clicked
                categoryFilter = task -> btnName.equalsIgnoreCase(task.getCategory());
                updateFilters();
            });

            filterButtonBox.getChildren().add(newButton);
        }

        box.getChildren().add(filterButtonBox);
        return box;
    }

    // -------------------- POPUP: Add New Task --------------------
    private VBox addTaskPopup() {

        VBox box = new VBox();
        box.setId("addTaskPopup");
        box.getStylesheets().addAll(
                Objects.requireNonNull(this.getClass().getResource("/com/example/style.css")).toExternalForm()
        );
        box.setAlignment(Pos.TOP_CENTER);
        box.setPadding(new Insets(1,0,0,0));
        AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(box, 50.0);
        AnchorPane.setLeftAnchor(box, 50.0);


        // ---------------- Exit Window ----------------
        URL exitIcon = this.getClass().getResource("/com/example/icons/cross.png");
        assert exitIcon != null;
        ImageView exitWindow = new ImageView(new Image(exitIcon.toExternalForm()));
        exitWindow.setPreserveRatio(false);
        exitWindow.setFitWidth(10);
        exitWindow.setFitHeight(10);
        exitButton = new Button();
        exitButton.setId("exitButton");
        AnchorPane.setRightAnchor(exitButton, 1.0);
        AnchorPane.setTopAnchor(exitButton, 1.0);
        exitButton.setGraphic(exitWindow);
        anchorPane.getChildren().addAll(exitButton);
        box.getChildren().add(anchorPane);

        // ---------------- Add Task Form ----------------
        Label lblTitle = new Label("Task Title:");
        taskTitleField = new TextField();
        taskTitleField.setPromptText("Enter task title");

        Label lblDesc = new Label("Description:");
        taskDescriptionArea = new TextArea();
        taskDescriptionArea.setPromptText("Optional description...");
        taskDescriptionArea.setPrefRowCount(3);

        Label lblDue = new Label("Due Date:");
        taskDueDatePicker = new DatePicker();

        Label lblCategory = new Label("Category:");
        categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Work", "School", "Home", "Other");
        categoryCombo.setValue("Other");   // default

        confirmTaskCreationButton = new Button("Add Task");
        confirmTaskCreationButton.setId("addTaskButton");
        confirmTaskCreationButton.setOnAction(e -> handleAddTask());

        Label lblSubtask = new Label("Subtask:");
        Button subtaskButton = new Button("+ Subtask");
        VBox subtaskContainer = new VBox(8, subtaskButton);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setId("subtaskScrollPane");
        subtaskContainer.setId("subtaskContainer");

        subtaskButton.setOnAction(e -> {
            int addSubtask = subtaskContainer.getChildren().indexOf(subtaskButton);
            subtaskContainer.getChildren().add(addSubtask, new TextField());
        });


        VBox addTaskBox = new VBox(
                8,
                lblTitle, taskTitleField,
                lblDesc, taskDescriptionArea,
                lblSubtask, subtaskContainer,
                lblDue, taskDueDatePicker,
                lblCategory, categoryCombo,
                confirmTaskCreationButton
        );
        addTaskBox.setId("addTaskBox");

        scrollPane.setContent(addTaskBox);

        box.getChildren().add(scrollPane);

        return box;

        // TODO: Style this window
    }




    private void showNextSevenDaysTasks() {
        LocalDate today = LocalDate.now();
        LocalDate week = today.plusDays(7);

        dateFilter = task -> {
            LocalDate due = task.getDueDate();
            if (due == null) return false;
            return !due.isBefore(today) && !due.isAfter(week);
        };

        updateFilters();
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

        String category = (categoryCombo != null) ? categoryCombo.getValue() : null;
        if (category == null || category.isEmpty()) {
            category = "Other";
        }

        Task task = new Task(title, description, dueDate, category);

        // add to backing list (ListView updates via filteredTasks)
        allTasks.add(task);
        closePopUp(addStage);

        // clear form
        taskTitleField.clear();
        taskDescriptionArea.clear();
        taskDueDatePicker.setValue(null);
        if (categoryCombo != null) {
            categoryCombo.setValue("Other");
        }
        // TODO: Save tasks to file
    }


    // -------------------- CENTER: Task List & Calendar --------------------

    private VBox createTaskListPane(Stage primaryStage) {
        VBox topBar = new VBox();
        topBar.getStylesheets().addAll(
                Objects.requireNonNull(this.getClass().getResource("/com/example/style.css")).toExternalForm()
        );
        topBar.setId("topBar");

        AnchorPane topPane = new AnchorPane();
        topPane.setId("topPane");
        topBar.getChildren().add(topPane);

        Search search = new Search();
        HBox hBox = new HBox(search.createSearch());
        hBox.setAlignment(Pos.CENTER);
        topPane.getChildren().add(hBox);

        AnchorPane.setTopAnchor(hBox, 10.0);
        AnchorPane.setLeftAnchor(hBox, 10.0);
        AnchorPane.setRightAnchor(hBox, 10.0);
        AnchorPane.setBottomAnchor(hBox, 10.0);

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
        splitView.getRowConstraints().add(rowConstraints);

        Separator separator = new Separator();
        separator.setId("separator");
        separator.setOrientation(Orientation.VERTICAL);

        // LEFT: task list area
        BorderPane leftPane = new BorderPane();
        StackPane leftStack = new StackPane();
        leftStack.setId("leftStack");
        leftPane.setCenter(leftStack);

        taskListView = new ListView<>();
        taskListView.setItems(filteredTasks);
        taskListView.setPrefHeight(Double.MAX_VALUE);

        VBox leftPaneContainer = new VBox(10);
        leftPaneContainer.setId("leftButtonContainer");
        leftPaneContainer.setAlignment(Pos.TOP_CENTER);

        Button taskCreationButton = new Button();
        taskCreationButton.getStyleClass().add("leftPaneButtons");
        taskCreationButton.setText("Add Task +");


        // Add Task, Edit, Sort, Filter left pane button box
        HBox buttonContainer = new HBox();
        buttonContainer.setId("buttonContainer");
        buttonContainer.setAlignment(Pos.TOP_CENTER);
        buttonContainer.spacingProperty().bind(
                leftStack.widthProperty().multiply(0.05)
        );

        Button editTask = new Button();
        editTask.getStyleClass().add("leftPaneButtons");
        editTask.setText("Edit");

        sortMenu = new ComboBox<>();
        sortMenu.getStyleClass().addAll("leftPaneButtons", "comboBox");
        sortMenu.getItems().addAll("Priority: High to Low","Priority: Low to High", "Due Date",
                "Task Name: A to Z","Task Name: Z to A");
        sortMenu.setValue("Sort");


        filterMenu = new ComboBox<>();
        filterMenu.getStyleClass().addAll("leftPaneButtons", "comboBox");
        filterMenu.getItems().addAll("High Priority", "Mid Priority", "Low Priority", "Work List", "School List",
                "Home List", "Other List", "Due Today", "Due This Week", "Due This Month", "Completed Tasks");
        filterMenu.setValue("Filter");

        buttonContainer.getChildren().addAll(taskCreationButton, editTask,  sortMenu, filterMenu);



        taskCreationButton.setOnMouseClicked(event -> {
            newWindow = addTaskPopup();
            handlePopup(primaryStage);
        });


        ScrollPane leftScrollPane = new ScrollPane();
        leftScrollPane.setContent(taskListView);
        leftScrollPane.setFitToWidth(true);
        leftScrollPane.setFitToHeight(true);
        leftScrollPane.setId("leftScrollPane");
        leftScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        leftScrollPane.setMinSize(300, 500);
        leftScrollPane.setMaxSize(575, 1050);

        leftPaneContainer.getChildren().addAll(buttonContainer, leftScrollPane);
        leftStack.getChildren().add(leftPaneContainer);

        leftStack.setMinSize(400, 350);
        leftStack.setPrefSize(400, 350);
        leftStack.setMaxSize(600, 1100);
        leftStack.setPadding(new Insets(10, 10, 10, 10));


        // RIGHT: calendar area
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

        VBox rightVBox = new VBox();
        rightVBox.setId("rightVBox");
        rightVBox.setAlignment(Pos.TOP_CENTER);

        Calendar calendar = new Calendar();
        rightVBox.getChildren().add(calendar.createCalendar());
        rightStack.getChildren().add(rightVBox);
        rightStack.setMinSize(400, 350);
        rightStack.setPrefSize(400, 350);
        rightStack.setMaxSize(600, 1100);
        rightStack.setPadding(new Insets(30, 20, 0, 20));

        topBar.getChildren().add(splitView);

        GridPane.setHalignment(separator, HPos.CENTER);
        GridPane.setValignment(separator, VPos.CENTER);
        VBox.setVgrow(splitView, Priority.ALWAYS);

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

    // Create a popup window
    private void handlePopup(Stage mainStage) {
        addStage = new Stage();
        Scene addScene = new Scene(newWindow);
        addStage.setScene(addScene);
        addStage.setResizable(false);
        addStage.initOwner(mainStage);
        root.setDisable(true);
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setBrightness(-.5);
        root.setEffect(colorAdjust);
        addStage.initStyle(StageStyle.UNDECORATED);
        exitButton.setOnAction(event -> {
            closePopUp(addStage);
        });
        addStage.showAndWait();

    }

    // close a popup window
    public void closePopUp(Stage addStage) {
            addStage.close();
            root.setDisable(false);
            root.setEffect(null);
    }

}
