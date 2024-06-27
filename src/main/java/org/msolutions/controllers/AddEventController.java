package org.msolutions.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.msolutions.DTO.Event;
import org.msolutions.DTO.TimeInterval;
import org.msolutions.PageLoader;
import org.msolutions.service.EventsManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class AddEventController extends PageLoader {
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField eventName;

    @FXML private VBox intervalPane;
    @FXML private ToggleGroup group;
    @FXML private RadioButton radioButton;

    @FXML private ComboBox<String> startTime;
    @FXML private ComboBox<String> endTime;

    @FXML private ListView<TimeInterval> intervalsListView;

    ObservableList<TimeInterval> intervalsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        group.selectToggle(radioButton);

        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                String timeString = String.format("%02d:%02d", hour, minute);
                startTime.getItems().add(timeString);
                endTime.getItems().add(timeString);
            }
        }

        group.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            RadioButton selectedRadioButton = (RadioButton) newValue;

            if (selectedRadioButton == radioButton) {
                intervalPane.setDisable(true);
            } else {
                intervalPane.setDisable(false);
            }
        });
    }

    @FXML
    private void addTimeInterval() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        String stringStartTime = startTime.getSelectionModel().getSelectedItem();
        String stringEndTime = endTime.getSelectionModel().getSelectedItem();

        if (stringStartTime == null) {
            invalidInputNotification("Время начала события не введено!");
        } else if (stringEndTime == null) {
            invalidInputNotification("Время окончания события не введено!");
        } else {
            LocalTime start = LocalTime.parse(stringStartTime, formatter);
            LocalTime end = LocalTime.parse(stringEndTime, formatter);

            if (!start.isBefore(end)) {
                invalidInputNotification("Время начала должно быть раньше, чем время окончания!");
            } else {
                TimeInterval timeInterval = new TimeInterval(start, end);

                boolean isOverlap = false;
                for (TimeInterval interval : intervalsList) {
                    if (timeInterval.isOverlap(interval)) {
                        invalidInputNotification("Интервалы не должны перекрывать друг друга!");
                        isOverlap = true;
                        break;
                    }
                }

                if (!isOverlap) {
                    intervalsList.add(timeInterval);
                    refreshIntervalsList();
                }
            }
        }
    }

    @FXML
    public void deleteTimeInterval() {
        if (intervalsListView.getSelectionModel().getSelectedItem() != null) {
            TimeInterval selectedItem = intervalsListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                intervalsList.remove(selectedItem);
                refreshIntervalsList();
            }
        }
    }

    @FXML
    public void addEvent(ActionEvent event) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        String name = eventName.getText();

        LocalDate today = LocalDate.now();

        if (startDate == null) {
            invalidInputNotification("Дата начала события введена неверно!");

        } else if (endDate == null) {
            invalidInputNotification("Дата окончания события введена неверно!");

        } else if (endDate.isBefore(today)) {
            invalidInputNotification("Дата окончания события должна быть сегодня или позже!");

        } else if (!startDate.isBefore(endDate) && !startDate.equals(endDate)) {
            invalidInputNotification("Дата начала события должна быть не позже, чем дата окончания!");

        } else if (name.isEmpty() || name.trim().isEmpty()) {
            invalidInputNotification("Название события введено неверно!");

        } else if (!intervalPane.isDisabled() && intervalsList.size() == 0) {
            invalidInputNotification("Вы не выбрали временной интервал");
        } else {
            EventsManager.addEvent(new Event(name, startDate, endDate, intervalsList));

            startDatePicker.setValue(null);
            endDatePicker.setValue(null);
            eventName.clear();
            intervalsList.clear();
            refreshIntervalsList();
            startTime.setValue(null);
            endTime. setValue(null);
            group.selectToggle(radioButton);
        }
    }

    private void invalidInputNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void refreshIntervalsList() {
        intervalsListView.setItems(FXCollections.observableArrayList(intervalsList)); // обновление
    }
}
