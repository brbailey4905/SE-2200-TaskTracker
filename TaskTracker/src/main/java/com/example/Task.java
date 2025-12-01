package com.example;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class Task {
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dueDate = new SimpleObjectProperty<>();

    private final ObservableList<SubTask> subTasks = FXCollections.observableArrayList();

    public Task(String title, String description, LocalDate dueDate) {
        this.title.set(title);
        this.description.set(description);
        this.dueDate.set(dueDate);
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public String getDescription() {
        return description.get();
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public LocalDate getDueDate() {
        return dueDate.get();
    }

    public ObjectProperty<LocalDate> dueDateProperty() {
        return dueDate;
    }

    public ObservableList<SubTask> getSubTasks() {
        return subTasks;
    }

    /**
     * Completion rate:
     * - If there are subtasks, percent of completed subtasks.
     * - If no subtasks, 0 (you could adapt this to include a "completed" flag for whole tasks if you want).
     */
    public double getCompletionRate() {
        if (subTasks.isEmpty()) {
            return 0.0;
        }
        long done = subTasks.stream().filter(SubTask::isCompleted).count();
        return (double) done / subTasks.size();
    }
}
