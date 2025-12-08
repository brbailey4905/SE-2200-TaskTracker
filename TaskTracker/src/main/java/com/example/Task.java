package com.example;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task {

    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>();
    private final StringProperty category = new SimpleStringProperty();

    private final ObservableList<SubTask> subTasks =
            FXCollections.observableArrayList();

    // ---- Constructors ----

    public Task(String title, String description, LocalDate dueDate) {
        this(title, description, dueDate, "Other");      // default category
    }

    public Task(String title, String description, LocalDate dueDate, String category) {
        this.title.set(title);
        this.description.set(description);
        this.dueDate.set(dueDate);
        this.category.set(category);
    }

    // ---- Title ----
    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    // ---- Description ----
    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    // ---- Due Date ----
    public LocalDate getDueDate() {
        return dueDate.get();
    }

    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    // ---- Category ----
    public String getCategory() {
        return category.get();
    }

    public void setCategory(String category) {
        this.category.set(category);
    }

    public StringProperty categoryProperty() {
        return category;
    }

    // ---- Subtasks ----
    public ObservableList<SubTask> getSubTasks() {
        return subTasks;
    }

    /**
     * Completion rate:
     * - If there are subtasks, percent of completed subtasks.
     * - If no subtasks, 0.
     */
    public double getCompletionRate() {
        if (subTasks.isEmpty()) {
            return 0.0;
        }
        long done = subTasks.stream().filter(SubTask::isCompleted).count();
        return (double) done / subTasks.size();
    }

    @Override
    public String toString() {
        LocalDate d = getDueDate();
        String dateStr = (d != null) ? d.format(DTF) : "";
        String cat = getCategory();

        String base = (cat == null || cat.isEmpty())
                ? getTitle()
                : "[" + cat + "] " + getTitle();

        return dateStr.isEmpty()
                ? base
                : base + " (Due: " + dateStr + ")";
    }
}

