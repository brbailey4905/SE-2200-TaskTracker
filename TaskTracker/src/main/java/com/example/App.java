package com.example;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.Collectors;

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
        root.setPadding(new Insets(10));

        // --- Left: Add Task Form ---
        VBox leftPane = createAddTaskPane();

        // --- Center: Task List + Filter ---
        VBox centerPane = createTaskListPane();

        // --- Right: Task Details / Subtasks ---
        VBox rightPane = createTaskDetailsPane();

        root.setLeft(leftPane);
        root.setCenter(centerPane);
        root.setRight(rightPane);

        Scene scene = new Scene(root, 1100, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // -------------------- LEFT: Add Task --------------------

    private VBox createAddTaskPane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(300);
        box.setStyle("-fx-background-color: #f4f4f4;");

        Label header = new Label("Add New Task");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        taskTitleField = new TextField();
        taskTitleField.setPromptText("Task title");

        taskDescriptionArea = new TextArea();
        taskDescriptionArea.setPromptText("Task description");
        taskDescriptionArea.setPrefRowCount(4);

        taskDueDatePicker = new DatePicker();
        taskDueDatePicker.setPromptText("Due date");

        Button addTaskButton = new Button("Add Task");
        addTaskButton.setMaxWidth(Double.MAX_VALUE);
        addTaskButton.setOnAction(e -> handleAddTask());

        box.getChildren().addAll(
                header,
                new Label("Title:"),
                taskTitleField,
                new Label("Description:"),
                taskDescriptionArea,
                new Label("Due date:"),
                taskDueDatePicker,
                addTaskButton
        );

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

        refreshTaskList();
    }

    // -------------------- CENTER: Task List & View Toggle --------------------

    private VBox createTaskListPane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        Label header = new Label("Tasks");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // View toggle: Day / Week / Month / All
        HBox viewToggleBox = new HBox(10);
        viewToggleBox.setAlignment(Pos.CENTER_LEFT);

        viewToggleGroup = new ToggleGroup();

        RadioButton dayView = new RadioButton("Day");
        dayView.setToggleGroup(viewToggleGroup);

        RadioButton weekView = new RadioButton("Week");
        weekView.setToggleGroup(viewToggleGroup);

        RadioButton monthView = new RadioButton("Month");
        monthView.setToggleGroup(viewToggleGroup);

        RadioButton allView = new RadioButton("All");
        allView.setToggleGroup(viewToggleGroup);
        allView.setSelected(true);

        viewToggleGroup.selectedToggleProperty().addListener((obs, old, newVal) -> refreshTaskList());

        viewToggleBox.getChildren().addAll(new Label("View:"), dayView, weekView, monthView, allView);

        taskListView = new ListView<>();
        taskListView.setPrefWidth(400);
        taskListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null) {
                    setText(null);
                } else {
                    String due = task.getDueDate() != null ? task.getDueDate().toString() : "No date";
                    int percent = (int) Math.round(task.getCompletionRate() * 100);
                    setText(task.getTitle() + " (Due: " + due + ") - " + percent + "% complete");
                }
            }
        });

        taskListView.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            updateTaskDetails(sel);
        });

        box.getChildren().addAll(header, viewToggleBox, taskListView);

        return box;
    }

    private void refreshTaskList() {
    LocalDate today = LocalDate.now();

    Toggle selectedToggle = viewToggleGroup.getSelectedToggle();
    String mode = "All";
    if (selectedToggle != null) {
        mode = ((RadioButton) selectedToggle).getText();
    }

    LocalDate weekLimit = today.plusDays(7);
    LocalDate monthLimit = today.plusDays(30);

    ObservableList<Task> filtered = FXCollections.observableArrayList();

    for (Task task : allTasks) {
        LocalDate due = task.getDueDate();

        // If no due date, always include
        if (due == null) {
            filtered.add(task);
            continue;
        }

        boolean include = true;

        if ("Day".equals(mode)) {
            include = due.isEqual(today);
        } else if ("Week".equals(mode)) {
            include = !due.isBefore(today) && !due.isAfter(weekLimit);
        } else if ("Month".equals(mode)) {
            include = !due.isBefore(today) && !due.isAfter(monthLimit);
        } else {
            // "All" view
            include = true;
        }

        if (include) {
            filtered.add(task);
        }
    }

    // sort by due date (nulls last)
    FXCollections.sort(filtered,
            Comparator.comparing(Task::getDueDate,
                    Comparator.nullsLast(Comparator.naturalOrder())));

    taskListView.setItems(filtered);

    // keep details panel sane
    Task selected = taskListView.getSelectionModel().getSelectedItem();
    if (selected == null || !filtered.contains(selected)) {
        taskListView.getSelectionModel().clearSelection();
        updateTaskDetails(null);
    }
}


    // -------------------- RIGHT: Task Details + Subtasks --------------------

    private VBox createTaskDetailsPane() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(350);
        box.setStyle("-fx-background-color: #f9f9f9;");

        Label header = new Label("Task Details");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label descLabel = new Label("Subtasks:");
        subTaskListView = new ListView<>();
        subTaskListView.setCellFactory(listView ->
                new CheckBoxListCell<>(SubTask::completedProperty) {
                    @Override
                    public void updateItem(SubTask item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.getName());
                        }
                    }
                }
        );

        // Whenever a subtask completion changes, refresh progress display
        subTaskListView.itemsProperty().addListener((obs, old, val) -> {
            if (val != null) {
                val.forEach(sub ->
                        sub.completedProperty().addListener((o, ov, nv) -> updateTaskDetails(taskListView.getSelectionModel().getSelectedItem()))
                );
            }
        });

        HBox addSubTaskBox = new HBox(5);
        subTaskField = new TextField();
        subTaskField.setPromptText("New subtask name");
        Button addSubTaskButton = new Button("Add");
        addSubTaskButton.setOnAction(e -> handleAddSubTask());
        addSubTaskBox.getChildren().addAll(subTaskField, addSubTaskButton);

        Label progressTextLabel = new Label("Completion:");
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(250);

        progressLabel = new Label("0%");

        VBox progressBox = new VBox(5, progressTextLabel, progressBar, progressLabel);

        box.getChildren().addAll(header, descLabel, subTaskListView, addSubTaskBox, progressBox);
        VBox.setVgrow(subTaskListView, Priority.ALWAYS);

        return box;
    }

    private void handleAddSubTask() {
        Task selectedTask = taskListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert(Alert.AlertType.WARNING, "No task selected", "Please select a task first.");
            return;
        }

        String name = subTaskField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No subtask name", "Please enter a subtask name.");
            return;
        }

        SubTask subTask = new SubTask(name);
        selectedTask.getSubTasks().add(subTask);

        // Re-bind listeners for completion changes
        subTask.completedProperty().addListener((o, ov, nv) -> updateTaskDetails(selectedTask));

        subTaskField.clear();
        updateTaskDetails(selectedTask);
        refreshTaskList();
    }

    private void updateTaskDetails(Task task) {
        if (task == null) {
            subTaskListView.setItems(FXCollections.observableArrayList());
            progressBar.setProgress(0);
            progressLabel.setText("0%");
            return;
        }

        subTaskListView.setItems(task.getSubTasks());

        // Update progress
        double progress = task.getCompletionRate();
        progressBar.setProgress(progress);
        int percent = (int) Math.round(progress * 100);
        progressLabel.setText(percent + "%");

        // Refresh the main task list view text for updated percentage
        taskListView.refresh();
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
