package org.msolutions.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import org.msolutions.DTO.Event;
import org.msolutions.PageLoader;
import org.msolutions.service.EventsManager;

import java.util.Optional;

public class DeleteEventController extends PageLoader {
    @FXML
    ListView<Event> eventsListView;
    private ObservableList<Event> eventsList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        eventsList.setAll(EventsManager.getAllEvents());
        eventsListView.setItems(eventsList);
    }

    @FXML
    public void deleteEvent() {
        Event selectedItem = eventsListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && deleteConfirmation(selectedItem.toString())) {
            EventsManager.deleteEvent(selectedItem.getId());
            refreshEventsList();
        }
    }

    private boolean deleteConfirmation(String event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Внимание!");
        alert.setHeaderText("Удалить событие «" + event + "» ?");
        alert.setContentText("При удалении события удалятся все связанные с ним изображения и видео. Вы действительно хотите удалить это событие?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void refreshEventsList() {
        eventsList.clear();
        eventsList.setAll(EventsManager.getAllEvents());
    }
}
